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
package org.eclipse.ecf.provider.cxf.client;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.JaxRSDistributionProvider;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainer;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;

public class CXFClientDistributionProvider extends JaxRSDistributionProvider {

	public static final String CLIENT_PROVIDER_NAME = "ecf.jaxrs.cxf.client";
	public static final String SERVER_PROVIDER_NAME = "ecf.jaxrs.cxf.server";

	public CXFClientDistributionProvider() {
		super(CLIENT_PROVIDER_NAME, new JaxRSContainerInstantiator(SERVER_PROVIDER_NAME, CLIENT_PROVIDER_NAME) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					Configuration configuration) {
				return new JaxRSClientContainer() {
					@Override
					protected IRemoteService createRemoteService(RemoteServiceClientRegistration registration) {
						return new JaxRSClientRemoteService(this, registration) {
							@SuppressWarnings("unchecked")
							@Override
							protected Object createJaxRSProxy(ClassLoader cl,
									@SuppressWarnings("rawtypes") Class interfaceClass, WebTarget webTarget)
									throws ECFException {
								return JAXRSClientFactory.create(getConnectedTarget(), interfaceClass);
							}

							@Override
							protected Client createJaxRSClient(Configuration configuration) throws ECFException {
								return null;
							}

							@Override
							protected WebTarget getJaxRSWebTarget(Client client) throws ECFException {
								return null;
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
