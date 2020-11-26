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

import javax.ws.rs.core.Configurable;

import org.eclipse.ecf.provider.jersey.server.AbstractJerseyServerDistributionProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.framework.BundleContext;

public class BasicAuthJerseyServerDistributionProvider extends AbstractJerseyServerDistributionProvider {

	public static final String JERSEY_SERVER_CONFIG = "ecf.jaxrs.jersey.server.basicauth";

	public BasicAuthJerseyServerDistributionProvider(final BundleContext context) {
		super(context, JERSEY_SERVER_CONFIG, "Jersey Jax-RS Server Distribution Provider with Basic Auth");
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		return new ResourceConfig().register(new BasicAuthContainerRequestFilter());
	}
}
