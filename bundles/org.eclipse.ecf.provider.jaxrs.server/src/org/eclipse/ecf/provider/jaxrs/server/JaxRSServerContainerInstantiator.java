/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.server;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.provider.jaxrs.JaxRSAbstractContainerInstantiator;

public abstract class JaxRSServerContainerInstantiator extends JaxRSAbstractContainerInstantiator {
	public JaxRSServerContainerInstantiator() {
	}

	@Override
	public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
		return null;
	}

	@Override
	public String[] getSupportedConfigs(ContainerTypeDescription description) {
		return new String[] { description.getName() };
	}

}