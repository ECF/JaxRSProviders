package org.eclipse.ecf.provider.jaxrs.client;

import java.util.Arrays;
import java.util.Dictionary;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.core.provider.IRemoteServiceContainerInstantiator;

public class JaxRSClientContainerInstantiator extends BaseContainerInstantiator
		implements IRemoteServiceContainerInstantiator {

	public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
		if (Arrays.asList(exporterSupportedConfigs).contains(description.getName()))
			return new String[] { description.getName() };
		return null;
	}

	public static final String[] restIntents = { "passByValue", "exactlyOnce", "ordered", "jaxrs" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

	public String[] getSupportedConfigs(ContainerTypeDescription description) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description, String[] importedConfigs,
			Dictionary exportedProperties) {
		return null;
	}

	public String[] getSupportedIntents(ContainerTypeDescription description) {
		return restIntents;
	}

	@Override
	public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) {
		return new JaxRSClientContainer();
	}

}