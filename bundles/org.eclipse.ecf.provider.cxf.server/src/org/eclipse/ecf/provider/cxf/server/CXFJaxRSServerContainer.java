/*******************************************************************************
* Copyright (c) 2018 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.cxf.server;

import java.net.URI;

import javax.servlet.Servlet;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.ext.ContextResolver;

import org.eclipse.ecf.provider.jaxrs.ObjectMapperContextResolver;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.osgi.framework.BundleContext;

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class CXFJaxRSServerContainer extends JaxRSServerContainer {

	public CXFJaxRSServerContainer(BundleContext context, URI uri, int jacksonPriority) {
		super(context, uri, jacksonPriority);
	}

	@Override
	protected Servlet createServlet(Configurable<?> configurable, RSARemoteServiceRegistration registration) {
		return new DPCXFNonSpringJaxrsServlet(registration, (CXFServerConfigurable) configurable);
	}

	@Override
	protected void registerExtensions(Configurable<?> configurable, RSARemoteServiceRegistration registration) {
		// This overrides/replaces superclass implementation to setup
		// ObjectMapperContextResolver, and Jackson Jaxb Json parsing/writing
		configurable.register(new ObjectMapperContextResolver(), ContextResolver.class);
		configurable.register(JsonParseExceptionMapper.class);
		configurable.register(JsonMappingExceptionMapper.class);
		configurable.register(new JacksonJaxbJsonProvider(), jacksonPriority);
	}

	@Override
	protected Configurable<?> createConfigurable(RSARemoteServiceRegistration registration) {
		return new CXFServerConfigurable();
	}

}
