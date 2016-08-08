/*******************************************************************************
* Copyright (c) 2016 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.client;

import java.util.Map;

import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.client.ClientResponseFilter;

import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.JaxRSDistributionProvider;

public abstract class JaxRSClientDistributionProvider extends JaxRSDistributionProvider {

	protected JaxRSClientDistributionProvider() {
	}

	protected JaxRSClientDistributionProvider(String name, IContainerInstantiator instantiator, String description,
			boolean server) {
		super(name, instantiator, description, server);
	}

	protected JaxRSClientDistributionProvider(String name, IContainerInstantiator instantiator, String description) {
		super(name, instantiator, description);
	}

	protected JaxRSClientDistributionProvider(String name, IContainerInstantiator instantiator) {
		super(name, instantiator);
	}

	@SuppressWarnings("rawtypes")
	protected void bindClientRequestFilter(ClientRequestFilter instance, Map serviceProps) {
		this.bindJaxRSExtension(instance, serviceProps);
	}

	protected void unbindClientRequestFilter(ClientRequestFilter instance) {
		this.removeJaxRSExtension(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindClientResponseFilter(ClientResponseFilter instance, Map serviceProps) {
		this.bindJaxRSExtension(instance, serviceProps);
	}

	protected void unbindClientResponseFilter(ClientResponseFilter instance) {
		this.removeJaxRSExtension(instance);
	}

}
