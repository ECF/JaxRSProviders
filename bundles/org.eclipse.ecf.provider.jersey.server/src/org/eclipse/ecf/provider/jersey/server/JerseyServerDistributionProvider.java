/*******************************************************************************
* Copyright (c) 2018 Composent, Inc. and Erdal Karaca. All rights reserved. This
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

import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.ConnectionCallback;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerDistributionProvider;
import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component(service = IRemoteServiceDistributionProvider.class)
public class JerseyServerDistributionProvider extends JaxRSServerDistributionProvider
		implements IRemoteServiceDistributionProvider {

	public static final String JERSEY_SERVER_CONFIG = "ecf.jaxrs.jersey.server";
	public static final String BINDING_PRIORITY = "bindingPriority";
	public static final String JACKSON_PRIORITY = "jacksonPriority";

	public JerseyServerDistributionProvider() {
		this(JERSEY_SERVER_CONFIG, "Jersey Jax-RS Server Distribution Provider");
	}

	protected JerseyServerDistributionProvider(String serverConfigType, String description) {
		super();
		setName(serverConfigType);
		setDescription(description);
		setServer(true);
	}

	protected boolean supportsOSGIConfidentialIntent() {
		return true;
	}

	protected boolean supportsOSGIPrivateIntent() {
		return true;
	}

	protected boolean supportsOSGIAsyncIntent() {
		return true;
	}

	@Activate
	protected void activate(BundleContext context) {
		setInstantiator(new JaxRSServerContainerInstantiator(getName()) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					Configuration configuration) throws ContainerCreateException {
				URI uri = getUri(parameters, getName());
				checkOSGIIntents(description, uri, parameters);
				return new JerseyServerContainer(description.getName(), createJaxRSID(uri), context,
						(ResourceConfig) configuration, getJacksonPriority(parameters),
						getParameterValue(parameters, BINDING_PRIORITY, Integer.class,
								JerseyServerContainer.BINDING_DEFAULT_PRIORITY),
						getIncludeRemoteServiceId(parameters, getName()));
			}

			@Override
			protected boolean supportsOSGIConfidentialIntent(ContainerTypeDescription description) {
				return JerseyServerDistributionProvider.this.supportsOSGIConfidentialIntent();
			}

			@Override
			protected boolean supportsOSGIPrivateIntent(ContainerTypeDescription description) {
				return JerseyServerDistributionProvider.this.supportsOSGIConfidentialIntent();
			}

			@Override
			protected boolean supportsOSGIAsyncIntent(ContainerTypeDescription description) {
				return JerseyServerDistributionProvider.this.supportsOSGIAsyncIntent();
			}
		});
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		return new ResourceConfig();
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindCompletionCallback(CompletionCallback instance, Map serviceProps) {
		super.bindCompletionCallback(instance, serviceProps);
	}

	protected void unbindCompletionCallback(CompletionCallback instance) {
		super.unbindCompletionCallback(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindConnectionCallback(ConnectionCallback instance, Map serviceProps) {
		super.bindConnectionCallback(instance, serviceProps);
	}

	protected void unbindConnectionCallback(ConnectionCallback instance) {
		super.unbindConnectionCallback(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindContainerRequestFilter(ContainerRequestFilter instance, Map serviceProps) {
		super.bindContainerRequestFilter(instance, serviceProps);
	}

	protected void unbindContainerRequestFilter(ContainerRequestFilter instance) {
		super.unbindContainerRequestFilter(instance);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindContainerResponseFilter(ContainerResponseFilter instance, Map serviceProps) {
		super.bindContainerResponseFilter(instance, serviceProps);
	}

	protected void unbindContainerResponseFilter(ContainerResponseFilter instance) {
		super.unbindContainerResponseFilter(instance);
	}

}
