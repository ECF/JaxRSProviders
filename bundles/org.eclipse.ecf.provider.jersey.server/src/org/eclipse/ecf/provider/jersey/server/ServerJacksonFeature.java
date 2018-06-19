/*******************************************************************************
* Copyright (c) 2018 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.eclipse.ecf.provider.jaxrs.server.ServerJacksonJaxbJsonProvider;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.glassfish.jersey.jackson.JacksonFeature;

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class ServerJacksonFeature extends JacksonFeature {

	private RSARemoteServiceRegistration reg;

	public ServerJacksonFeature(RSARemoteServiceRegistration reg) {
		this.reg = reg;
	}

	@Override
	public boolean configure(final FeatureContext context) {
		if (!context.getConfiguration().isRegistered(JacksonJaxbJsonProvider.class)) {
			context.register(JsonParseExceptionMapper.class);
			context.register(JsonMappingExceptionMapper.class);
			context.register(new ServerJacksonJaxbJsonProvider(reg), MessageBodyReader.class, MessageBodyWriter.class);
		}
		return true;
	}
}
