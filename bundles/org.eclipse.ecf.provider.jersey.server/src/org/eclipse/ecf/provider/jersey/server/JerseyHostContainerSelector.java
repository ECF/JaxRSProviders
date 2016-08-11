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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import org.eclipse.ecf.osgi.services.remoteserviceadmin.HostContainerSelector;
import org.eclipse.ecf.osgi.services.remoteserviceadmin.IHostContainerSelector;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Component;

@Component()
public class JerseyHostContainerSelector extends HostContainerSelector implements IHostContainerSelector {

	public JerseyHostContainerSelector() {
		super(new String[] { JerseyServerDistributionProvider.JERSEY_SERVER_CONFIG_NAME }, true);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Collection selectExistingHostContainers(ServiceReference serviceReference,
			Map<String, Object> overridingProperties, String[] serviceExportedInterfaces,
			String[] serviceExportedConfigs, String[] serviceIntents) {
		return Collections.EMPTY_LIST;
	}
}
