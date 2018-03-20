/*******************************************************************************
* Copyright (c) 2016 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Erdal Karaca - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import java.net.URI;
import java.util.Map;

import javax.servlet.Servlet;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerDistributionProvider;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.framework.BundleContext;

public class JerseyServerDistributionProvider extends JaxRSServerDistributionProvider {

	public static final String JERSEY_SERVER_CONFIG_NAME = "ecf.jaxrs.jersey.server";

	public static final String URI_PARAM = "uri";
	public static final String URI_DEFAULT = "http://localhost:8080/jersey";

	public JerseyServerDistributionProvider(final BundleContext context) {
		super();
		setName(JERSEY_SERVER_CONFIG_NAME);
		setInstantiator(new JaxRSContainerInstantiator(JERSEY_SERVER_CONFIG_NAME) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					Configuration configuration) throws ContainerCreateException {
				String uriStr = getParameterValue(parameters, URI_PARAM, URI_DEFAULT);
				URI uri = null;
				try {
					uri = new URI(uriStr);
				} catch (Exception e) {
					throw new ContainerCreateException("Cannot create Jersey Server Container because uri=" + uri, e);
				}
				checkOSGIIntents(description, uri, parameters);
				return new JerseyServerContainer(context, uri, (ResourceConfig) configuration);
			}

			@Override
			protected boolean supportsOSGIConfidentialIntent(ContainerTypeDescription description) {
				return true;
			}

			@Override
			protected boolean supportsOSGIPrivateIntent(ContainerTypeDescription description) {
				return true;
			}
		});
		setDescription("Jersey Jax-RS Server Distribution Provider");
		setServer(true);
		addJaxRSComponent(new ObjectMapperContextResolver(), ContextResolver.class);
		addJaxRSComponent(new JacksonFeature(), Feature.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		return new ResourceConfig();
	}

	public class JerseyServerContainer extends JaxRSServerContainer {

		private ResourceConfig originalConfiguration;

		public JerseyServerContainer(BundleContext context, URI uri, ResourceConfig configuration) {
			super(context, uri);
			this.originalConfiguration = configuration;
		}

		@SuppressWarnings("rawtypes")
		@Override
		protected Configurable createConfigurable() {
			return (this.originalConfiguration == null) ? new ResourceConfig()
					: new ResourceConfig(this.originalConfiguration);
		}

		@Override
		protected void exportRegistration(RSARemoteServiceRegistration reg) {
			ServletContainer sc = (ServletContainer) this.servlet;
			if (sc == null)
				throw new NullPointerException("Servlet cannot be null");
			ResourceConfig config = new ResourceConfig(sc.getConfiguration());
			Object svc = reg.getService();
			String className = svc.getClass().getName();
			int lastDot = className.lastIndexOf(".");
			String packageName = className.substring(0, lastDot);
			config.packages(packageName);
			config.register(reg.getService());
			((ServletContainer) servlet).reload(config);
		}

		@Override
		protected Servlet createServlet(RSARemoteServiceRegistration registration) {
			ResourceConfig resourceConfig = (ResourceConfig) createConfigurable();
			if (resourceConfig != null)
				resourceConfig.register(registration.getService());
			return new ServletContainer(resourceConfig);
		}

		@Override
		protected void unexportRegistration(RSARemoteServiceRegistration registration) {
			@SuppressWarnings("rawtypes")
			Configurable c = createConfigurable(getExportedRegistrations());
			if (c != null)
				((ServletContainer) servlet).reload((ResourceConfig) c);
		}

	}
}
