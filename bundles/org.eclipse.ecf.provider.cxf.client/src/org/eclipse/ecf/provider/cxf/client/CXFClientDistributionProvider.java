package org.eclipse.ecf.provider.cxf.client;

import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.jaxrs.AbstractJaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainer;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.provider.RemoteServiceDistributionProvider;

public class CXFClientDistributionProvider extends RemoteServiceDistributionProvider {

	public static final String CLIENT_PROVIDER_NAME = "ecf.jaxrs.cxf.client";
	public static final String SERVER_PROVIDER_NAME = "ecf.jaxrs.cxf.server";

	public CXFClientDistributionProvider() {
		super(CLIENT_PROVIDER_NAME, new AbstractJaxRSContainerInstantiator(SERVER_PROVIDER_NAME, CLIENT_PROVIDER_NAME) {
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
								Object result = JAXRSClientFactory.create(getConnectedTarget(), interfaceClass, cl);
								return result;
							}

							@Override
							protected Client createJaxRSClient(Configuration configuration) throws ECFException {
								return null;
							}
						};
					}
				};
			}
		});
	}

}
