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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseFilter;

import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.JaxRSDistributionProvider;
import org.osgi.service.http.HttpService;

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

	private List<HttpService> httpServices = Collections.synchronizedList(new ArrayList<HttpService>());

	public void bindHttpService(HttpService httpService) {
		if (httpService != null)
			httpServices.add(httpService);
	}

	public void unbindHttpService(HttpService httpService) {
		if (httpService != null)
			httpServices.remove(httpService);
	}

	protected List<HttpService> getHttpServices() {
		return httpServices;
	}

	@SuppressWarnings("rawtypes")
	protected void bindContainerRequestFilter(ContainerRequestFilter instance, Map serviceProps) {
		this.bindJaxComponent(instance, serviceProps);
	}

	protected void unbindContainerRequestFilter(ContainerRequestFilter instance) {
		this.removeJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindContainerResponseFilter(ContainerResponseFilter instance, Map serviceProps) {
		this.bindJaxComponent(instance, serviceProps);
	}

	protected void unbindContainerResponseFilter(ContainerResponseFilter instance) {
		this.removeJaxComponent(instance);
	}

}
