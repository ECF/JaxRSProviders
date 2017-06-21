/*******************************************************************************
* Copyright (c) 2017 Composent, Inc. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import javax.ws.rs.core.Feature;

import org.glassfish.jersey.jackson.JacksonFeature;
import org.osgi.service.component.annotations.Component;

@Component(property = "jaxrs-configurable-target=org.eclipse.ecf.provider.jersey.server.JerseyServerDistributionProvider")
public class JacksonFeatureComponent extends JacksonFeature implements Feature {

}
