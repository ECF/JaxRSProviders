/*******************************************************************************
* Copyright (c) 2022 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.client;

import org.eclipse.ecf.core.util.BundleStarter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final String[] dependents = { 
			"org.eclipse.ecf.provider.jaxrs.client",
			"javax.inject",
			"javassist",
			"javax.validation.api",
			"com.sun.activation.javax.activation",
			"org.glassfish.hk2.api",
			"org.glassfish.hk2.external.aopalliance-repackaged", 
			"org.glassfish.hk2.locator",
			"org.glassfish.hk2.utils",
			"org.glassfish.jersey.core.jersey-common",
			"org.glassfish.jersey.core.jersey-client",
			"org.glassfish.jersey.ext.jersey-entity-filtering",
			"org.glassfish.jersey.inject.jersey-hk2"};

	@Override
	public void start(BundleContext context) throws Exception {
		BundleStarter.startDependents(context, dependents, Bundle.RESOLVED | Bundle.STARTING);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
