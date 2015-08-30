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

import java.util.Dictionary;
import java.util.Map;

import javax.ws.rs.core.Configuration;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.core.provider.IRemoteServiceContainerInstantiator;

public abstract class JaxRSAbstractContainerInstantiator extends BaseContainerInstantiator
		implements IRemoteServiceContainerInstantiator {

	public static final String CONFIG_PARAM = ".configuration";

	@Override
	public abstract String[] getImportedConfigs(ContainerTypeDescription description,
			String[] exporterSupportedConfigs);

	@Override
	public abstract String[] getSupportedConfigs(ContainerTypeDescription description);

	public static final String[] restIntents = { "passByValue", "exactlyOnce", "ordered", "jaxrs" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	@SuppressWarnings("rawtypes")
	public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description, String[] importedConfigs,
			Dictionary exportedProperties) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Class[][] getSupportedParameterTypes(ContainerTypeDescription description) {
		return new Class[][] { { Map.class } };
	}

	@Override
	public String[] getSupportedAdapterTypes(ContainerTypeDescription description) {
		return new String[] { org.eclipse.ecf.remoteservice.IRemoteServiceContainerAdapter.class.getName() };
	}

	public String[] getSupportedIntents(ContainerTypeDescription description) {
		return restIntents;
	}

	protected Configuration getConfigurationFromParams(ContainerTypeDescription description, Object[] parameters) {
		return getMapParameter(parameters, description.getName() + CONFIG_PARAM, Configuration.class, null);
	}

	public abstract IContainer createInstance(ContainerTypeDescription description, Object[] parameters,
			Configuration configuration);

	@Override
	public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) {
		return createInstance(description, parameters, getConfigurationFromParams(description, parameters));
	}

}