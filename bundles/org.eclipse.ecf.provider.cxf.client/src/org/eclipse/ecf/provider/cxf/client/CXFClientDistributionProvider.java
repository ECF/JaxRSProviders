/*******************************************************************************
* Copyright (c) 2016 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Erdal Karaca - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.cxf.client;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;

import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.core.util.ECFException;
import org.eclipse.ecf.provider.jaxrs.JaxRSContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.JaxRSDistributionProvider;
import org.eclipse.ecf.provider.jaxrs.client.JaxRSClientContainer;
import org.eclipse.ecf.remoteservice.IRemoteService;
import org.eclipse.ecf.remoteservice.client.RemoteServiceClientRegistration;
import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

@Component(immediate=true,service=IRemoteServiceDistributionProvider.class)
public class CXFClientDistributionProvider extends JaxRSDistributionProvider {

	public static final String CLIENT_PROVIDER_NAME = "ecf.jaxrs.cxf.client";
	public static final String SERVER_PROVIDER_NAME = "ecf.jaxrs.cxf.server";

	public class CXFClientConfiguration implements Configuration {

		private List<Object> extensions;
		private Map<String,Object> properties;
		
		public CXFClientConfiguration(List<Object> extensions, Map<String,Object> props) {
			this.extensions = extensions;
			this.properties = props;
		}
		@Override
		public Set<Class<?>> getClasses() {
			return null;
		}

		@Override
		public Map<Class<?>, Integer> getContracts(Class<?> arg0) {
			return null;
		}

		@SuppressWarnings({ "unchecked", "rawtypes" })
		@Override
		public Set<Object> getInstances() {
			return new HashSet(extensions);
		}

		public List<Object> getExtensions() {
			return extensions;
		}
		
		@Override
		public Map<String, Object> getProperties() {
			return properties;
		}

		@Override
		public Object getProperty(String arg0) {
			return properties.get(arg0);
		}

		@Override
		public Collection<String> getPropertyNames() {
			return properties.keySet();
		}

		@Override
		public RuntimeType getRuntimeType() {
			return null;
		}

		@Override
		public boolean isEnabled(Feature arg0) {
			return false;
		}

		@Override
		public boolean isEnabled(Class<? extends Feature> arg0) {
			return false;
		}

		@Override
		public boolean isRegistered(Object arg0) {
			return extensions.contains(arg0);
		}

		@Override
		public boolean isRegistered(Class<?> arg0) {
			return false;
		}
		
	}
	@SuppressWarnings("rawtypes")
	public class CXFClientConfigurable implements Configurable {

		private Map<String,Object> properties = new HashMap<String,Object>();
		private List<Object> extensions = new ArrayList<Object>();
		
		@Override
		public Configuration getConfiguration() {
			return new CXFClientConfiguration(extensions,properties);
		}

		@Override
		public Configurable property(String arg0, Object arg1) {
			properties.put(arg0, arg1);
			return this;
		}

		@Override
		public Configurable register(Class arg0) {
			return null;
		}

		@Override
		public Configurable register(Object arg0) {
			this.extensions.add(arg0);
			return this;
		}

		@Override
		public Configurable register(Class arg0, int arg1) {
			return null;
		}

		@Override
		public Configurable register(Class arg0, Class... arg1) {
			return null;
		}

		@Override
		public Configurable register(Class arg0, Map arg1) {
			return null;
		}

		@Override
		public Configurable register(Object arg0, int arg1) {
			this.extensions.add(arg0);
			return this;
		}

		@Override
		public Configurable register(Object arg0, Class... arg1) {
			this.extensions.add(arg0);
			return this;
		}

		@Override
		public Configurable register(Object arg0, Map arg1) {
			this.extensions.add(arg0);
			return this;
		}
		
	}
	public CXFClientDistributionProvider() {
		super(CLIENT_PROVIDER_NAME, new JaxRSContainerInstantiator(SERVER_PROVIDER_NAME, CLIENT_PROVIDER_NAME) {
			@Override
			public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
					final Configuration configuration) {
				return new JaxRSClientContainer() {
					@Override
					protected IRemoteService createRemoteService(RemoteServiceClientRegistration registration) {
						return new JaxRSClientRemoteService(this, registration) {
							@SuppressWarnings("unchecked")
							@Override
							protected Object createJaxRSProxy(ClassLoader cl,
									@SuppressWarnings("rawtypes") Class interfaceClass, WebTarget webTarget)
									throws ECFException {
								List<Object> providers = new ArrayList<Object>();
								providers.add(new JacksonJaxbJsonProvider());
								providers.add(new ObjectMapperContextResolverComponent());
								return JAXRSClientFactory.create(getConnectedTarget(), interfaceClass, ((CXFClientConfiguration) configuration).getExtensions());
							}

							@Override
							protected Client createJaxRSClient(Configuration configuration) throws ECFException {
								return null;
							}

							@Override
							protected WebTarget getJaxRSWebTarget(Client client) throws ECFException {
								return null;
							}
						};
					}
				};
			}
		});
		addJaxRSComponent(new ObjectMapperContextResolverComponent(), ContextResolver.class);
	}

	@SuppressWarnings("rawtypes")
	@Override
	protected Configurable createConfigurable() {
		CXFClientConfigurable configurable = new CXFClientConfigurable();
		configurable.register(new JacksonJaxbJsonProvider());
		configurable.register(new ObjectMapperContextResolverComponent());
		return configurable;
	}

}
