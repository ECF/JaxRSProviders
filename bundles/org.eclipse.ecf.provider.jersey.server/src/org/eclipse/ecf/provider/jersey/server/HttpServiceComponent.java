/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import org.osgi.service.http.HttpService;

public class HttpServiceComponent {

	private static HttpService httpService;

	public static HttpService getHttpService() {
		return httpService;
	}

	void bindHttpService(HttpService service) {
		httpService = service;
	}

	void unbindHttpService(HttpService service) {
		httpService = null;
	}
}
