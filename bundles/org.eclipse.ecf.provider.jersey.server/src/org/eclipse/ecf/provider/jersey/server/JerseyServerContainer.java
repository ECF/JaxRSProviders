/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configuration;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.AbstractJaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.service.http.HttpService;

public class JerseyServerContainer extends JaxRSServerContainer {

	public static final String NAME = "ecf.jaxrs.jersey.server";

	public static class Instantiator extends AbstractJaxRSContainerInstantiator {

		protected Instantiator() {
			super(NAME);
		}

		public static final String URL_CONTEXT_PARAM = "urlContext";
		public static final String URL_CONTEXT_DEFAULT = System
				.getProperty(JerseyServerContainer.class.getName() + ".defaultUrlContext", "http://localhost:8080");
		public static final String ALIAS_PARAM = "alias";
		public static final String ALIAS_PARAM_DEFAULT = "/" + JerseyServerContainer.class.getName();

		@Override
		public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
				Configuration configuration) {
			String urlContext = getParameterValue(parameters, URL_CONTEXT_PARAM, URL_CONTEXT_DEFAULT);
			String alias = getParameterValue(parameters, ALIAS_PARAM, ALIAS_PARAM_DEFAULT);
			return new JerseyServerContainer(urlContext, alias,
					(ResourceConfig) ((configuration instanceof ResourceConfig) ? configuration : null));
		}
	}

	public class JerseyApplication extends Application {
		private Class<?> resourceClass;

		public JerseyApplication(Class<?> resourceClass) {
			this.resourceClass = resourceClass;
		}

		@Override
		public Set<Class<?>> getClasses() {
			Set<Class<?>> results = new HashSet<Class<?>>();
			results.add(this.resourceClass);
			return results;
		}
	}

	private ResourceConfig configuration;

	public JerseyServerContainer(String urlContext, String alias, ResourceConfig configuration) {
		super(urlContext, alias);
		this.configuration = configuration;
	}

	protected ResourceConfig createResourceConfig(IRemoteServiceRegistration registration, Object serviceObject,
			@SuppressWarnings("rawtypes") Dictionary properties) {
		return (this.configuration != null) ? this.configuration
				: ResourceConfig.forApplication(new JerseyApplication(serviceObject.getClass()));
	}

	@Override
	protected Servlet createServlet(IRemoteServiceRegistration registration, Object serviceObject,
			@SuppressWarnings("rawtypes") Dictionary properties) {
		ResourceConfig rc = createResourceConfig(registration, serviceObject, properties);
		return (rc != null) ? new ServletContainer(rc) : new ServletContainer();
	}

	@Override
	protected HttpService getHttpService() {
		return HttpServiceComponent.getHttpService();
	}

}
