/*******************************************************************************
* Copyright (c) 2018 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Erdal Karaca - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import java.net.URI;

import javax.servlet.Servlet;
import javax.ws.rs.core.Feature;

import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.framework.BundleContext;

public class JerseyServerContainer extends JaxRSServerContainer {

	private ResourceConfig originalConfiguration;

	public JerseyServerContainer(BundleContext context, URI uri, ResourceConfig configuration) {
		super(context, uri);
		this.originalConfiguration = configuration;
	}

	@Override
	protected Servlet createServlet(RSARemoteServiceRegistration registration) {
		Object svc = registration.getService();
		String packageName = getPackageName(svc);
		ResourceConfig configuration = new ResourceConfig(this.originalConfiguration);
		if (packageName != null)
			configuration.packages(packageName);
		configuration.register(svc);
		configuration.register(new ServerJacksonFeature(registration), Feature.class);
		return new ServletContainer(configuration);
	}

}