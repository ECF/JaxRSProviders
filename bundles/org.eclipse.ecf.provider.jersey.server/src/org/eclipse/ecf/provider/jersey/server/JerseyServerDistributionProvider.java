/*******************************************************************************
* Copyright (c) 2015, 2016 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Erdal Karaca <erdal.karaca.de@gmail.com> - Bug 499165 
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSExtensionsRegistry;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer.JaxRSServerRemoteServiceContainerAdapter.JaxRSServerRemoteServiceRegistration;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerDistributionProvider;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.http.HttpService;

@Component(immediate = true, service = IRemoteServiceDistributionProvider.class)
public class JerseyServerDistributionProvider extends JaxRSServerDistributionProvider {

	public static final String JERSEY_SERVER_CONFIG_NAME = "ecf.jaxrs.jersey.server";

	public static final String URL_CONTEXT_PARAM = "urlContext";
	public static final String URL_CONTEXT_DEFAULT = System
			.getProperty(JerseyServerContainer.class.getName() + ".defaultUrlContext", "http://localhost:8080");
	public static final String ALIAS_PARAM = "alias";
	public static final String ALIAS_PARAM_DEFAULT = "/org.eclipse.ecf.provider.jersey.server";

	private JaxRSExtensionsRegistry extensionsComponent;

	public JerseyServerDistributionProvider() {
		super();
	}

	@Activate
	public void activate() throws Exception {
		setName(JERSEY_SERVER_CONFIG_NAME);
		setInstantiator(new JaxRSContainerInstantiator(JERSEY_SERVER_CONFIG_NAME) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					Configuration configuration) {
				String urlContext = getParameterValue(parameters, URL_CONTEXT_PARAM, URL_CONTEXT_DEFAULT);
				String alias = getParameterValue(parameters, ALIAS_PARAM, ALIAS_PARAM_DEFAULT);
				return new JerseyServerContainer(urlContext, alias,
						(ResourceConfig) ((configuration instanceof ResourceConfig) ? configuration : null));
			}
		});
		setDescription("Jersey Jax-RS Server Distribution Provider");
		setServer(true);
	}

	@Reference(unbind = "-", cardinality = ReferenceCardinality.MANDATORY)
	public void setExtensionsComponent(JaxRSExtensionsRegistry extensionsComponent) {
		this.extensionsComponent = extensionsComponent;
	}

	public class JerseyServerContainer extends JaxRSServerContainer {

		private ResourceConfig configuration;

		public JerseyServerContainer(String urlContext, String alias, ResourceConfig configuration) {
			super(urlContext, alias);
			this.configuration = configuration;
		}

		protected ResourceConfig createResourceConfig(final RSARemoteServiceRegistration registration) {
			final Class<?> svcClass = registration.getService().getClass();
			if (this.configuration == null) {
				this.configuration = ResourceConfig.forApplication(new Application() {
					@Override
					public Set<Class<?>> getClasses() {
						Set<Class<?>> results = new HashSet<Class<?>>();
						results.add(svcClass);
						return results;
					}
				});
			} else
				this.configuration.registerClasses(svcClass);
			return this.configuration;
		}

		@Override
		protected Servlet createServlet(JaxRSServerRemoteServiceRegistration registration) {
			ResourceConfig rc = createResourceConfig(registration);
			extensionsComponent.bindConfigurable((Configurable<?>) rc);
			return (rc != null) ? new ServletContainer(rc) : new ServletContainer();
		}

		@Override
		protected HttpService getHttpService() {
			List<HttpService> svcs = extensionsComponent.getHttpServices();
			return (svcs == null || svcs.size() == 0) ? null : svcs.get(0);
		}
	}
}
