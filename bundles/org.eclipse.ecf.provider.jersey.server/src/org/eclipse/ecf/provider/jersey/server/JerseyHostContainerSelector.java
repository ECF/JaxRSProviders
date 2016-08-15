/*******************************************************************************
* Copyright (c) 2016 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Erdal Karaca - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import java.util.Map;

import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.URIID;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.HostContainerSelector;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.IHostContainerSelector;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

@Component()
public class JerseyHostContainerSelector extends HostContainerSelector implements IHostContainerSelector {

	private static final String JERSEY_SERVER_URI_PROP = JerseyServerDistributionProvider.JERSEY_SERVER_CONFIG_NAME
			+ "." + JerseyServerDistributionProvider.URI_PARAM;

	public JerseyHostContainerSelector() {
		super(new String[] { JerseyServerDistributionProvider.JERSEY_SERVER_CONFIG_NAME }, true);
	}

	protected boolean matchHostContainerID(@SuppressWarnings("rawtypes") ServiceReference serviceReference,
			Map<String, Object> properties, IContainer container) {
		ID containerID = container.getID();
		if (containerID == null)
			return false;
		if (containerID instanceof URIID) {
			// get uri
			Object propVal = properties.get(JERSEY_SERVER_URI_PROP);
			if (propVal == null)
				propVal = serviceReference.getProperty(JERSEY_SERVER_URI_PROP);
			if (propVal == null)
				propVal = JerseyServerDistributionProvider.URI_DEFAULT;
			if (containerID.getName().equals(propVal))
				return true;
		}
		return false;
	}
}
