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

import java.util.Map;

import javax.ws.rs.client.ClientRequestFilter;

import org.eclipse.ecf.provider.jersey.client.AbstractJerseyClientDistributionProvider;
import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;

@Component(service = IRemoteServiceDistributionProvider.class)
public class BasicAuthJerseyClientDistributionProvider extends AbstractJerseyClientDistributionProvider {
	public static final String CLIENT_PROVIDER_NAME = "ecf.jaxrs.jersey.client.basicauth";
	public static final String SERVER_PROVIDER_NAME = "ecf.jaxrs.jersey.server.basicauth";

	public BasicAuthJerseyClientDistributionProvider() {
		super(SERVER_PROVIDER_NAME, CLIENT_PROVIDER_NAME);
	}

	@SuppressWarnings("rawtypes")
	@Reference(cardinality = ReferenceCardinality.MULTIPLE)
	protected void bindClientRequestFilter(ClientRequestFilter instance, Map serviceProps) {
		super.bindClientRequestFilter(instance, serviceProps);
	}

	protected void unbindClientRequestFilter(ClientRequestFilter instance) {
		super.unbindClientRequestFilter(instance);
	}

}