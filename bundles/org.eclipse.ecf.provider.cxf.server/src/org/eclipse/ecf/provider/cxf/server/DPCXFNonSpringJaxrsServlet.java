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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;

import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.cxf.jaxrs.utils.ResourceUtils;
import org.eclipse.ecf.provider.jaxrs.server.ServerJacksonJaxbJsonProvider;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;

public class DPCXFNonSpringJaxrsServlet extends CXFNonSpringJaxrsServlet {

	private static final long serialVersionUID = -2618572428261717260L;

	private RSARemoteServiceRegistration initRegistration;
	private CXFServerConfiguration config;
	private JAXRSServerFactoryBean bean;
//	private String servletAlias;
	
	public DPCXFNonSpringJaxrsServlet(final RSARemoteServiceRegistration initRegistration,
			CXFServerConfiguration config, String servletAlias) {
		super(new Application() {
			@Override
			public Set<Class<?>> getClasses() {
				Set<Class<?>> results = new HashSet<Class<?>>();
				results.add(initRegistration.getService().getClass());
				return results;
			}
			@Override
			public Set<Object> getSingletons() {
				Set<Object> results = new HashSet<Object>();
				results.add(initRegistration.getService());
				return results;
			}
		});
		this.initRegistration = initRegistration;
		this.config = config;
//		this.servletAlias = servletAlias;
	}

	@Override
	protected void createServerFromApplication(ServletConfig servletConfig) throws ServletException {
		Application app = getApplication();
		bean = ResourceUtils.createApplication(app, isIgnoreApplicationPath(servletConfig),
				getStaticSubResolutionValue(servletConfig), isAppResourceLifecycleASingleton(app, servletConfig),
				getBus());
		List<Object> extensions = new ArrayList<Object>(config.getExtensions());
		extensions.add(new ServerJacksonJaxbJsonProvider(this.initRegistration));
		bean.setProviders(extensions);
		bean.create();
	}
	
}