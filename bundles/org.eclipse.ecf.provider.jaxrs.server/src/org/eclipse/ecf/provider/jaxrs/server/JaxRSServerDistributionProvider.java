/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.server;

import java.util.Map;

import javax.ws.rs.container.CompletionCallback;
import javax.ws.rs.container.ConnectionCallback;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;

import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.JaxRSDistributionProvider;

public abstract class JaxRSServerDistributionProvider extends JaxRSDistributionProvider {

	protected JaxRSServerDistributionProvider() {
		super();
	}

	protected JaxRSServerDistributionProvider(String name, IContainerInstantiator instantiator) {
		super(name, instantiator);
	}

	protected JaxRSServerDistributionProvider(String name, IContainerInstantiator instantiator, String description) {
		super(name, instantiator, description);
	}

	protected JaxRSServerDistributionProvider(String name, IContainerInstantiator instantiator, String description,
			boolean server) {
		super(name, instantiator, description, server);
	}

	@SuppressWarnings("rawtypes")
	protected void bindCompletionCallback(CompletionCallback instance, Map serviceProps) {
		super.bindJaxComponent(instance, serviceProps);
	}
	
	protected void unbindCompletionCallback(CompletionCallback instance) {
		super.unbindJaxComponent(instance);
	}
	
	@SuppressWarnings("rawtypes")
	protected void bindConnectionCallback(ConnectionCallback instance, Map serviceProps) {
		super.bindJaxComponent(instance, serviceProps);
	}
	
	protected void unbindConnectionCallback(ConnectionCallback instance) {
		super.unbindJaxComponent(instance);
	}
	
	@SuppressWarnings("rawtypes")
	protected void bindContainerRequestFilter(ContainerRequestFilter instance, Map serviceProps) {
		super.bindJaxComponent(instance, serviceProps);
	}

	protected void unbindContainerRequestFilter(ContainerRequestFilter instance) {
		super.unbindJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindContainerResponseFilter(ContainerResponseFilter instance, Map serviceProps) {
		super.bindJaxComponent(instance, serviceProps);
	}

	protected void unbindContainerResponseFilter(ContainerResponseFilter instance) {
		super.unbindJaxComponent(instance);
	}
}
