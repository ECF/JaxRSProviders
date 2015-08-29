/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jersey.server;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Servlet;
import javax.ws.rs.core.Application;

import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainer;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainerInstantiator;
import org.eclipse.ecf.remoteservice.IRemoteServiceRegistration;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.osgi.service.http.HttpService;

public class JerseyServerContainer extends JaxRSServerContainer {

	public static class Instantiator extends JaxRSServerContainerInstantiator {
		@Override
		public IContainer createInstance(ContainerTypeDescription description, Object[] parameters) {
			// XXX TODO
			
			return new JerseyServerContainer(HttpServiceComponent.getHttpService(), "http://localhost:8080/",
					"/jersey");
		}
	}

	public class JerseyApplication extends Application {
		private Class<?> resourceClass;

		public JerseyApplication(Class<?> resourceClass) {
			this.resourceClass = resourceClass;
		}

		@Override
		public Set<Class<?>> getClasses() {
			Set<Class<?>> results = new HashSet<Class<?>>();
			results.add(this.resourceClass);
			return results;
		}
	}

	public JerseyServerContainer(HttpService httpService, String urlContext, String alias) {
		super(httpService, urlContext, alias);
	}

	protected ResourceConfig createResourceConfig(IRemoteServiceRegistration registration, Object serviceObject,
			@SuppressWarnings("rawtypes") Dictionary properties) {
		return ResourceConfig.forApplication(new JerseyApplication(serviceObject.getClass()));
	}

	@Override
	protected Servlet createServlet(IRemoteServiceRegistration registration, Object serviceObject,
			@SuppressWarnings("rawtypes") Dictionary properties) {
		ResourceConfig rc = createResourceConfig(registration, serviceObject, properties);
		return (rc != null) ? new ServletContainer(rc) : new ServletContainer();
	}

}
