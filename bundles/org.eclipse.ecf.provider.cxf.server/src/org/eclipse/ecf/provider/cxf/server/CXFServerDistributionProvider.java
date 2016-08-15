/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.cxf.server;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerDistributionProvider;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.osgi.service.http.HttpService;

public class CXFServerDistributionProvider extends JaxRSServerDistributionProvider {

	public static final String SERVER_CONFIG_NAME = "ecf.jaxrs.cxf.server";

	public static final String URI_PARAM = "uri";
	public static final String URI_DEFAULT = "http://localhost:8080/jersey";

	public CXFServerDistributionProvider() {
		super();
	}

	public void activate() throws Exception {
		setName(SERVER_CONFIG_NAME);
		setInstantiator(new JaxRSContainerInstantiator(SERVER_CONFIG_NAME) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					Configuration configuration) throws ContainerCreateException {
				String uri = getParameterValue(parameters, URI_PARAM, URI_DEFAULT);
				try {
					return new JaxRSServerContainer(new URI(uri)) {
						@Override
						protected Servlet createServlet(final RSARemoteServiceRegistration registration) {
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
							List<HttpService> svcs = getHttpServices();
							return (svcs == null || svcs.size() == 0) ? null : svcs.get(0);
						}

						@Override
						protected void exportRegistration(RSARemoteServiceRegistration reg) {
							// TODO Auto-generated method stub
							
						}

						@Override
						protected void unexportRegistration(RSARemoteServiceRegistration registration) {
							// TODO Auto-generated method stub
							
						}
					};
				} catch (URISyntaxException e) {
					throw new ContainerCreateException("Could not create CXF Server Container",e);
				}
			}
		});
		setDescription("CXF Jax-RS Distribution Provider");
		setServer(true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		return null;
	}

}
