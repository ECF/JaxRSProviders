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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.ws.rs.core.Configurable;

import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;
import org.eclipse.ecf.remoteservice.AbstractRSAContainer;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;

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
	protected final List<RSARemoteServiceRegistration> exportedRegistrations = new ArrayList<RSARemoteServiceRegistration>();

	protected final String servletAlias;
	protected Servlet servlet;
	@SuppressWarnings("rawtypes")
	protected Dictionary servletProperties;
	protected HttpContext servletContext;

	public JaxRSServerContainer(URI uri) {
		super(JaxRSNamespace.INSTANCE.createInstance(new Object[] { uri }));
		String path = uri.getPath();
		this.servletAlias = (path == null) ? SLASH : path;
	}

	protected List<RSARemoteServiceRegistration> getExportedRegistrations() {
		return exportedRegistrations;
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

	protected abstract HttpService getHttpService();

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

	@SuppressWarnings("rawtypes")
	protected Configurable createConfigurable(List<RSARemoteServiceRegistration> registrations) {
		Configurable result = null;
		for (RSARemoteServiceRegistration reg : registrations) {
			if (result == null)
				result = createConfigurable(reg);
			else
				result.register(reg);
		}
		return result;
	}

	protected abstract void exportRegistration(RSARemoteServiceRegistration registration);

	protected abstract void unexportRegistration(RSARemoteServiceRegistration registration);

	@Override
	protected Map<String, Object> exportRemoteService(RSARemoteServiceRegistration registration) {

		RSARemoteServiceRegistration reg = (RSARemoteServiceRegistration) registration;

		synchronized (this.exportedRegistrations) {
			if (this.servlet == null) {
				// Create Servlet
				this.servlet = createServlet(reg);
				if (this.servlet == null)
					throw new NullPointerException(
							"Servlet is null.  It cannot be null to export Jax RS servlet.   See subclass implementation of JaxRSServerContainer.createServlet()");
				// Create servletProps
				this.servletProperties = createServletProperties(reg);
				// Create HttpContext
				this.servletContext = createServletContext(reg);

				HttpService svc = getHttpService();
				if (svc == null)
					throw new NullPointerException("HttpService instance is null. It cannot to export JaxRS servlet="
							+ servlet + ".  See subclass implementation of JaxRSServerContainer.getHttpService()");

				try {
					svc.registerServlet(this.servletAlias, this.servlet, this.servletProperties, this.servletContext);
				} catch (ServletException | NamespaceException e) {
					throw new RuntimeException("Cannot register servlet with alias=" + servletAlias, e);
				}
			} else
				exportRegistration(reg);

			this.exportedRegistrations.add(reg);

			return createExtraProperties(reg);

		}
	}

	protected Map<String, Object> createExtraProperties(RSARemoteServiceRegistration reg) {
		return null;
	}

	@Override
	public void dispose() {
		synchronized (this.exportedRegistrations) {
			for (Iterator<RSARemoteServiceRegistration> i = this.exportedRegistrations.iterator(); i.hasNext();) {
				RSARemoteServiceRegistration reg = i.next();
				i.remove();
				removeRegistration(reg);
			}
		}
		super.dispose();
	}

	protected void removeRegistration(RSARemoteServiceRegistration registration) {
		if (this.servlet != null) {
			unexportRegistration(registration);
			if (this.exportedRegistrations.size() == 0) {
				HttpService httpService = getHttpService();
				if (httpService != null) {
					try {
						httpService.unregister(servletAlias);
					} catch (Exception e) {
						// log
						e.printStackTrace();
					}
				}
				this.servlet = null;
			}
		}
	}

	@Override
	protected void unexportRemoteService(RSARemoteServiceRegistration registration) {
		synchronized (this.exportedRegistrations) {
			if (this.exportedRegistrations.remove(registration))
				removeRegistration(registration);
		}
	}

}
