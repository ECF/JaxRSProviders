/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.core.Configuration;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.core.provider.IRemoteServiceContainerInstantiator;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.provider.RemoteServiceContainerInstantiator;

public abstract class JaxRSContainerInstantiator extends RemoteServiceContainerInstantiator
		implements IRemoteServiceContainerInstantiator {

	public static final String CONFIG_PARAM = "configuration";

	protected static final String[] jaxIntents = new String[] { "jaxrs" };

	public static final String JACKSON_PRIORITY_PROP = "jacksonPriority";

	public static final int JACKSON_DEFAULT_PRIORITY = Integer
			.valueOf(System.getProperty(JaxRSContainerInstantiator.class.getName() + ".jacksonPriority", String.valueOf(javax.ws.rs.Priorities.USER)));

	private JaxRSDistributionProvider distprovider;

	private JaxRSNamespace jaxRSNamespace;

	protected JaxRSContainerInstantiator(String serverConfigTypeName) {
		this.exporterConfigs.add(serverConfigTypeName);
	}

	protected JaxRSContainerInstantiator(String serverConfigTypeName, String clientConfigTypeName) {
		this(serverConfigTypeName);
		this.exporterConfigToImporterConfigs.put(serverConfigTypeName,
				Arrays.asList(new String[] { clientConfigTypeName }));
	}

	@Override
	public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters)
			throws ContainerCreateException {
		Configuration configuration = getConfigurationFromParams(description, parameters);
		if (configuration == null)
			configuration = (this.distprovider == null) ? null : this.distprovider.getConfiguration();
		return createInstance(description, parameters, configuration);
	}

	public abstract IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
			Configuration configuration) throws ContainerCreateException;

	protected URIID createJaxRSID() {
		try {
			return createJaxRSID(new URI("uuid:" + UUID.randomUUID().toString()));
		} catch (URISyntaxException e) {
			throw new IDCreateException("Could not create random JaxRSID", e);
		}
	}

	private JaxRSNamespace getJaxRSNamespace() {
		synchronized (this) {
			if (this.jaxRSNamespace == null) {
				this.jaxRSNamespace = new JaxRSNamespace();
			}
			return this.jaxRSNamespace;
		}
	}
	
	protected URIID createJaxRSID(String uri) {
		return (URIID) getJaxRSNamespace().createInstance(uri);
	}

	protected URIID createJaxRSID(URI uri) {
		return (URIID) getJaxRSNamespace().createInstance(uri);
	}

	protected Configuration getConfigurationFromParams(ContainerTypeDescription description,
			Map<String, ?> parameters) {
		return getParameterValue(parameters, CONFIG_PARAM, Configuration.class, null);
	}

	protected Integer getJacksonPriority(Map<String, ?> parameters) {
		return getParameterValue(parameters, JACKSON_PRIORITY_PROP, Integer.class, JACKSON_DEFAULT_PRIORITY);
	}

	public String[] getSupportedIntents(ContainerTypeDescription description) {
		List<String> results = new ArrayList<String>(Arrays.asList(super.getSupportedIntents(description)));
		results.addAll(Arrays.asList(jaxIntents));
		// remove basic intent
		return removeSupportedIntent(Constants.OSGI_BASIC_INTENT,
				(String[]) results.toArray(new String[results.size()]));
	}

	void setDistributionProvider(JaxRSDistributionProvider jaxRSDistributionProvider) {
		this.distprovider = jaxRSDistributionProvider;
	}

}