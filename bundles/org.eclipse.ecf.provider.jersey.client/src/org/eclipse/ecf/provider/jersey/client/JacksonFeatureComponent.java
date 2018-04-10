/*******************************************************************************
* Copyright (c) 2016 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Erdal Karaca - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.client;

import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;

import org.glassfish.jersey.jackson.JacksonFeature;

public class JacksonFeatureComponent implements Feature {

	private final JacksonFeature feature;

	public JacksonFeatureComponent() {
		feature = new JacksonFeature();
	}

	@Override
	public boolean configure(FeatureContext context) {
		return feature.configure(context);
	}

}
