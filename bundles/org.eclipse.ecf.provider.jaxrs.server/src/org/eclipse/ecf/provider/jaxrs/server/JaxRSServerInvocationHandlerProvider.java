package org.eclipse.ecf.provider.jaxrs.server;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import org.eclipse.ecf.remoteservice.Constants;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.asyncproxy.AsyncReturnUtil;
import org.eclipse.ecf.remoteservice.util.AsyncUtil;

public class JaxRSServerInvocationHandlerProvider {
	private final RSARemoteServiceRegistration registration;

	public JaxRSServerInvocationHandlerProvider(RSARemoteServiceRegistration registration) {
		this.registration = registration;
	}

	protected InvocationHandler createSyncInvocationHandler(Method method) {
		return (proxy, m, args) -> {
			return method.invoke(proxy, args);
		};
	}

	protected InvocationHandler createAsyncInvocationHandler(Method method, long timeout) {
		return (proxy, m, args) -> {
			Object asyncResult = method.invoke(proxy, args);
			return AsyncReturnUtil.convertAsyncToReturn(asyncResult, method.getReturnType(), timeout);
		};
	}

	protected boolean isAsyncReturn(Class<?> returnType) {
		return AsyncUtil.isOSGIAsync(registration.getReference()) && AsyncReturnUtil.isAsyncType(returnType);
	}

	protected long getTimeout() {
		Object val = registration.getProperty(Constants.OSGI_BASIC_TIMEOUT_INTENT);
		// If given then convert to long, if not use IRemoteCall.DEFAULT_TIMEOUT
		return (val == null) ? IRemoteCall.DEFAULT_TIMEOUT
				: (val instanceof String ? Long.valueOf((String) val) : (Long) val);
	}

	public InvocationHandler createInvocationHandler(Method method) {
		Class<?> returnType = method.getReturnType();
		return isAsyncReturn(returnType) ? createAsyncInvocationHandler(method, getTimeout())
				: createSyncInvocationHandler(method);
	}
}