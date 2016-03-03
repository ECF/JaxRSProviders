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

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configuration;

import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.internal.jaxrs.client.WebResourceFactory;
import org.eclipse.ecf.provider.jaxrs.JaxRSNamespace;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.client.AbstractClientContainer;
import org.eclipse.ecf.remoteservice.client.AbstractRSAClientContainer;
import org.eclipse.ecf.remoteservice.client.AbstractRSAClientService;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;

public class JaxRSClientContainer extends AbstractRSAClientContainer {

	public JaxRSClientContainer() {
		super(JaxRSNamespace.INSTANCE
				.createInstance(new Object[] { URI.create("uuid:" + java.util.UUID.randomUUID().toString()) }));
	}

	protected class JaxRSClientRemoteService extends AbstractRSAClientService {

		public JaxRSClientRemoteService(AbstractClientContainer container,
				RemoteServiceClientRegistration registration) {
			super(container, registration);
		}

		@Override
		protected Object invokeAsync(RSARemoteCall remoteCall) throws ECFException {
			throw new ECFException("invokeAsync not yet implemented implemented");
		}

		@Override
		protected Object invokeSync(RSARemoteCall remoteCall) throws ECFException {
			Method methodToInvoke;
			synchronized (JaxRSClientRemoteService.this) {
				if (jaxRSProxy == null)
					throw new ECFException("invokeRemoteCall:  jaxRSProxy is null");
				methodToInvoke = remoteCall.getReflectMethod();
				if (methodToInvoke == null)
					throw new ECFException("method '" + methodToInvoke + " on jax rs jaxRSProxy could not be found");
			}
			// Now invoke method
			try {
				return methodToInvoke.invoke(this.jaxRSProxy, remoteCall.getParameters());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new ECFException("Invoke failed on jaxrs jaxRSProxy", e);
			}
		}


		@Override
		public void dispose() {
			super.dispose();
			synchronized (JaxRSClientRemoteService.this) {
				this.jaxRSProxy = null;
			}
		}

		protected Object jaxRSProxy;

		@SuppressWarnings("unchecked")
		@Override
		public Object getProxy(ClassLoader cl, @SuppressWarnings("rawtypes") Class[] interfaces) throws ECFException {
			if (interfaces.length == 0)
				throw new ECFException("At least one interface must be provided to create a jaxRSProxy");
			try {
				synchronized (JaxRSClientRemoteService.this) {
					if (jaxRSProxy == null) {
						Configuration config = createJaxRSClientConfiguration();
						Client client = createJaxRSClient(config);
						WebTarget webtarget = getJaxRSWebTarget(client);
						jaxRSProxy = createJaxRSProxy(cl, (Class<Object>) interfaces[0], webtarget);
						if (this.jaxRSProxy == null)
							throw new ECFException("getProxy:  CreateJaxRSProxy returned null.  Cannot create jaxRSProxy");
					}
					return super.createProxy(cl, interfaces);
				}
			} catch (Throwable t) {
				ECFException e = new ECFException(t.getMessage());
				e.setStackTrace(t.getStackTrace());
				throw e;
			}
		}

		protected <T> T createJaxRSProxy(ClassLoader cl, Class<T> interfaceClass,
				WebTarget webTarget) throws ECFException {
			try {
				return WebResourceFactory.newResource(interfaceClass, webTarget);
			} catch (Throwable t) {
				t.printStackTrace();
				throw new ECFException("client could not be create", t);
			}
		}

		protected Configuration createJaxRSClientConfiguration() throws ECFException {
			return null;
		}

		protected Client createJaxRSClient(Configuration configuration) throws ECFException {
			ClientBuilder cb = ClientBuilder.newBuilder();
			if (configuration != null)
				cb.withConfig(configuration);
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
		return new JaxRSClientRemoteService(this, registration);
	}

}
