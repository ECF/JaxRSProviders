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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainer;
import org.glassfish.jersey.client.proxy.WebResourceFactory;

public class JerseyClientContainer extends JaxRSClientContainer {

	public static final String CONTAINER_TYPE_NAME = "ecf.container.client.jersey";

	@Override
	protected Object createJaxRSProxy(ClassLoader cl, @SuppressWarnings("rawtypes") Class interfaceClass)
			throws ECFException {
		try {
			Client client = ClientBuilder.newClient();
			// XXX other client configuration can occur here
			WebTarget webTarget = client.target(getConnectedTarget());
			@SuppressWarnings("unchecked")
			Object result = WebResourceFactory.newResource(interfaceClass, webTarget);
			return result;
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ECFException("client could not be create", t);
		}
	}

}
