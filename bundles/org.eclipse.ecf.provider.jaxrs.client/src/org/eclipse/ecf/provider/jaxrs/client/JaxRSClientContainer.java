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

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.Arrays;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;

import org.eclipse.ecf.core.ContainerConnectException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.identity.ID;
import org.eclipse.ecf.core.identity.Namespace;
import org.eclipse.ecf.core.provider.BaseContainerInstantiator;
import org.eclipse.ecf.core.provider.IRemoteServiceContainerInstantiator;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.remoteservice.IRemoteCall;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.IRemoteServiceCallPolicy;
import org.eclipse.ecf.remoteservice.IRemoteServiceReference;
import org.eclipse.ecf.remoteservice.RemoteServiceID;
import org.eclipse.ecf.remoteservice.client.AbstractClientContainer;
import org.eclipse.ecf.remoteservice.client.AbstractClientService;
import org.eclipse.ecf.remoteservice.client.IRemoteCallable;
import org.eclipse.ecf.remoteservice.client.RemoteCallableFactory;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistry;
import org.osgi.framework.InvalidSyntaxException;

public abstract class JaxRSClientContainer extends AbstractClientContainer {

	public abstract static class JaxRSContainerInstantiator extends BaseContainerInstantiator
			implements IRemoteServiceContainerInstantiator {

		public String[] getImportedConfigs(ContainerTypeDescription description, String[] exporterSupportedConfigs) {
			if (Arrays.asList(exporterSupportedConfigs).contains(description.getName()))
				return new String[] { description.getName() };
			return null;
		}

		public static final String[] restIntents = { "passByValue", "exactlyOnce", "ordered", "jaxrs" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

		public String[] getSupportedConfigs(ContainerTypeDescription description) {
			return null;
		}

		@Override
		public abstract IContainer createInstance(ContainerTypeDescription description, Object[] parameters);

		@SuppressWarnings("rawtypes")
		public Dictionary getPropertiesForImportedConfigs(ContainerTypeDescription description,
				String[] importedConfigs, Dictionary exportedProperties) {
			return null;
		}

		public String[] getSupportedIntents(ContainerTypeDescription description) {
			return restIntents;
		}

	}

	private IRemoteCallable callable;

	protected JaxRSClientContainer(ID id) {
		super(id);
		this.callable = RemoteCallableFactory.createCallable(getID().getName());
	}

	protected JaxRSClientContainer() {
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

	public static boolean isJaxRSAnnotated(Method method) {
		HashSet<String> methods = new HashSet<String>();
		for (Annotation annotation : method.getAnnotations()) {
			HttpMethod http = annotation.annotationType().getAnnotation(HttpMethod.class);
			if (http != null)
				methods.add(http.value());
		}
		if (methods.size() > 0 || ((methods.size() == 0) && method.isAnnotationPresent(Path.class)
				&& method.getReturnType().isInterface()))
			return true;
		return false;
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

	protected class JaxProxyClientRemoteService extends AbstractClientService {

		public JaxProxyClientRemoteService(AbstractClientContainer container,
				RemoteServiceClientRegistration registration) {
			super(container, registration);
		}

		@Override
		protected Object invokeRemoteCall(IRemoteCall call, IRemoteCallable callable) throws ECFException {
			Method methodToInvoke = null;
			synchronized (JaxProxyClientRemoteService.this) {
				if (proxy == null)
					throw new ECFException("Underlying jax proxy is null");
				String methodName = call.getMethod();
				methodToInvoke = methodMap.get(methodName);
				if (methodToInvoke == null)
					throw new ECFException("method '" + methodToInvoke + " on jax proxy");
			}
			// Now invoke method
			// invoke
			try {
				return methodToInvoke.invoke(proxy, call.getParameters());
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				throw new ECFException("Invoke failed on jaxrs proxy", e);
			}
		}

		private Object proxy;
		private Map<String, Method> methodMap = new HashMap<String, Method>();

		@Override
		public Object getProxy(ClassLoader cl, @SuppressWarnings("rawtypes") Class[] interfaces) throws ECFException {
			try {
				synchronized (JaxProxyClientRemoteService.this) {
					if (proxy == null) {
						if (interfaces.length > 1)
							throw new ECFException("Cannot have more than a single JaxRS interface");
						proxy = createJaxRSProxy(cl, interfaces[0]);
						if (this.proxy == null)
							throw new ECFException("createJaxRSProxy returned null.  Cannot create proxy");
						for (Method method : interfaces[0].getMethods())
							if (isJaxRSAnnotated(method))
								methodMap.put(method.getName(), method);
					}
				}
				return super.createProxy(cl, interfaces);
			} catch (Throwable t) {
				throw new ECFException("Cannot create jaxrs proxy");
			}
		}
	}

	protected abstract Object createJaxRSProxy(ClassLoader cl, @SuppressWarnings("rawtypes") Class interfaceClass)
			throws ECFException;

	protected String getConnectedTarget() {
		ID targetID = getConnectedID();
		if (targetID == null)
			return null;
		return targetID.getName();
	}

	@Override
	protected IRemoteService createRemoteService(RemoteServiceClientRegistration registration) {
		return new JaxProxyClientRemoteService(this, registration);
	}

}
