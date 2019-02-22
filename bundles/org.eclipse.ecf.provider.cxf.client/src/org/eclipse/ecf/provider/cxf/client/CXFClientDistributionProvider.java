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
package org.eclipse.ecf.provider.cxf.client;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

import org.apache.cxf.jaxrs.client.spec.ClientBuilderImpl;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.JaxRSDistributionProvider;
import org.eclipse.ecf.provider.jaxrs.ObjectMapperContextResolver;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainer;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientJacksonFeature;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.osgi.service.component.annotations.Component;

@Component(immediate = true, service = IRemoteServiceDistributionProvider.class)
public class CXFClientDistributionProvider extends JaxRSDistributionProvider {

	public static final String CXF_CLIENT_CONFIG = "ecf.jaxrs.cxf.client";
	public static final String CXF_SERVER_CONFIG = "ecf.jaxrs.cxf.server";

	public CXFClientDistributionProvider() {
		super(CXF_CLIENT_CONFIG, new JaxRSContainerInstantiator(CXF_SERVER_CONFIG, CXF_CLIENT_CONFIG) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					final Configuration configuration) {
				return new JaxRSClientContainer(createJaxRSID(), configuration, (int) getJacksonPriority(parameters)) {
					@Override
					protected IRemoteService createRemoteService(RemoteServiceClientRegistration registration) {
						return new JaxRSClientRemoteService(this, registration) {
							protected Client createJaxRSClient(Configuration configuration, ClassLoader cl) throws ECFException {
								ClientBuilder cb = new ClientBuilderImpl();
								cb.register(new ObjectMapperContextResolver());
								cb.register(new JaxRSClientJacksonFeature(getRegistration(), cl, jacksonPriority), jacksonPriority);
								return cb.build();
							}

							protected WebTarget getJaxRSWebTarget(Client client) throws ECFException {
								return client.target(getConnectedTarget());
							}
						};
					}
				};
			}
		});
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		return null;
	}

}
