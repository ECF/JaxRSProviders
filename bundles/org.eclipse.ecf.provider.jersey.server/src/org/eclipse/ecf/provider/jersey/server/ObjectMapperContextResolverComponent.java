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
package org.eclipse.ecf.provider.jersey.server;

import javax.ws.rs.ext.ContextResolver;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(property = "jaxrs-configurable-target=org.eclipse.ecf.provider.jersey.server.JerseyServerDistributionProvider")
public class ObjectMapperContextResolverComponent implements ContextResolver<ObjectMapper> {

	private ObjectMapper mapper = null;

	public ObjectMapperContextResolverComponent() {
		super();
		// Set fail on unknown properties to false
		mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		return mapper;
	}
}
