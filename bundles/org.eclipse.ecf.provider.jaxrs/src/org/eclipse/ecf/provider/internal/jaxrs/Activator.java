/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.internal.jaxrs;

import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.BundleStarter;
import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static BundleContext context;
	
	private static final String[] dependencies = { "com.fasterxml.jackson.core.jackson-annotations",
			"com.fasterxml.jackson.core.jackson-core", "com.fasterxml.jackson.core.jackson-databind",
			"javax.ws.rs-api" };

	static BundleContext getContext() {
		return context;
	}

	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		BundleStarter.startDependents(bundleContext, dependencies, Bundle.ACTIVE | Bundle.STARTING);
		bundleContext.registerService(Namespace.class, new JaxRSNamespace(), null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
