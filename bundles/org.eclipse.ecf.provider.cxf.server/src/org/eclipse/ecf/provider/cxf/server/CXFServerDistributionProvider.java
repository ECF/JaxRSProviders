/*******************************************************************************
* Copyright (c) 2015, 2016 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Erdal Karaca <erdal.karaca.de@gmail.com> - Bug 499165 
******************************************************************************/
package org.eclipse.ecf.provider.cxf.server;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configuration;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSExtensionsRegistry;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer.JaxRSServerRemoteServiceContainerAdapter.JaxRSServerRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerDistributionProvider;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.http.HttpService;

@Component(immediate = true, service = IRemoteServiceDistributionProvider.class)
public class CXFServerDistributionProvider extends JaxRSServerDistributionProvider {

	public static final String SERVER_CONFIG_NAME = "ecf.jaxrs.cxf.server";

	public static final String URL_CONTEXT_PARAM = "urlContext";
	public static final String URL_CONTEXT_DEFAULT = System
			.getProperty("org.eclipse.ecf.provider.cxf.server.defaultUrlContext", "http://localhost:8080");
	public static final String ALIAS_PARAM = "alias";
	public static final String ALIAS_PARAM_DEFAULT = "/cxf";
	
	private JaxRSExtensionsRegistry extensionsComponent;

	public CXFServerDistributionProvider() {
		super();
	}

	@Activate
	public void activate() throws Exception {
		setName(SERVER_CONFIG_NAME);
		setInstantiator(new JaxRSContainerInstantiator(SERVER_CONFIG_NAME) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					Configuration configuration) {
				String urlContext = getParameterValue(parameters, URL_CONTEXT_PARAM, URL_CONTEXT_DEFAULT);
				String alias = getParameterValue(parameters, ALIAS_PARAM, ALIAS_PARAM_DEFAULT);
				return new JaxRSServerContainer(urlContext, alias) {
					@Override
					protected Servlet createServlet(final JaxRSServerRemoteServiceRegistration registration) {
						return new CXFNonSpringJaxrsServlet(new Application() {
							@Override
							public Set<Class<?>> getClasses() {
								Set<Class<?>> results = new HashSet<Class<?>>();
								results.add(registration.getService().getClass());
								return results;
							}
						});
					}

					@Override
					protected HttpService getHttpService() {
						List<HttpService> svcs = extensionsComponent.getHttpServices();
						return (svcs == null || svcs.size() == 0) ? null : svcs.get(0);
					}
				};
			}
		});
		setDescription("CXF Jax-RS Distribution Provider");
		setServer(true);
	}

}
