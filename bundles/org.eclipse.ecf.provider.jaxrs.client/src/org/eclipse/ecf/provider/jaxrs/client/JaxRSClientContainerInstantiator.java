/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.client;

import java.util.Arrays;
import java.util.List;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.provider.jaxrs.JaxRSAbstractContainerInstantiator;

public abstract class JaxRSClientContainerInstantiator extends JaxRSAbstractContainerInstantiator {

	protected String serverContainerType;

	public JaxRSClientContainerInstantiator() {

	}

	public JaxRSClientContainerInstantiator(String serverContainerType) {
		this.serverContainerType = serverContainerType;
	}

	@Override
	public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
		List<String> esc = Arrays.asList(exporterSupportedConfigs);
		String dName = description.getName();
		if ((serverContainerType != null && esc.contains(this.serverContainerType)) || esc.contains(dName))
			return new String[] { description.getName() };
		return null;
	}

	@Override
	public String[] getSupportedConfigs(ContainerTypeDescription description) {
		return null;
	}

}