/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.server;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.ws.rs.HttpMethod;

import org.eclipse.ecf.remoteservice.IExtendedRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.IRegistrationListener;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.RemoteServiceContainerAdapterImpl;
import org.eclipse.ecf.remoteservice.RemoteServiceRegistrationImpl;
import org.eclipse.ecf.remoteservice.RemoteServiceRegistryImpl;
import org.eclipse.equinox.concurrent.future.IExecutor;
import org.osgi.service.http.HttpContext;

public class JaxRSRemoteServiceContainerAdapter extends RemoteServiceContainerAdapterImpl {

	static void throwIfNotJaxRSClass(Class<?> clazz) throws IllegalArgumentException {
		Method[] methods = clazz.getMethods();
		for (Method m : methods) {
			boolean foundHttpMethodAnnotation = false;
			for (Annotation annotation : m.getAnnotations()) {
				HttpMethod http = annotation.annotationType().getAnnotation(HttpMethod.class);
				if (http != null)
					foundHttpMethodAnnotation = true;
			}
			if (!foundHttpMethodAnnotation)
				throw new IllegalArgumentException("Method " + m.getName() + " on " + clazz.getName()
						+ " is not annotated as an java.ws.rs.HttpMethod");
		}
	}

	private final JaxRSServerContainer jaxRSServerContainer;

	public JaxRSRemoteServiceContainerAdapter(JaxRSServerContainer jaxRSServerContainer, IExecutor executor) {
		super(jaxRSServerContainer, executor);
		this.jaxRSServerContainer = jaxRSServerContainer;
	}

	class JaxRSRemoteServiceRegistration extends RemoteServiceRegistrationImpl
			implements IExtendedRemoteServiceRegistration {
		private static final long serialVersionUID = 1825593065080575664L;

		private String servletAlias;

		public JaxRSRemoteServiceRegistration() {
			super(new IRegistrationListener() {
				public void unregister(RemoteServiceRegistrationImpl registration) {
					handleServiceUnregister(registration);
				}
			});
		}

		public void setServletAlias(String alias) {
			this.servletAlias = alias;
		}

		@Override
		public Map<String, Object> getExtraProperties() {
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("ecf.endpoint.id", jaxRSServerContainer.getUrlContext() + servletAlias);
			return result;
		}
	}

	protected RemoteServiceRegistrationImpl createRegistration() {
		return new JaxRSRemoteServiceRegistration();
	}

	@SuppressWarnings("unchecked")
	@Override
	public IRemoteServiceRegistration registerRemoteService(String[] clazzes, final Object service,
			@SuppressWarnings("rawtypes") Dictionary properties) {
		if (service == null)
			throw new NullPointerException("service cannot be null"); //$NON-NLS-1$
		final int size = clazzes.length;

		if (size == 0)
			throw new IllegalArgumentException("service classes list is empty"); //$NON-NLS-1$
		if (size > 1)
			throw new IllegalArgumentException("service classes must be of length 1");

		final String[] copy = new String[clazzes.length];
		for (int i = 0; i < clazzes.length; i++)
			copy[i] = new String(clazzes[i].getBytes());
		clazzes = copy;

		@SuppressWarnings({ "rawtypes" })
		final ClassLoader cl = (ClassLoader) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				return service.getClass().getClassLoader();
			}
		});

		Set<Class<?>> serviceClazzes = new HashSet<Class<?>>();
		for (int i = 0; i < clazzes.length; i++) {
			try {
				final Class<?> serviceClazz = cl == null ? Class.forName(clazzes[i]) : cl.loadClass(clazzes[i]);
				if (!serviceClazz.isInstance(service))
					throw new IllegalArgumentException(
							"Service=" + clazzes[i] + " is not implemented by serviceObject=" + service);
				if (!serviceClazz.isInterface())
					throw new IllegalArgumentException("Service=" + clazzes[i] + " is not an interface");
				throwIfNotJaxRSClass(serviceClazz);
				serviceClazzes.add(serviceClazz);
			} catch (final ClassNotFoundException e) {
				throw new IllegalArgumentException("ClassNotFoundException for " + clazzes[i]);
			}
		}

		RemoteServiceRegistryImpl reg = getRegistry();
		if (reg == null)
			throw new NullPointerException("registry cannot be null"); //$NON-NLS-1$

		JaxRSRemoteServiceRegistration registration = (JaxRSRemoteServiceRegistration) createRegistration();

		// Now register
		synchronized (reg) {
			registration.publish(reg, service, clazzes, properties);
		}
		// Create Servlet Alias
		String servletAlias = jaxRSServerContainer.createServletAlias(registration, service, properties);
		registration.setServletAlias(servletAlias);

		// Create Servlet
		Servlet servlet = jaxRSServerContainer.createServlet(registration, service, properties);

		// Create servletProps
		@SuppressWarnings("rawtypes")
		Dictionary servletProps = jaxRSServerContainer.createServletProperties(registration, service, properties);

		// Create HttpContext
		HttpContext servletContext = jaxRSServerContainer.createServletContext(registration, service, properties);

		try {
			// Register the resource as a servlet
			this.jaxRSServerContainer.registerResource(servletAlias, servlet, servletProps, servletContext);
		} catch (Exception e) {
			synchronized (registry) {
				registry.unpublishService(registration);
			}
			throw e;
		}
		
		fireRemoteServiceListeners(createRegisteredEvent(registration));
		return registration;
	}
}