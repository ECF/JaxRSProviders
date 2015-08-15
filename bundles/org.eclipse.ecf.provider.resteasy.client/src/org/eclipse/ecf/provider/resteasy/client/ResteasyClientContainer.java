/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.resteasy.client;

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainer;
import org.jboss.resteasy.client.jaxrs.ProxyBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

public class ResteasyClientContainer extends JaxRSClientContainer {

	public static final String CONTAINER_TYPE_NAME = "ecf.container.client.resteasy";

	@Override
	protected Object createJaxRSProxy(ClassLoader cl, @SuppressWarnings("rawtypes") Class interfaceClass)
			throws ECFException {
		ResteasyClient client = new ResteasyClientBuilder().build();
		ResteasyWebTarget target = client.target(getConnectedTarget());
		@SuppressWarnings({ "unchecked", "rawtypes" })
		ProxyBuilder proxyBuilder = target.proxyBuilder(interfaceClass);
		proxyBuilder.classloader(cl);
		return proxyBuilder.build();
	}

}
