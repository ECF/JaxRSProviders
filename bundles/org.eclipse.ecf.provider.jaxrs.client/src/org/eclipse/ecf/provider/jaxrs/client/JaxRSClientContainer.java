/*******************************************************************************
* Copyright (c) 2018 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.client;

import java.io.InvalidObjectException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Configuration;
import jakarta.ws.rs.ext.ContextResolver;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.internal.jaxrs.client.WebResourceFactory;
import org.eclipse.ecf.provider.jaxrs.ObjectMapperContextResolver;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.asyncproxy.AsyncReturnUtil;
import org.eclipse.ecf.remoteservice.client.AbstractClientContainer;
import org.eclipse.ecf.remoteservice.client.AbstractRSAClientContainer;
import org.eclipse.ecf.remoteservice.client.AbstractRSAClientService;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.events.IRemoteCallCompleteEvent;

public class JaxRSClientContainer extends AbstractRSAClientContainer {

	public static final int JACKSON_DEFAULT_PRIORITY = Integer
			.valueOf(System.getProperty(JaxRSClientContainer.class.getName() + ".jacksonDefaultPriority", "1"));
	protected Configuration configuration;
	protected int jacksonPriority = JACKSON_DEFAULT_PRIORITY;

	protected List<JaxRSClientRemoteService> remoteServices = Collections
			.synchronizedList(new ArrayList<JaxRSClientRemoteService>());

	public JaxRSClientContainer(ID containerID, Configuration configuration, int jacksonPriority) {
		super(containerID);
		this.configuration = configuration;
		this.jacksonPriority = jacksonPriority;
	}

	@Override
	public void dispose() {
		super.dispose();
		synchronized (this.remoteServices) {
			this.remoteServices.forEach(c -> c.dispose());
		}
		this.configuration = null;
	}

	protected class JaxRSClientRemoteService extends AbstractRSAClientService {

		protected Map<Class<?>, Object> interfaceProxyMap = Collections
				.synchronizedMap(new HashMap<Class<?>, Object>());

		protected Client client;

		public JaxRSClientRemoteService(AbstractClientContainer container,
				RemoteServiceClientRegistration registration) {
			super(container, registration);
		}

		@Override
		public void dispose() {
			super.dispose();
			if (client != null) {
				client.close();
				client = null;
			}
			interfaceProxyMap.clear();
		}

		protected Object getProxyForInterface(Class<?> intfClass) {
			return this.interfaceProxyMap.get(intfClass);
		}

		protected Object invokeViaProxy(RSARemoteCall call) throws Exception {
			Method m = call.getReflectMethod();
			Object proxy = getProxyForInterface(m.getDeclaringClass());
			if (proxy == null)
				throw new InvalidObjectException("Proxy for remote method=" + m.getName() + " not found");
			return m.invoke(proxy, call.getParameters());
		}

		@Override
		protected Callable<Object> getSyncCallable(RSARemoteCall call) {
			return () -> invokeViaProxy(call);
		}

		@Override
		protected Callable<IRemoteCallCompleteEvent> getAsyncCallable(RSARemoteCall call) {
			return () -> createRCCESuccess(AsyncReturnUtil.convertAsyncToReturn(invokeViaProxy(call),
					call.getReflectMethod().getReturnType(), 0));
		}

		@Override
		public Object getProxy(ClassLoader cl, @SuppressWarnings("rawtypes") Class[] interfaces) throws ECFException {
			if (interfaces.length == 0)
				throw new ECFException("At least one interface must be provided to create a jaxRSProxy");
			try {
				// If nothing in the interface -> proxy map
				if (this.interfaceProxyMap.size() == 0) {
					// Create configuration
					Configuration config = createJaxRSClientConfiguration();
					// create client
					client = createJaxRSClient(config, cl);
					// get WebTarget from client
					WebTarget webtarget = getJaxRSWebTarget(client);
					// For all interfaces create proxy and associate with interface class
					// Can then be used at invoke time to get right proxy for given method
					// invocation
					for (Class<?> clazz : interfaces)
						this.interfaceProxyMap.put(clazz, createJaxRSProxy(clazz, webtarget));
				}
				return super.createProxy(cl, interfaces);
			} catch (Exception t) {
				ECFException e = new ECFException(t.getMessage());
				e.setStackTrace(t.getStackTrace());
				throw e;
			}
		}

		protected <T> T createJaxRSProxy(Class<T> interfaceClass, WebTarget webTarget) throws Exception {
			return WebResourceFactory.newResource(interfaceClass, webTarget, isOSGIAsync());
		}

		protected Configuration createJaxRSClientConfiguration() throws ECFException {
			return configuration;
		}

		protected Client createJaxRSClient(Configuration configuration) throws ECFException {
			return createJaxRSClient(configuration, this.getClass().getClassLoader());
		}

		protected Client createJaxRSClient(Configuration configuration, ClassLoader cl) throws ECFException {
			ClientBuilder cb = ClientBuilder.newBuilder();
			
			boolean isContextResolverNeeded = true;
			if (configuration != null) {
				cb.withConfig(configuration);
				
				for (Object registeredInstance : configuration.getInstances()) {
					if (registeredInstance instanceof ContextResolver) {
						isContextResolverNeeded = false;
					}
				}
			}
			
			if (isContextResolverNeeded)
				cb.register(new ObjectMapperContextResolver(), ContextResolver.class);
			
			cb.register(new JaxRSClientJacksonFeature(getRegistration(), cl), jacksonPriority);
			return cb.build();
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

	}

	@Override
	protected IRemoteService createRemoteService(RemoteServiceClientRegistration registration) {
		JaxRSClientRemoteService rs = new JaxRSClientRemoteService(this, registration);
		this.remoteServices.add(rs);
		return rs;
	}

}
