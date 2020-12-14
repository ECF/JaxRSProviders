/*******************************************************************************
* Copyright (c) 2018 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.server;

import jakarta.ws.rs.core.Feature;
import jakarta.ws.rs.core.FeatureContext;

import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class JaxRSServerJacksonFeature implements Feature {

	private final RSARemoteServiceRegistration registration;

	@Deprecated
	public JaxRSServerJacksonFeature(RSARemoteServiceRegistration reg, int priority) {
		this.registration = reg;
	}

	public JaxRSServerJacksonFeature(RSARemoteServiceRegistration reg) {
		this.registration = reg;
	}

	@Override
	public boolean configure(final FeatureContext context) {
		if (!context.getConfiguration().isRegistered(JacksonJaxbJsonProvider.class)) {
			context.register(JsonParseExceptionMapper.class);
			context.register(JsonMappingExceptionMapper.class);
			context.register(new JaxRSServerJacksonJaxbJsonProvider(registration));
		}
		return true;
	}
}
