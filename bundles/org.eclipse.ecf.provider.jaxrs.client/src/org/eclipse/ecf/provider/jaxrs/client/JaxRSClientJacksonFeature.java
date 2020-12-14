/*******************************************************************************
* Copyright (c) 2018 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.client;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class JaxRSClientJacksonFeature implements Feature {

	private final RemoteServiceClientRegistration reg;
	private final ClassLoader cl;

	@Deprecated
	public JaxRSClientJacksonFeature(RemoteServiceClientRegistration reg, ClassLoader cl, int priority) {
		this.reg = reg;
		this.cl = cl;
	}
	
	public JaxRSClientJacksonFeature(RemoteServiceClientRegistration reg, ClassLoader cl) {
		this.reg = reg;
		this.cl = cl;
	}

	@Override
	public boolean configure(final FeatureContext context) {
		if (!context.getConfiguration().isRegistered(JacksonJaxbJsonProvider.class)) {
			context.register(JsonParseExceptionMapper.class);
			context.register(JsonMappingExceptionMapper.class);
			context.register(new JaxRSClientJacksonJaxbJsonProvider(reg, cl));
		}
		return true;
	}
}
