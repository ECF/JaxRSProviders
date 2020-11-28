/*******************************************************************************
* Copyright (c) 2020 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.server;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.WriterInterceptor;
import javax.ws.rs.ext.WriterInterceptorContext;

import org.eclipse.ecf.provider.jaxrs.JaxRSConstants;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.asyncproxy.AsyncReturnUtil;
import org.eclipse.ecf.remoteservice.util.AsyncUtil;

public class JaxRSServerWriterInterceptor implements WriterInterceptor {

	private final RSARemoteServiceRegistration registration;
	
	public JaxRSServerWriterInterceptor(RSARemoteServiceRegistration reg) {
		this.registration = reg;
	}
	
	@Override
	public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
		Object value = context.getEntity();
		// By default set the genericClazz String to
		// the genericType.getTypeName
		Type genericType = context.getGenericType();
		String genericClazz = genericType.getTypeName();
		// If genericType instanceof ParameterizedType
		if (genericType instanceof ParameterizedType) {
			ParameterizedType pt = (ParameterizedType) genericType;
			Type ownerType = pt.getRawType();
			genericClazz = ownerType.getTypeName();
		}
		MultivaluedMap<String,Object> httpHeaders = context.getHeaders();
		if (AsyncUtil.isOSGIAsync(registration.getReference()) && AsyncReturnUtil.isAsyncType(genericClazz)) {
			if (value != null) {
				// Set the httpHeader to the genericClazz
				httpHeaders.add(JaxRSConstants.JAXRS_RESPHEADER_ASYNC_TYPE, genericClazz);
			}
			// this is what changes the subsequent processing
			context.setGenericType(null);
		}
		context.proceed();
	}

}
