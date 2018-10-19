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
import java.util.Map;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.ObjectMapperContextResolver;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerDistributionProvider;
import org.osgi.framework.BundleContext;

public class CXFServerDistributionProvider extends JaxRSServerDistributionProvider {

	public static final String CXF_SERVER_CONFIG = "ecf.jaxrs.cxf.server";

	public CXFServerDistributionProvider(final BundleContext context) {
		super();
		setName(CXF_SERVER_CONFIG);
		setInstantiator(new JaxRSServerContainerInstantiator(CXF_SERVER_CONFIG) {

			@Override
			protected boolean supportsOSGIConfidentialIntent(ContainerTypeDescription description) {
				return true;
			}

			@Override
			protected boolean supportsOSGIPrivateIntent(ContainerTypeDescription description) {
				return true;
			}

			@Override
			protected boolean supportsOSGIAsyncIntent(ContainerTypeDescription description) {
				return true;
			}

			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					final Configuration configuration) throws ContainerCreateException {
				URI uri = getUri(parameters, CXF_SERVER_CONFIG);
				checkOSGIIntents(description, uri, parameters);
				return new CXFJaxRSServerContainer(context, uri, (CXFServerConfiguration) configuration);
			}
		});
		setDescription("CXF Jax-RS Distribution Provider");
		setServer(true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		CXFServerConfigurable configurable = new CXFServerConfigurable();
		configurable.register(new ObjectMapperContextResolver());
		return configurable;
	}

}
