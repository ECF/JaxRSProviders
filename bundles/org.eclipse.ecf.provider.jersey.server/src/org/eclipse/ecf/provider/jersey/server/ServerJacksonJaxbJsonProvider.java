package org.eclipse.ecf.provider.jersey.server;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

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
				writeValue = AsyncReturnUtil.convertAsyncToReturn(value, type, 30000);
			} catch (InvocationTargetException | InterruptedException | ExecutionException | TimeoutException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} 
		super.writeTo(writeValue, type, genericType, annotations, mediaType, httpHeaders, entityStream);
	}
}
