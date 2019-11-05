/*******************************************************************************
* Copyright (c) 2019 Composent, Inc. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.internal.jersey.ext.example;

import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jersey.server.JerseyServerContainer;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.framework.BundleContext;

public class ExtJerseyServerContainer extends JerseyServerContainer {

	public ExtJerseyServerContainer(String configType, URIID containerID, BundleContext context, ResourceConfig configuration) {
		super(configType, containerID, context, configuration, JaxRSContainerInstantiator.JACKSON_DEFAULT_PRIORITY,
				JerseyServerContainer.BINDING_DEFAULT_PRIORITY, false);
	}

	@Override
	protected String getServletAlias(RSARemoteServiceRegistration reg) {
		// Here's what all this was for...we return an empty string
		return "";
		// rather than the reg.getServiceId() value as string. This
		// is what the superclass does:
		// return String.valueOf(reg.getServiceId());
	}
}
