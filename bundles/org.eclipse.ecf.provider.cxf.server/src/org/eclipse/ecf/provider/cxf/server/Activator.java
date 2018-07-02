/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.cxf.server;

import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		System.setProperty("javax.ws.rs.ext.RuntimeDelegate","org.apache.cxf.jaxrs.impl.RuntimeDelegateImpl");
		System.setProperty("org.apache.cxf.stax.allowInsecureParser","1");
		// Register CXF Server distribution provider
		context.registerService(IRemoteServiceDistributionProvider.class, new CXFServerDistributionProvider(context),
				null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
