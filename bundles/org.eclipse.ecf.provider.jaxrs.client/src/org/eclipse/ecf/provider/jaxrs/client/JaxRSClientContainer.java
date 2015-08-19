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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.internal.jaxrs.client.WebResourceFactory;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteCallListener;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceCallPolicy;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.RemoteCall;
import org.eclipse.ecf.remoteservice.RemoteServiceID;
import org.eclipse.ecf.remoteservice.client.AbstractClientContainer;
import org.eclipse.ecf.remoteservice.client.AbstractClientService;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.RemoteCallableFactory;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistry;
import org.osgi.framework.InvalidSyntaxException;

public class JaxRSClientContainer extends AbstractClientContainer {

	private IRemoteCallable callable;

	public JaxRSClientContainer(ID id) {
		super(id);
		this.callable = RemoteCallableFactory.createCallable(getID().getName());
	}

	public JaxRSClientContainer() {
		this(JaxRSClientNamespace.INSTANCE
				.createInstance(new Object[] { URI.create("uuid:" + java.util.UUID.randomUUID().toString()) }));
	}

	@Override
	public Namespace getConnectNamespace() {
		return JaxRSClientNamespace.INSTANCE;
	}

	@Override
	public boolean setRemoteServiceCallPolicy(IRemoteServiceCallPolicy policy) {
		return false;
	}

	@Override
	protected String prepareEndpointAddress(IRemoteCall call, IRemoteCallable callable) {
		return null;
	}

	protected class JaxRemoteServiceClientRegistration extends RemoteServiceClientRegistration {

		@SuppressWarnings({ "unchecked", "rawtypes" })
		public JaxRemoteServiceClientRegistration(Namespace namespace, IRemoteCallable[] restCalls,
				Dictionary properties, RemoteServiceClientRegistry registry) {
			super(namespace, restCalls, properties, registry);
			ID cID = getConnectedID();
			if (cID != null)
				this.containerId = cID;
			long rsId = 0;
			this.serviceID = new RemoteServiceID(namespace, containerId, rsId);
			if (rsId > 0) {
				if (this.properties == null)
					this.properties = new Hashtable();
				this.properties.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, new Long(rsId));
			}
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		public JaxRemoteServiceClientRegistration(Namespace namespace, String[] classNames,
				IRemoteCallable[][] restCalls, Dictionary properties, RemoteServiceClientRegistry registry) {
			super(namespace, classNames, restCalls, properties, registry);
			ID cID = getConnectedID();
			if (cID != null)
				this.containerId = cID;
			long rsId = 0;
			this.serviceID = new RemoteServiceID(namespace, containerId, rsId);
			if (rsId > 0) {
				if (this.properties == null)
					this.properties = new Hashtable();
				this.properties.put(org.eclipse.ecf.remoteservice.Constants.SERVICE_ID, new Long(rsId));
			}
		}

		@Override
		public IRemoteCallable lookupCallable(IRemoteCall remoteCall) {
			return callable;
		}
	}

	@Override
	protected RemoteServiceClientRegistration createRestServiceRegistration(String[] clazzes,
			IRemoteCallable[][] callables, @SuppressWarnings("rawtypes") Dictionary properties) {
		return new JaxRemoteServiceClientRegistration(getConnectNamespace(), clazzes, callables, properties,
				this.registry);
	}

	@Override
	public IRemoteServiceReference[] getRemoteServiceReferences(ID target, ID[] idFilter, String clazz, String filter)
			throws InvalidSyntaxException, ContainerConnectException {
		IRemoteServiceReference[] refs = super.getRemoteServiceReferences(target, idFilter, clazz, filter);
		if (refs == null) {
			registerCallables(new String[] { clazz }, new IRemoteCallable[][] { { callable } }, null);
			refs = super.getRemoteServiceReferences(target, idFilter, clazz, filter);
		}
		return refs;
	}

	protected class JaxRSProxyClientRemoteService extends AbstractClientService {

		public JaxRSProxyClientRemoteService(AbstractClientContainer container,
				RemoteServiceClientRegistration registration) {
			super(container, registration);
		}

		@Override
		public void dispose() {
			super.dispose();
			synchronized (JaxRSProxyClientRemoteService.this) {
				this.proxy = null;
			}
		}

		@Override
		protected Object invokeRemoteCall(IRemoteCall call, IRemoteCallable callable) throws ECFException {
			Method methodToInvoke = null;
			synchronized (JaxRSProxyClientRemoteService.this) {
				if (proxy == null)
					throw new ECFException("invokeRemoteCall:  proxy is null");
				if (!(call instanceof JaxRSRemoteCall))
					throw new ECFException("invokeRemoteCall call must be of type JaxRSRemoteCall");
				methodToInvoke = ((JaxRSRemoteCall) call).getJavaMethod();
				if (methodToInvoke == null)
					throw new ECFException("method '" + methodToInvoke + " on jax rs proxy could not be found");
			}
			// Now invoke method
			try {
				return methodToInvoke.invoke(this.proxy, call.getParameters());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new ECFException("Invoke failed on jaxrs proxy", e);
			}
		}

		private Object proxy;

		protected Map<String, Method> createMethodMap(@SuppressWarnings("rawtypes") Class interfaceClass)
				throws ECFException {
			Map<String, Method> results = new HashMap<String, Method>();
			for (Method method : interfaceClass.getMethods())
				results.put(interfaceClass.getName() + "." + method.getName(), method);
			return results;
		}

		protected Client createAndConfigureJaxRSClient() throws ECFException {
			ClientBuilder cb = ClientBuilder.newBuilder();
			Client client = cb.build();
			return client;
		}

		protected WebTarget getJaxRSWebTarget(Client client) throws ECFException {
			return client.target(getConnectedTarget());
		}

		protected String getConnectedTarget() {
			ID targetID = getConnectedID();
			if (targetID == null)
				return null;
			return targetID.getName();
		}

		@Override
		public Object getProxy(ClassLoader cl, @SuppressWarnings("rawtypes") Class[] interfaces) throws ECFException {
			if (interfaces.length == 0)
				throw new ECFException("At least one interface must be provided to create a proxy");
			try {
				synchronized (JaxRSProxyClientRemoteService.this) {
					if (proxy == null) {
						Client client = createAndConfigureJaxRSClient();
						WebTarget webtarget = getJaxRSWebTarget(client);
						proxy = createJaxRSProxy(cl, interfaces[0], webtarget);
						if (this.proxy == null)
							throw new ECFException("getProxy:  CreateJaxRSProxy returned null.  Cannot create proxy");
					}
					return super.createProxy(cl, interfaces);
				}
			} catch (Throwable t) {
				ECFException e = new ECFException(t.getMessage());
				e.setStackTrace(t.getStackTrace());
				throw e;
			}
		}

		protected class JaxRSRemoteCall extends RemoteCall {
			private final Method method;

			public JaxRSRemoteCall(Method method, String methodName, Object[] methodArgs, long timeout) {
				super(methodName, methodArgs, timeout);
				this.method = method;
			}

			public Method getJavaMethod() {
				return method;
			}
		}

		protected IRemoteCall createRemoteCall(Method method, String callMethod, Object[] callParameters,
				long callTimeout) {
			return new JaxRSRemoteCall(method, callMethod, callParameters, callTimeout);
		}

		protected RemoteCall getAsyncRemoteCall(Method method, String callMethod, Object[] callParameters) {
			return new JaxRSRemoteCall(method, callMethod, callParameters, IRemoteCall.DEFAULT_TIMEOUT);
		}

		protected Object invokeAsync(final Method method, final Object[] args) throws Throwable {
			final String invokeMethodName = getAsyncInvokeMethodName(method);
			final AsyncArgs asyncArgs = getAsyncArgs(method, args);
			RemoteCall remoteCall = getAsyncRemoteCall(method, invokeMethodName, asyncArgs.getArgs());
			IRemoteCallListener listener = asyncArgs.getListener();
			return (listener != null) ? callAsyncWithResult(remoteCall, listener)
					: callFuture(remoteCall, asyncArgs.getReturnType());
		}

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object resultObject = null;
			try {
				// If the method is from Class Object, or from
				// IRemoteServiceProxy
				// then return result by directly invoking on the proxy
				resultObject = invokeObject(proxy, method, args);
			} catch (Throwable t) {
				handleProxyException(
						"Exception invoking local Object method on remote service proxy=" + getRemoteServiceID(), t); //$NON-NLS-1$
			}
			if (resultObject != null)
				return resultObject;

			try {
				if (isAsync(proxy, method, args))
					return invokeAsync(method, args);
			} catch (Throwable t) {
				handleProxyException("Exception invoking async method on remote service proxy=" + getRemoteServiceID(), //$NON-NLS-1$
						t);
			}
			// Get the callMethod, callParameters, and callTimeout
			final String callMethod = getCallMethodNameForProxyInvoke(method, args);
			final Object[] callParameters = getCallParametersForProxyInvoke(callMethod, method, args);
			final long callTimeout = getCallTimeoutForProxyInvoke(callMethod, method, args);
			// Create IRemoteCall instance from method, parameters, and timeout
			final IRemoteCall remoteCall = createRemoteCall(method, callMethod, callParameters, callTimeout);

			// Invoke synchronously
			try {
				return invokeSync(remoteCall);
			} catch (ECFException e) {
				handleInvokeSyncException(method.getName(), e);
				// If the above method doesn't throw as it should, we return
				// null
				return null;
			}
		}

	}

	@Override
	protected IRemoteService createRemoteService(RemoteServiceClientRegistration registration) {
		return new JaxRSProxyClientRemoteService(this, registration);
	}

	@SuppressWarnings("unchecked")
	protected Object createJaxRSProxy(ClassLoader cl, @SuppressWarnings("rawtypes") Class interfaceClass,
			WebTarget webTarget) throws ECFException {
		try {
			return WebResourceFactory.newResource(interfaceClass, webTarget);
		} catch (Throwable t) {
			t.printStackTrace();
			throw new ECFException("client could not be create", t);
		}
	}

}
