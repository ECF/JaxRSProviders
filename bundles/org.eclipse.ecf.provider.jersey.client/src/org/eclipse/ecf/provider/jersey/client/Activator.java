/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.client;

import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

	public static final String CLIENT_PROVIDER_NAME = "ecf.jaxrs.jersey.client";
	public static final String SERVER_PROVIDER_NAME = "ecf.jaxrs.jersey.server";

	private ServiceRegistration<IRemoteServiceDistributionProvider> sr;

	public void start(BundleContext bundleContext) throws Exception {
		// Register the JersyRSDistributionProvider as a remote service
		// distribution provider
		sr = bundleContext.registerService(IRemoteServiceDistributionProvider.class,
				new JerseyClientDistributionProvider(CLIENT_PROVIDER_NAME, SERVER_PROVIDER_NAME), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (sr != null) {
			sr.unregister();
			sr = null;
		}
	}

}
