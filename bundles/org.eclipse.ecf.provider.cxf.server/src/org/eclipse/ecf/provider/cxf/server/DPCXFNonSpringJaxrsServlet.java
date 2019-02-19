/*******************************************************************************
* Copyright (c) 2018 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.cxf.server;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.MultivaluedMap;

import org.apache.cxf.feature.Feature;
import org.apache.cxf.jaxrs.JAXRSInvoker;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.lifecycle.ResourceProvider;
import org.apache.cxf.jaxrs.lifecycle.SingletonResourceProvider;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.cxf.message.Exchange;
import org.apache.cxf.service.invoker.Invoker;
import org.eclipse.ecf.provider.jaxrs.JaxRSConstants;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerInvocationHandlerProvider;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.eclipse.ecf.remoteservice.asyncproxy.AsyncReturnUtil;
import org.eclipse.ecf.remoteservice.util.AsyncUtil;

public class DPCXFNonSpringJaxrsServlet extends CXFNonSpringJaxrsServlet {

	private static final long serialVersionUID = -2618572428261717260L;

	private final RSARemoteServiceRegistration registration;
	private final CXFServerConfigurable configurable;

	public class DPCXFApplication extends Application {

		private final Set<Class<?>> resourceClasses;
		private final Set<Object> singletons;

		public DPCXFApplication() throws ServletException {
			Object obj = registration.getService();
			Class<?> resourceClass = obj.getClass();
			setClassLoader(resourceClass.getClassLoader());
			resourceClasses = new HashSet<Class<?>>();
			resourceClasses.add(resourceClass);
			singletons = new HashSet<Object>();
			singletons.add(obj);
		}

		@Override
		public Set<Class<?>> getClasses() {
			return resourceClasses;
		}

		@Override
		public Set<Object> getSingletons() {
			return singletons;
		}

	}

	public DPCXFNonSpringJaxrsServlet(final RSARemoteServiceRegistration registration,
			CXFServerConfigurable configurable) {
		super(new Application());
		this.registration = registration;
		this.configurable = configurable;
	}

	class AsyncResponse {
		private Object response;
		private Class<?> returnType;

		public AsyncResponse(Object response, Class<?> returnType) {
			this.response = response;
			this.returnType = returnType;
		}
	}

	class DPCXFInvocationHandlerProvider extends JaxRSServerInvocationHandlerProvider {
		public DPCXFInvocationHandlerProvider(RSARemoteServiceRegistration registration) {
			super(registration);
		}

		protected InvocationHandler createAsyncInvocationHandler(Method method, long timeout) {
			return (proxy, m, args) -> {
				Object asyncResult = method.invoke(proxy, args);
				Class<?> returnType = method.getReturnType();
				return new AsyncResponse(AsyncReturnUtil.convertAsyncToReturn(asyncResult, returnType, timeout),
						returnType);
			};
		}
	}

	class DPCXFInvoker extends JAXRSInvoker {

		private final DPCXFInvocationHandlerProvider provider;

		public DPCXFInvoker() {
			provider = new DPCXFInvocationHandlerProvider(registration);
		}

		@Override
		protected Object performInvocation(Exchange exchange, Object serviceObject, Method m, Object[] paramArray)
				throws Exception {
			paramArray = insertExchange(m, paramArray, exchange);
			try {
				return provider.createInvocationHandler(m).invoke(serviceObject, m, paramArray);
			} catch (Throwable e) {
				throw new InvocationTargetException(e);
			}
		}

	}

	class DPCXFContainerResponseFilter implements ContainerResponseFilter {

		@Override
		public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
				throws IOException {
			Object entity = responseContext.getEntity();
			if (entity instanceof AsyncResponse) {
				AsyncResponse asyncResponse = (AsyncResponse) entity;
				Class<?> returnType = asyncResponse.returnType;
				MultivaluedMap<String, Object> httpHeaders = responseContext.getHeaders();
				if (AsyncUtil.isOSGIAsync(registration.getReference()) && AsyncReturnUtil.isAsyncType(returnType)) {
					httpHeaders.add(JaxRSConstants.JAXRS_RESPHEADER_ASYNC_TYPE, returnType.getName());
				}
				responseContext.setEntity(asyncResponse.response);
			}
		}

	}

	public class DPJAXRSServerFactoryBean extends JAXRSServerFactoryBean {
		@Override
		protected Invoker createInvoker() {
			return new DPCXFInvoker();
		}
	}

	protected JAXRSServerFactoryBean createJAXRSServerFactoryBean() {
		return new DPJAXRSServerFactoryBean();
	}

	@Override
	protected List<? extends Feature> getFeatures(ServletConfig servletConfig, String splitChar)
			throws ServletException {
		List<? extends Feature> feat = super.getFeatures(servletConfig, splitChar);
		List<? extends Feature> result = new ArrayList<Feature>(feat);
		// add any other features here
		return result;
	}

	@Override
	protected void createServerFromApplication(ServletConfig servletConfig) throws ServletException {
		Application app = new DPCXFApplication();

		configurable.register(new DPCXFContainerResponseFilter(), ContainerResponseFilter.class);

		List<Class<?>> resourceClasses = new ArrayList<>();
		Map<Class<?>, ResourceProvider> map = new HashMap<>();
		List<Feature> features = new ArrayList<>();

		for (Object o : app.getSingletons()) {
			ResourceProvider rp = new SingletonResourceProvider(o);
			for (Class<?> c : app.getClasses()) {
				resourceClasses.add(c);
				map.put(c, rp);
			}
		}

		JAXRSServerFactoryBean bean = createJAXRSServerFactoryBean();
		bean.setBus(getBus());
		bean.setAddress("/");

		bean.setResourceClasses(resourceClasses);
		bean.setFeatures(features);

		for (Map.Entry<Class<?>, ResourceProvider> entry : map.entrySet()) {
			bean.setResourceProvider(entry.getKey(), entry.getValue());
		}

		Map<String, Object> appProps = app.getProperties();
		if (appProps != null) {
			bean.getProperties(true).putAll(appProps);
		}

		bean.setApplication(app);

		CXFServerConfiguration configuration = (CXFServerConfiguration) this.configurable.getConfiguration();
		bean.setProviders(new ArrayList<Object>(configuration.getExtensions()));

		bean.create();
	}

}