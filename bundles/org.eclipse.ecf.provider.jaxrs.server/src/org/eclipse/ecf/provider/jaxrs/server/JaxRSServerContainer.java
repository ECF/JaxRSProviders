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

import java.util.Dictionary;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer.JaxRSServerRemoteServiceContainerAdapter.JaxRSServerRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.AbstractRSAContainer;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter;
import org.eclipse.ecf.remoteservice.RemoteServiceRegistrationImpl;
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

	private final String urlContext;
	private final String alias;
	
	public JaxRSServerContainer(String urlContext, String alias) {
		super(JaxRSNamespace.INSTANCE.createInstance(new Object[] { urlContext + alias }));
		this.urlContext = urlContext;
		this.alias = alias;
	}

	public class JaxRSServerRemoteServiceContainerAdapter extends RSARemoteServiceContainerAdapter {

		public JaxRSServerRemoteServiceContainerAdapter(AbstractRSAContainer container) {
			super(container);
		}
		@Override
		protected RemoteServiceRegistrationImpl createRegistration() {
			return new JaxRSServerRemoteServiceRegistration();
		}
		
		public class JaxRSServerRemoteServiceRegistration extends RSARemoteServiceRegistration {

			private static final long serialVersionUID = 2376479911719219503L;
			
			private String servletAlias;
			
			public String getServletAlias() {
				return this.servletAlias;
			}
			
			public void setServletAlias(String servletAlias) {
				this.servletAlias = servletAlias;
			}
		}
	}
	
	protected RSARemoteServiceContainerAdapter createContainerAdapter() {
		return new JaxRSServerRemoteServiceContainerAdapter(this);
	}

	protected String getAlias() {
		return alias;
	}

	protected String getUrlContext() {
		return urlContext;
	}

	@SuppressWarnings("rawtypes")
	protected Dictionary createServletProperties(JaxRSServerRemoteServiceRegistration registration) {
		return getKeyEndsWithPropertyValue(registration, SERVLET_PROPERTIES_PARAM, Dictionary.class);
	}

	protected HttpContext createServletContext(JaxRSServerRemoteServiceRegistration registration) {
		return getKeyEndsWithPropertyValue(registration, SERVLET_HTTPCONTEXT_PARAM, HttpContext.class);
	}

	protected String createServletAlias(JaxRSServerRemoteServiceRegistration registration) {
		return getAlias();
	}

	protected abstract Servlet createServlet(JaxRSServerRemoteServiceRegistration registration);
	
	@SuppressWarnings("unchecked")
	private <T> T getKeyEndsWithPropertyValue(JaxRSServerRemoteServiceRegistration registration, String keyEndsWith,
			Class<T> valueType) {
		for (String key: registration.getPropertyKeys()) {
				if (key.endsWith(keyEndsWith)) {
					Object v = registration.getProperty(key);
					if (valueType.isInstance(v))
						return (T) v;
				}
		}
		return null;
	}

	protected abstract HttpService getHttpService();

	@Override
	protected Map<String, Object> registerEndpoint(RSARemoteServiceRegistration registration) {
		
		JaxRSServerRemoteServiceRegistration reg = (JaxRSServerRemoteServiceRegistration) registration;
		// Create Servlet Alias
		String servletAlias = createServletAlias(reg);
		// Create Servlet
		Servlet servlet = createServlet(reg);
		// Create servletProps
		@SuppressWarnings("rawtypes")
		Dictionary servletProps = createServletProperties(reg);
		// Create HttpContext
		HttpContext servletContext = createServletContext(reg);

		try {
			getHttpService().registerServlet(servletAlias, servlet, servletProps, servletContext);
		} catch (ServletException | NamespaceException e) {
			throw new RuntimeException("Cannot register servlet with alias=" + getAlias(), e);
		} catch (Exception e) {
			throw new RuntimeException("Unexpected error registering servlet with alias=" + getAlias(), e);
		}

		((JaxRSServerRemoteServiceRegistration) registration).setServletAlias(servletAlias);

		return createExtraProperties(registration);
	}

	protected Map<String, Object> createExtraProperties(RSARemoteServiceRegistration registration) {
		return null;
	}

	@Override
	protected void unregisterEndpoint(RSARemoteServiceRegistration registration) {
		JaxRSServerRemoteServiceRegistration reg = (JaxRSServerRemoteServiceRegistration) registration;
		String servletAlias = reg.getServletAlias();
		if (servletAlias != null) {
			HttpService httpService = getHttpService();
			if (httpService != null)
				httpService.unregister(servletAlias);
		}
	}

}
