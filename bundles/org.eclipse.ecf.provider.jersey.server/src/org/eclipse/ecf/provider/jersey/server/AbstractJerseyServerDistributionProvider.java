/*******************************************************************************
* Copyright (c) 2020 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import java.net.URI;
import java.util.Map;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerDistributionProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.framework.BundleContext;

public class AbstractJerseyServerDistributionProvider extends JaxRSServerDistributionProvider {

	public static final String BINDING_PRIORITY = "bindingPriority";
	public static final String JACKSON_PRIORITY = "jacksonPriority";

	public AbstractJerseyServerDistributionProvider(String serverConfigType, String description) {
		super();
		setName(serverConfigType);
		JaxRSNamespace.class.getName();
		setDescription(description);
		setServer(true);
	}
	
	protected void activate(BundleContext context) {
		setInstantiator(new JaxRSServerContainerInstantiator(getName()) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					Configuration configuration) throws ContainerCreateException {
				URI uri = getUri(parameters, getName());
				checkOSGIIntents(description, uri, parameters);
				return new JerseyServerContainer(description.getName(), createJaxRSID(uri), context, (ResourceConfig) configuration,
						getJacksonPriority(parameters),
						getParameterValue(parameters, BINDING_PRIORITY, Integer.class,
								JerseyServerContainer.BINDING_DEFAULT_PRIORITY),getIncludeRemoteServiceId(parameters, getName()));
			}

			@Override
			protected boolean supportsOSGIConfidentialIntent(ContainerTypeDescription description) {
				return true;
			}

			@Override
			protected boolean supportsOSGIPrivateIntent(ContainerTypeDescription description) {
				return true;
			}

			@Override
			protected boolean supportsOSGIAsyncIntent(ContainerTypeDescription description) {
				return true;
			}
		});
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		return new ResourceConfig();
	}
}
