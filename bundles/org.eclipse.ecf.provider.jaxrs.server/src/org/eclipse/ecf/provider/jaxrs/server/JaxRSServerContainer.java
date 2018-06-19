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
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.ws.rs.core.Configurable;

import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;
import org.eclipse.ecf.remoteservice.AbstractRSAContainer;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

public abstract class JaxRSServerContainer extends AbstractRSAContainer {

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
	protected RSARemoteServiceRegistration registration;

	public JaxRSServerContainer(BundleContext context, URI uri) {
		super(JaxRSNamespace.INSTANCE.createInstance(new Object[] { uri }));
		this.context = context;
		String path = uri.getPath();
		this.servletPathPrefix = (path == null) ? SLASH : path;
	}

	protected RSARemoteServiceRegistration getRegistration() {
		return this.registration;
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

	protected abstract Servlet createServlet(RSARemoteServiceRegistration registration);

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

	protected HttpService getHttpService() {
		ServiceTracker<IHttpServiceHolder, IHttpServiceHolder> st = new ServiceTracker<IHttpServiceHolder, IHttpServiceHolder>(
				this.context, IHttpServiceHolder.class, null);
		st.open();
		HttpService s = st.getService().getHttpService();
		st.close();
		return s;
	}

	@SuppressWarnings("rawtypes")
	protected Configurable createConfigurable() {
		return null;
	}

	@SuppressWarnings("rawtypes")
	protected Configurable createConfigurable(RSARemoteServiceRegistration registration) {
		Configurable configurable = createConfigurable();
		if (configurable != null)
			configurable.register(registration.getService());
		return configurable;
	}

	protected String getServletAlias(RSARemoteServiceRegistration reg) {
		String servletAliasPrefix = (this.servletPathPrefix == null || "".equals(this.servletPathPrefix)) ? SLASH
				: this.servletPathPrefix;
		if (!servletAliasPrefix.endsWith(SLASH))
			servletAliasPrefix += SLASH;
		return servletAliasPrefix + String.valueOf(reg.getServiceId());
	}

	@Override
	protected Map<String, Object> exportRemoteService(RSARemoteServiceRegistration reg) {

		RSARemoteServiceRegistration registration = getRegistration();
		if (registration != null)
			throw new RuntimeException("JaxRSServerContainer=" + getID().getName()
					+ " cannot has already exported registration=" + getRegistration());
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
		try {
			httpService.registerServlet(servletAlias, servlet, servletProperties, servletContext);
		} catch (ServletException | NamespaceException e) {
			throw new RuntimeException("Cannot register servlet with alias=" + servletPathPrefix, e);
		}
		this.registration = reg;
		return createExtraExportProperties(servletAlias, reg);
	}

	protected String getExportedEndpointId(String servletAlias, RSARemoteServiceRegistration reg) {
		String uri = getURI().toString();
		while (uri.endsWith("/"))
			uri = uri.substring(0, uri.length() - 1);
		return uri + servletAlias;
	}

	protected Map<String, Object> createExtraExportProperties(String servletAlias, RSARemoteServiceRegistration reg) {
		HashMap<String, Object> result = new HashMap<String, Object>();
		result.put(Constants.ENDPOINT_ID, getExportedEndpointId(servletAlias, registration));
		return result;
	}

	@Override
	public void dispose() {
		super.dispose();
		removeRegistration();
	}

	protected void removeRegistration() {
		HttpService httpService = getHttpService();
		if (httpService != null) {
			String servletAlias = getServletAlias(registration);
			try {
				httpService.unregister(servletAlias);
			} catch (Exception e) {
				// XXX log
				e.printStackTrace();
			}
		}
	}

	@Override
	protected void unexportRemoteService(RSARemoteServiceRegistration registration) {
		RSARemoteServiceRegistration existing = getRegistration();
		if (existing != null && (existing.getServiceId() == registration.getServiceId()))
			removeRegistration();
	}

	protected String getPackageName(Object serviceObject) {
		String className = serviceObject.getClass().getName();
		int lastDot = className.lastIndexOf(".");
		return (lastDot > 0) ? className.substring(0, lastDot) : null;
	}
}
