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

import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;
import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		JaxRSNamespace ns = new JaxRSNamespace();
		ns.getName();
		context.registerService(IRemoteServiceDistributionProvider.class,
				new BasicAuthJerseyServerDistributionProvider(context), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}
}
