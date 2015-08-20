/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.client;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.ext.ContextResolver;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainer;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSDistributionProvider;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JerseyRSDistributionProvider extends JaxRSDistributionProvider {
	public JerseyRSDistributionProvider(String name) {
		super(name);
		// We create a new container instantiator 
		// so we can override the createJaxRSClientConfiguratio below
		setInstantiator(new JaxRSClientContainerInstantiator() {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) {
				return new JaxRSClientContainer() {
					// Overriding this method allows us to configure the JaxRS
					// client
					@Override
					protected Configuration createJaxRSClientConfiguration() throws ECFException {
						ClientConfig config = new ClientConfig();
						// Configure for Jackson json generation/parsing
						config.register(JacksonFeature.class);
						// Configure to use ObjectMapper that is configured
						// to ignore unknown properties
						config.register(ObjectMapperContextResolver.class);
						return config;
					}
				};
			}
		});
	}

	public static class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
		private ObjectMapper mapper = null;

		public ObjectMapperContextResolver() {
			super();
			// Set fail on unknown properties to false
			mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		}

		@Override
		public ObjectMapper getContext(Class<?> type) {
			return mapper;
		}
	}

}