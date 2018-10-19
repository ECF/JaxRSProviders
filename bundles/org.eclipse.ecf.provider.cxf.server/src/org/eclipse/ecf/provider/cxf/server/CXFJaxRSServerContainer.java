/*******************************************************************************
* Copyright (c) 2018 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.cxf.server;

import java.net.URI;

import javax.servlet.Servlet;

import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.osgi.framework.BundleContext;

public class CXFJaxRSServerContainer extends JaxRSServerContainer {

	private final CXFServerConfiguration configuration;
	
	public CXFJaxRSServerContainer(BundleContext context, URI uri, CXFServerConfiguration configuration) {
		super(context, uri);
		this.configuration = configuration;
	}

	@Override
	protected Servlet createServlet(RSARemoteServiceRegistration registration) {
		return new DPCXFNonSpringJaxrsServlet(registration, (CXFServerConfiguration) configuration, getServletAlias(registration));
	}

}
