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

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSContainerTypeDescription;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public static final String PROVIDER_NAME = "ecf.container.client.jersey";

	public void start(BundleContext bundleContext) throws Exception {
		bundleContext.registerService(ContainerTypeDescription.class,
				new JaxRSContainerTypeDescription(PROVIDER_NAME),null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
