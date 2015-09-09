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

import java.util.Map;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.ext.ContextResolver;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainer;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.provider.RemoteServiceDistributionProvider;
import org.eclipse.ecf.provider.jaxrs.AbstractJaxRSContainerInstantiator;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JerseyClientDistributionProvider extends RemoteServiceDistributionProvider {
	public static final String CLIENT_PROVIDER_NAME = "ecf.jaxrs.jersey.client";
	public static final String SERVER_PROVIDER_NAME = "ecf.jaxrs.jersey.server";

	public JerseyClientDistributionProvider() {
		super(CLIENT_PROVIDER_NAME, new AbstractJaxRSContainerInstantiator(SERVER_PROVIDER_NAME, CLIENT_PROVIDER_NAME) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, @SuppressWarnings("rawtypes") Map parameters,
					final Configuration configuration) {
				return new JaxRSClientContainer() {
					@Override
					protected IRemoteService createRemoteService(RemoteServiceClientRegistration registration) {
						return new JaxRSClientRemoteService(this, registration) {
							// Overriding this method allows us to configure the
							// JaxRS client when a remote service instance is
							// created
							@Override
							protected Configuration createJaxRSClientConfiguration() throws ECFException {
								// If we are provided a configuration via
								// service property, and it's appropriate
								// type (ClientConfig), then use it, otherwise
								// create new ClientConfig
								ClientConfig config = (ClientConfig) ((configuration instanceof ClientConfig)
										? configuration : new ClientConfig());
								// Configure for Jackson json generation/parsing
								config.register(JacksonFeature.class);
								// Configure to use ObjectMapper that is
								// configured to ignore unknown properties
								config.register(ObjectMapperContextResolver.class);
								return config;
							}
						};
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