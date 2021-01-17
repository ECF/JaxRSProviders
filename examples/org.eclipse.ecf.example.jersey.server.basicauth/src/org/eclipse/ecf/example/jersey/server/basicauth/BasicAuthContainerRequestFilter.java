/*******************************************************************************
* Copyright (c) 2020 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.example.jersey.server.basicauth;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;

import org.osgi.service.component.annotations.Component;

@Component(immediate = true, property = { "jaxrs-service-exported-config-target=ecf.jaxrs.jersey.server",
		"jaxrs-component-intents=ecf.jaxrs.basicauth" })
public class BasicAuthContainerRequestFilter implements ContainerRequestFilter {

	private static final String AUTHORIZATION_PROPERTY = "Authorization";
	private static final String AUTHENTICATION_SCHEME = "Basic";
	// 'fake' password database
	private static final Map<String, String> usernamePasswordDb = new HashMap<String, String>();

	static {
		usernamePasswordDb.put(System.getProperty("rs.basicauth.username", "testusername"),
				System.getProperty("rs.basicauth.password", "testpassword"));
	}

	class BasicAuthCredentials {
		private final String username;
		private final String password;

		public BasicAuthCredentials(ContainerRequestContext containerRequestContext) {
			List<String> authHeaders = containerRequestContext.getHeaders().get(AUTHORIZATION_PROPERTY);
			if (authHeaders == null) {
				throw new IllegalArgumentException("Request does not have Authorization header");
			}
			String authHeaderValue = authHeaders.get(0);
			if (authHeaderValue == null) {
				throw new IllegalArgumentException("Request does not have authorization header value");
			}
			final StringTokenizer tokenizer = new StringTokenizer(new String(Base64.getDecoder()
					.decode(authHeaderValue.replaceFirst(AUTHENTICATION_SCHEME + " ", "").getBytes())), ":");
			this.username = tokenizer.nextToken();
			this.password = tokenizer.nextToken();
		}

		public boolean authenticate() {
			String password = usernamePasswordDb.get(this.username);
			if (password == null || this.password == null || !this.password.equals(password)) {
				return false;
			}
			return true;
		}
	}

	@Override
	public void filter(ContainerRequestContext containerRequestContext) throws IOException {
		try {
			// XXX as this is example, it prints to system out that we are here, so that
			// can verify this is being called at request time
			System.out.println(
					"ContainerRequestFilter.filter for uri=" + containerRequestContext.getUriInfo().getRequestUri());
			BasicAuthCredentials authCreds = new BasicAuthCredentials(containerRequestContext);
			if (authCreds.authenticate()) {
				return;
			} else {
				throw new IOException("Incorrect or invalid username or password");
			}
		} catch (IOException e) {
			throw e;
		} catch (Exception e) {
			// log error
			e.printStackTrace(System.err);
			containerRequestContext
					.abortWith(Response.status(Response.Status.UNAUTHORIZED).entity("Authentication error").build());
		}
	}
}
