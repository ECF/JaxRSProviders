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
import java.util.HashSet;
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
import org.osgi.framework.BundleContext;

public class CXFServerDistributionProvider extends JaxRSServerDistributionProvider {

	public static final String SERVER_CONFIG_NAME = "ecf.jaxrs.cxf.server";

	public static final String URI_PARAM = "uri";
	public static final String URI_DEFAULT = "http://localhost:8080/cxf";

	public CXFServerDistributionProvider(final BundleContext context) {
		super();
		setName(SERVER_CONFIG_NAME);
		setInstantiator(new JaxRSContainerInstantiator(SERVER_CONFIG_NAME) {

			@Override
			protected boolean supportsOSGIConfidentialIntent(ContainerTypeDescription description) {
				return true;
			}

			@Override
			protected boolean supportsOSGIPrivateIntent(ContainerTypeDescription description) {
				return true;
			}

			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					Configuration configuration) throws ContainerCreateException {
				String uriStr = getParameterValue(parameters, URI_PARAM, URI_DEFAULT);
				URI uri = null;
				try {
					uri = new URI(uriStr);
				} catch (Exception e) {
					throw new ContainerCreateException("Cannot create Jersey Server Container because uri=" + uri, e);
				}
				checkOSGIIntents(description, uri, parameters);
				return new JaxRSServerContainer(context, uri) {
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
					protected void exportRegistration(RSARemoteServiceRegistration reg) {
					}

					@Override
					protected void unexportRegistration(RSARemoteServiceRegistration registration) {
					}
				};
			}
		});
		setDescription("CXF Jax-RS Distribution Provider");
		setServer(true);
//		JacksonJaxbJsonProvider p = new JacksonJaxbJsonProvider();
//		addJaxRSComponent(new ObjectMapperContextResolverComponent(), ContextResolver.class);
		//addJaxRSComponent(new JAXBElementProvider(),)
//		addJaxRSComponent(p , MessageBodyWriter.class);
//		addJaxRSComponent(p , MessageBodyReader.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		return null;
	}

}
