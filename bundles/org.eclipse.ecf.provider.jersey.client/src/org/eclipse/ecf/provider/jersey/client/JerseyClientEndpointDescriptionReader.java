/*******************************************************************************
* Copyright (c) 2020 Patrick Paulin and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Patrick Paulin - initial implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.client;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.eclipse.ecf.osgi.services.remoteserviceadmin.IEndpointDescriptionReader;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientEndpointDescriptionReader;
import org.osgi.service.component.annotations.Component;

/**
 * Extension of the JaxRS endpoint reader that sets JAX-RS defaults and allows
 * for profiles.
 * <p>
 * This extension adds defaults specific to Jersey and also registers the reader
 * as an OSGi service. This reader should be picked up first by the ECF
 * framework because the default reader is registered with the minimum service
 * ranking.
 */
@Component(service = IEndpointDescriptionReader.class)
public class JerseyClientEndpointDescriptionReader extends JaxRSClientEndpointDescriptionReader {

	protected Map<String, Object> addDefaultProperties(Map<String, Object> baseProperties) {
		Map<String, Object> propertiesIncludingDefaults = super.addDefaultProperties(baseProperties);

		if (!propertiesIncludingDefaults.containsKey("service.imported.configs")) {
			propertiesIncludingDefaults.put("service.imported.configs",
					new String[] { JerseyClientDistributionProvider.SERVER_PROVIDER_NAME });
		} else {
			String[] importedConfigs = (String[]) propertiesIncludingDefaults.get("service.imported.configs");
			List<String> importedConfigsAsList = Arrays.asList(importedConfigs);

			if (!importedConfigsAsList.contains(JerseyClientDistributionProvider.SERVER_PROVIDER_NAME)) {
				importedConfigsAsList.add(JerseyClientDistributionProvider.SERVER_PROVIDER_NAME);
				String[] updatedImportedConfigs = importedConfigsAsList
						.toArray(new String[importedConfigsAsList.size()]);
				propertiesIncludingDefaults.put("service.imported.configs", updatedImportedConfigs);
			}
		}

		return propertiesIncludingDefaults;
	}
}
