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

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.IDCreateException;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.identity.URIID;

public class JaxRSNamespace extends Namespace {

	private static final long serialVersionUID = -3848279615939604280L;
	public static final String NAME = "ecf.namespace.jaxrs";
	public static JaxRSNamespace INSTANCE;

	public JaxRSNamespace() {
		super(NAME, "JaxRS Namespace");
		INSTANCE = this;
	}

	@Override
	public ID createInstance(Object[] parameters) throws IDCreateException {
		try {
			URI uri = null;
			if (parameters[0] instanceof URI)
				uri = (URI) parameters[0];
			else if (parameters[0] instanceof String)
				uri = URI.create((String) parameters[0]);
			if (uri == null)
				throw new IllegalArgumentException("the first parameter must be of type String or URI");
			return new URIID(INSTANCE, uri);
		} catch (Exception e) {
			throw new IDCreateException("Could not create JaxRS ID", e); //$NON-NLS-1$
		}
	}

	@Override
	public String getScheme() {
		return "ecf.jaxrs";
	}

}
