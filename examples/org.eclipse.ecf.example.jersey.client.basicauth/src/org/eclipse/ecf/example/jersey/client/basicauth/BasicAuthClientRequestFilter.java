/*******************************************************************************
* Copyright (c) 2020 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.example.jersey.client.basicauth;

import java.io.IOException;
import java.util.Base64;

import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;

import org.osgi.service.component.annotations.Component;

@Component(property = {"jaxrs-service-exported-config-target=ecf.jaxrs.jersey.client" })
public class BasicAuthClientRequestFilter implements ClientRequestFilter {

	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic ";

	private static final String testUsername = System.getProperty("rs.basicauth.username", "testusername");
	private static final String testPassword = System.getProperty("rs.basicauth.password", "testpassword");

	@Override
	public void filter(ClientRequestContext clientRequestContext) throws IOException {
		System.out.println("ContainerRequestFilter.filter for uris="+clientRequestContext.getUri());
		clientRequestContext.getHeaders().add(AUTHORIZATION_PROPERTY, AUTHENTICATION_SCHEME
				+ Base64.getEncoder().encodeToString(new String(testUsername + ":" + testPassword).getBytes()));
	}

}
