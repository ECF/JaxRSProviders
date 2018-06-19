/*******************************************************************************
* Copyright (c) 2018 Composent, Inc.. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.server;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import org.eclipse.ecf.provider.jaxrs.JaxRSConstants;
import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.asyncproxy.AsyncReturnUtil;
import org.eclipse.ecf.remoteservice.util.AsyncUtil;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class ServerJacksonJaxbJsonProvider extends JacksonJaxbJsonProvider {

	private RSARemoteServiceRegistration reg;

	public ServerJacksonJaxbJsonProvider(RSARemoteServiceRegistration reg) {
		this.reg = reg;
	}

	@Override
	public void writeTo(Object value, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream) throws IOException {
		Object writeValue = value;
		if (reg != null && AsyncUtil.isOSGIAsync(reg.getReference()) && AsyncReturnUtil.isAsyncType(type)) {
			try {
				Object to = reg.getProperty(Constants.OSGI_BASIC_TIMEOUT_INTENT);
				Long timeout = 30000L;
				if (to != null) {
					if (to instanceof Long)
						timeout = (Long) to;
					else if (to instanceof String)
						timeout = Long.valueOf((String) to);
				}
				writeValue = AsyncReturnUtil.convertAsyncToReturn(value, type, timeout);
				if (writeValue != null) {
					type = writeValue.getClass();
					httpHeaders.add(JaxRSConstants.JAXRS_RESPHEADER_ASYNC_TYPE, type.getName());
				}
				genericType = null;
			} catch (Exception e) {
				throw new IOException("Could not convert async return type because of exception: " + e.getMessage());
			}
		}
		super.writeTo(writeValue, type, genericType, annotations, mediaType, httpHeaders, entityStream);
	}
}
