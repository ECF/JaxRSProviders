package org.eclipse.ecf.provider.jaxrs.client;

import org.eclipse.ecf.remoteservice.provider.RemoteServiceDistributionProvider;

public class JaxRSDistributionProvider extends RemoteServiceDistributionProvider {

	public JaxRSDistributionProvider(String name) {
		super(name, new JaxRSClientContainerInstantiator());
		setDescription("JaxRS Distribution Provider '" + name + "'");
	}

}
