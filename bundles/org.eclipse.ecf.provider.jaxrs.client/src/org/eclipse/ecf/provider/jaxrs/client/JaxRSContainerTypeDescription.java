package org.eclipse.ecf.provider.jaxrs.client;

import org.eclipse.ecf.core.ContainerTypeDescription;

public class JaxRSContainerTypeDescription extends ContainerTypeDescription {
	public JaxRSContainerTypeDescription(String name) {
		super(name, new JaxRSClientContainerInstantiator(), "JaxRS Container");
	}
}