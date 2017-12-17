package com.mycorp.examples.student.remoteservice.host;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.HostContainerSelector;

public class MyHostContainersSelector extends HostContainerSelector {

	public MyHostContainersSelector(String[] defaultConfigTypes, boolean autoCreateContainer) {
		super(defaultConfigTypes, autoCreateContainer);
	}

	@Override
	protected boolean matchHostSupportedConfigTypes(String[] requiredConfigTypes,
			ContainerTypeDescription containerTypeDescription) {
		// if no config type is set the spec requires to create a default
		// endpoint (see section 122.5.1)
		if (requiredConfigTypes == null)
			return true;
		// Get supported config types for this description
		String[] supportedConfigTypes = getSupportedConfigTypes(containerTypeDescription);
		// If it doesn't support anything, return false
		if (supportedConfigTypes == null || supportedConfigTypes.length == 0)
			return false;
		// Turn supported config types for this description into list
		List supportedConfigTypesList = Arrays.asList(supportedConfigTypes);
		List requiredConfigTypesList = Arrays.asList(requiredConfigTypes);
		// We check all of the required config types and make sure
		// that one or more of them are present in the supportedConfigTypes
		boolean result = false;
		for (Iterator i = requiredConfigTypesList.iterator(); i.hasNext();)
			result |= supportedConfigTypesList.contains(i.next());
		return result;
	}
}
