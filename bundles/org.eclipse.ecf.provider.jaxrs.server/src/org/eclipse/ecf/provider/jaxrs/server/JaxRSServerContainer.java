/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.server;

import java.net.URI;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.ext.ContextResolver;

import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;
import org.eclipse.ecf.provider.jaxrs.ObjectMapperContextResolver;
import org.eclipse.ecf.remoteservice.AbstractRSAContainer;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

public abstract class JaxRSServerContainer extends AbstractRSAContainer {

	public static final int JACKSON_DEFAULT_PRIORITY = Integer
			.valueOf(System.getProperty(JaxRSServerContainer.class.getName() + ".jacksonPriority", "1"));
	public static final Long HTTPSERVICE_START_TIMEOUT = Long
			.valueOf(System.getProperty(JaxRSServerContainer.class.getName() + ".httpservice.timeout", "30000"));

	public static final String SERVLET_PROPERTIES_PARAM = ".servletProperties"; // expected
																				// value
																				// type=Dictionary
	public static final String SERVLET_HTTPCONTEXT_PARAM = ".servletHttpContext"; // expected
																					// value
																					// type
																					// =
																					// HttpContext

	protected static final String SLASH = "/";
	protected final String servletPathPrefix;
	protected BundleContext context;
	protected final Map<String, RSARemoteServiceRegistration> registrations;

	protected int jacksonPriority = JACKSON_DEFAULT_PRIORITY;

	public JaxRSServerContainer(BundleContext context, URI uri) {
		this(context, uri, JACKSON_DEFAULT_PRIORITY);
	}

	public JaxRSServerContainer(BundleContext context, URI uri, int jacksonPriority) {
		super(JaxRSNamespace.INSTANCE.createInstance(new Object[] { uri }));
		this.context = context;
		String path = uri.getPath();
		this.servletPathPrefix = (path == null) ? SLASH : path;
		this.registrations = new HashMap<String, RSARemoteServiceRegistration>();
		this.jacksonPriority = jacksonPriority;
	}

	protected URI getURI() {
		return ((URIID) getID()).toURI();
	}

	protected String getUrlContext() {
		URI u = getURI();
		return u.getScheme() + "://" + u.getHost() + ":" + u.getPort();
	}

	@SuppressWarnings("rawtypes")
	protected Dictionary createServletProperties(RSARemoteServiceRegistration registration) {
		return getKeyEndsWithPropertyValue(registration, SERVLET_PROPERTIES_PARAM, Dictionary.class);
	}

	protected HttpContext createServletContext(RSARemoteServiceRegistration registration) {
		return getKeyEndsWithPropertyValue(registration, SERVLET_HTTPCONTEXT_PARAM, HttpContext.class);
	}

	protected Servlet createServlet(RSARemoteServiceRegistration registration) {
		Configurable<?> configurable = createConfigurable(registration);
		registerService(configurable, registration);
		registerExtensions(configurable, registration);
		return createServlet(configurable, registration);
	}

	protected abstract Servlet createServlet(Configurable<?> configurable, RSARemoteServiceRegistration registration);

	protected void registerService(Configurable<?> configurable, RSARemoteServiceRegistration registration) {
		configurable.register(registration.getService());
	}

	protected void registerExtensions(Configurable<?> configurable, RSARemoteServiceRegistration registration) {
		configurable.register(new ObjectMapperContextResolver(), ContextResolver.class);
		configurable.register(new JaxRSServerJacksonFeature(registration, jacksonPriority), jacksonPriority);
	}

	@SuppressWarnings("unchecked")
	private <T> T getKeyEndsWithPropertyValue(RSARemoteServiceRegistration registration, String keyEndsWith,
			Class<T> valueType) {
		for (String key : registration.getPropertyKeys()) {
			if (key.endsWith(keyEndsWith)) {
				Object v = registration.getProperty(key);
				if (valueType.isInstance(v))
					return (T) v;
			}
		}
		return null;
	}

	private ServiceTracker<HttpService, HttpService> httpServiceTracker;

	protected HttpService getHttpService() {
		synchronized (this) {
			if (httpServiceTracker == null) {
				this.httpServiceTracker = new ServiceTracker<HttpService, HttpService>(this.context, HttpService.class,
						null);
				this.httpServiceTracker.open();
			}
			HttpService result = null;
			try {
				result = this.httpServiceTracker.waitForService(HTTPSERVICE_START_TIMEOUT);
				if (result == null)
					throw new TimeoutException(
							"Timed out waiting " + String.valueOf(HTTPSERVICE_START_TIMEOUT) + "ms for HttpService");
			} catch (InterruptedException | TimeoutException e) {
				throw new RuntimeException(
						"Could not find instance of HttpService for JaxRSServerContainer.getHttpService()", e);
			}
			return result;
		}
	}

	@SuppressWarnings("rawtypes")
	protected abstract Configurable createConfigurable(RSARemoteServiceRegistration registration);

	protected String getServletAlias(RSARemoteServiceRegistration reg) {
		String servletAliasPrefix = (this.servletPathPrefix == null || "".equals(this.servletPathPrefix)) ? SLASH
				: this.servletPathPrefix;
		if (!servletAliasPrefix.endsWith(SLASH))
			servletAliasPrefix += SLASH;
		return servletAliasPrefix + String.valueOf(reg.getServiceId());
	}

	@Override
	protected Map<String, Object> exportRemoteService(RSARemoteServiceRegistration reg) {
		// Create Servlet
		Servlet servlet = createServlet(reg);
		if (servlet == null)
			throw new NullPointerException(
					"Servlet is null.  It cannot be null to export Jax RS servlet.   See subclass implementation of JaxRSServerContainer.createServlet()");
		// Create servletProps
		@SuppressWarnings("rawtypes")
		Dictionary servletProperties = createServletProperties(reg);
		// Create HttpContext
		HttpContext servletContext = createServletContext(reg);
		// Get servlet alias
		String servletAlias = getServletAlias(reg);
		// Get HttpService instance
		HttpService httpService = getHttpService();
		if (httpService == null)
			throw new NullPointerException("HttpService cannot cannot be null");
		synchronized (this.registrations) {
			try {
				httpService.registerServlet(servletAlias, servlet, servletProperties, servletContext);
			} catch (ServletException | NamespaceException e) {
				throw new RuntimeException("Cannot register servlet with alias=" + servletPathPrefix, e);
			}
			this.registrations.put(servletAlias, reg);
		}
		return createExtraExportProperties(servletAlias, reg);
	}

	protected String getExportedEndpointId(String servletAlias, RSARemoteServiceRegistration reg) {
		URI ourURI = getURI();
		String path = ourURI.getPath();
		// Fix for https://github.com/ECF/JaxRSProviders/issues/10
		String suffix = "";
		// If there is some path then
		if (!"".equals(path) && servletAlias.startsWith(path)) {
			// Then if the servletAlias starts with, then we remove
			suffix = servletAlias.substring(path.length());
		} else
			// otherwise we use the servletAlias unmodified
			suffix = servletAlias;
		return ourURI.toString() + suffix;
	}

	protected Map<String, Object> createExtraExportProperties(String servletAlias, RSARemoteServiceRegistration reg) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put(Constants.ENDPOINT_ID, getExportedEndpointId(servletAlias, reg));
		return result;
	}

	@Override
	public void dispose() {
		synchronized (this.registrations) {
			new ArrayList<RSARemoteServiceRegistration>(this.registrations.values()).forEach(r -> {
				removeRegistration(r);
			});
		}
		synchronized (this) {
			if (this.httpServiceTracker != null) {
				try {
					this.httpServiceTracker.close();
				} catch (Exception e) {
					// ignore
				}
				this.httpServiceTracker = null;
			}
		}
		super.dispose();
	}

	protected void removeRegistration(RSARemoteServiceRegistration registration) {
		final HttpService httpService = getHttpService();
		if (httpService != null) {
			final String servletAlias = getServletAlias(registration);
			if (servletAlias != null) {
				synchronized (this.registrations) {
					this.registrations.remove(servletAlias);
					SafeRunner.run(new ISafeRunnable() {
						@Override
						public void run() throws Exception {
							httpService.unregister(servletAlias);
						}
					});
				}
			}
		}
	}

	@Override
	protected void unexportRemoteService(RSARemoteServiceRegistration registration) {
		removeRegistration(registration);
	}

	protected String getPackageName(Object serviceObject) {
		String className = serviceObject.getClass().getName();
		int lastDot = className.lastIndexOf(".");
		return (lastDot > 0) ? className.substring(0, lastDot) : null;
	}
}
