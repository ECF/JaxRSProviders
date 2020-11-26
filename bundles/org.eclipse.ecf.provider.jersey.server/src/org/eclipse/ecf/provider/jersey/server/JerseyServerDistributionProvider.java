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

import org.osgi.framework.BundleContext;

public class JerseyServerDistributionProvider extends AbstractJerseyServerDistributionProvider {

	public static final String JERSEY_SERVER_CONFIG = "ecf.jaxrs.jersey.server";

	public JerseyServerDistributionProvider(final BundleContext context) {
		super(context, JERSEY_SERVER_CONFIG, "Jersey Jax-RS Server Distribution Provider");
	}

}
