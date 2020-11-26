/*******************************************************************************
* Copyright (c) 2020 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.client;

import java.util.Map;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainer;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientDistributionProvider;
import org.glassfish.jersey.client.ClientConfig;

public class AbstractJerseyClientDistributionProvider extends JaxRSClientDistributionProvider {

	public static final String JACKSON_PRIORITY = "jacksonPriority";

	public AbstractJerseyClientDistributionProvider(String serverProviderName, String clientProviderName) {
		super(clientProviderName, new JaxRSContainerInstantiator(serverProviderName, clientProviderName) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					final Configuration configuration) {
				Integer jacksonPriority = getParameterValue(parameters, JACKSON_PRIORITY, Integer.class,
						JaxRSClientContainer.JACKSON_DEFAULT_PRIORITY);
				return new JaxRSClientContainer(createJaxRSID(), configuration, jacksonPriority);
			}
		});

	}
	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		return new ClientConfig();
	}

}
