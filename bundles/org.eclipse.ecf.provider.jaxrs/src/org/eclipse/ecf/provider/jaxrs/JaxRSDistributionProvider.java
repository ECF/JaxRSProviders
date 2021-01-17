/*******************************************************************************
* Copyright (c) 2020 Composent, Inc. and Erdal Karaca. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
*   Erdal Karaca - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.ParamConverterProvider;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.WriterInterceptor;

import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.remoteservice.provider.RemoteServiceDistributionProvider;

public abstract class JaxRSDistributionProvider extends RemoteServiceDistributionProvider {

	public static final String JAXRS_COMPONENT_TARGET_PROPERTY = "jaxrs-configurable-target";
	public static final String JAXRS_COMPONENT_CONFIG_TARGET_PROPERTY = "jaxrs-service-exported-config-target";
	public static final String JAXRS_COMPONENT_PRIORITY_PROPERTY = "jaxrs-component-priority";
	public static final String JAXRS_COMPONENT_CONTRACT_PRIORITY_PROPERTY_ = "jaxrs-component-contract-priority_";

	public static final String JAXRS_COMPONENT_INTENTS_PROPERTY = "jaxrs-component-intents";

	static final Integer DEFAULT_PRIORITY = javax.ws.rs.Priorities.USER;
	static final Integer NO_PRIORITY = -1;

	public static class JaxRSComponent {
		final Object component;
		final Map<Class<?>, Integer> contracts;

		public JaxRSComponent(Object component, Map<Class<?>, Integer> contracts) {
			this.component = component;
			this.contracts = contracts;
		}

		public JaxRSComponent(Object component, Class<?> contract, int priority) {
			this.component = component;
			Map<Class<?>, Integer> contracts = new HashMap<Class<?>, Integer>();
			contracts.put(contract, priority);
			this.contracts = contracts;
		}

		public JaxRSComponent(Object component, Class<?> contract) {
			this(component, contract, NO_PRIORITY);
		}

		public Object getComponent() {
			return this.component;
		}

		public Map<Class<?>, Integer> getContracts() {
			return this.contracts;
		}
	}

	private Collection<JaxRSComponent> components = new ArrayList<JaxRSComponent>();

	protected JaxRSDistributionProvider() {
	}

	@Override
	protected RemoteServiceDistributionProvider setInstantiator(IContainerInstantiator instantiator) {
		if (instantiator instanceof JaxRSContainerInstantiator)
			((JaxRSContainerInstantiator) instantiator).setDistributionProvider(this);
		return super.setInstantiator(instantiator);
	}

	protected JaxRSDistributionProvider(String name, IContainerInstantiator instantiator, String description,
			boolean server) {
		super(name, instantiator, description, server);
	}

	protected JaxRSDistributionProvider(String name, IContainerInstantiator instantiator, String description) {
		super(name, instantiator, description);
	}

	protected JaxRSDistributionProvider(String name, IContainerInstantiator instantiator) {
		super(name, instantiator);
	}

	protected void addJaxComponent(JaxRSComponent extension) {
		synchronized (components) {
			components.add(extension);
		}
	}

	protected void addJaxRSComponent(Object instance, Map<Class<?>, Integer> contracts) {
		addJaxComponent(new JaxRSComponent(instance, contracts));
	}

	protected void addJaxRSComponent(Object instance, int priority, Class<?>... contracts) {
		Map<Class<?>, Integer> fullContracts = new HashMap<Class<?>, Integer>();
		for (Class<?> contract : contracts)
			fullContracts.put(contract, priority);
		addJaxRSComponent(instance, fullContracts);
	}

	protected void addJaxRSComponent(Object instance, Class<?>... contracts) {
		addJaxRSComponent(instance, NO_PRIORITY, contracts);
	}

	protected void removeJaxComponent(Object component) {
		synchronized (components) {
			for (Iterator<JaxRSComponent> it = components.iterator(); it.hasNext();) {
				JaxRSComponent ext = it.next();
				if (component.equals(ext.component))
					it.remove();
			}
		}
	}

	protected boolean isValidTarget(Object target, String value) {
		if (target instanceof String) {
			String val = (String) target;
			if (val.equals(value))
				return true;
			else
				return false;
		}
		return false;
	}

	protected boolean isValidComponentTarget(Object componentTarget) {
		return isValidTarget(componentTarget, this.getClass().getName());
	}

	protected boolean isValidConfigTarget(Object configTarget) {
		return isValidTarget(configTarget, getName());
	}

	protected boolean isValidComponent(Object instance, @SuppressWarnings("rawtypes") Map serviceProps) {
		if (instance != null && serviceProps != null) {
			Object o = serviceProps.get(JAXRS_COMPONENT_TARGET_PROPERTY);
			if (o != null)
				return isValidComponentTarget(o);
			o = serviceProps.get(JAXRS_COMPONENT_CONFIG_TARGET_PROPERTY);
			if (o != null)
				return isValidConfigTarget(o);
			return true;
		}
		return false;
	}

	protected void bindJaxComponent(Object instance, @SuppressWarnings("rawtypes") Map serviceProps) {
		if (isValidComponent(instance, serviceProps)) {
			// Get objectClass
			List<String> serviceClassNames = Arrays
					.asList((String[]) serviceProps.get(org.osgi.framework.Constants.OBJECTCLASS));
			// Find contract classes and associated priorities (if any)
			Map<Class<?>, Integer> contracts = new HashMap<Class<?>, Integer>();
			Class<?>[] serviceInstanceClasses = instance.getClass().getInterfaces();
			for (int i = 0; i < serviceInstanceClasses.length; i++) {
				Class<?> serviceClass = serviceInstanceClasses[i];
				String serviceClassName = serviceClass.getName();
				// If the service class names list contains the serviceClass
				// name,
				// then
				// we have a contract
				if (serviceClassNames.contains(serviceClassName)) {
					// Get priority for specific contract class
					Object o = serviceProps.get(JAXRS_COMPONENT_CONTRACT_PRIORITY_PROPERTY_ + serviceClassName);
					// If not there, then get priority for any/all classes
					if (o == null)
						o = serviceProps.get(JAXRS_COMPONENT_PRIORITY_PROPERTY);
					// If not there, then no priority specified
					if (o == null)
						o = NO_PRIORITY;
					// If we have a valid priority then put in contracts list.
					if (o != null && o instanceof Integer)
						contracts.put(serviceClass, (Integer) o);
					else
						contracts.put(serviceClass, DEFAULT_PRIORITY);
				}
			}
			if (contracts.size() > 0) {
				addJaxRSComponent(instance, contracts);
				addJaxRSComponentIntents(serviceProps);
			}
		}
	}

	protected List<String> jaxRSComponentIntents = Collections.synchronizedList(new ArrayList<String>());

	protected void addJaxRSComponentIntents(Map serviceProps) {
		Object o = serviceProps.get(JAXRS_COMPONENT_INTENTS_PROPERTY);
		if (o != null) {
			String[] is = null;
			if (o instanceof String[]) {
				is = (String[]) o;
			} else if (o instanceof String) {
				String i = (String) o;
				is = i.split(",");
			} else if (o instanceof List) {
				@SuppressWarnings("rawtypes")
				List l = (List) o;
				is = (String[]) l.toArray();
			}
			jaxRSComponentIntents.addAll(Arrays.asList(is));
		}
	}

	protected void unbindJaxRSComponent(Object instance) {
		removeJaxComponent(instance);
	}

	protected void unbindJaxComponent(Object instance) {
		removeJaxComponent(instance);
	}

	protected Configurable<?> registerComponents(Configurable<?> configurable) {
		synchronized (components) {
			for (JaxRSComponent ext : components) {
				List<Class<?>> noPriorityContracts = new ArrayList<Class<?>>();
				Map<Class<?>, Integer> priorityContracts = new HashMap<Class<?>, Integer>();
				for (Iterator<Class<?>> it = ext.contracts.keySet().iterator(); it.hasNext();) {
					Class<?> contractClass = it.next();
					Integer priority = ext.contracts.get(contractClass);
					if (priority.equals(NO_PRIORITY))
						noPriorityContracts.add(contractClass);
					else
						priorityContracts.put(contractClass, priority);
				}
				if (noPriorityContracts.size() > 0)
					configurable.register(ext.component,
							noPriorityContracts.toArray(new Class<?>[noPriorityContracts.size()]));
				if (priorityContracts.size() > 0)
					configurable.register(ext.component, priorityContracts);
			}
		}
		return configurable;
	}

	@SuppressWarnings("rawtypes")
	protected abstract Configurable createConfigurable();

	protected Configuration getConfiguration() {
		@SuppressWarnings("rawtypes")
		Configurable configurable = createConfigurable();
		return configurable != null ? getConfiguration(configurable) : null;
	}

	protected Configuration getConfiguration(Configurable<?> configurable) {
		return registerComponents(configurable).getConfiguration();
	}

	@SuppressWarnings("rawtypes")
	protected void bindMessageBodyWriter(MessageBodyWriter instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindMessageBodyWriter(MessageBodyWriter instance) {
		unbindJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindMessageBodyReader(MessageBodyReader instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindMessageBodyReader(MessageBodyReader instance) {
		unbindJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindContextResolver(ContextResolver instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindContextResolver(ContextResolver instance) {
		unbindJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindExceptionMapper(ExceptionMapper instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	@SuppressWarnings("rawtypes")
	protected void unbindExceptionMapper(ExceptionMapper instance) {
		unbindJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindFeature(Feature instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	protected void unbindFeature(Feature instance) {
		unbindJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindReaderInterceptor(ReaderInterceptor instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	protected void unbindReaderInterceptor(ReaderInterceptor instance) {
		unbindJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindWriterInterceptor(WriterInterceptor instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	protected void unbindWriterInterceptor(WriterInterceptor instance) {
		unbindJaxComponent(instance);
	}

	@SuppressWarnings("rawtypes")
	protected void bindParamConverterProvider(ParamConverterProvider instance, Map serviceProps) {
		bindJaxComponent(instance, serviceProps);
	}

	protected void unbindParamConverter(ParamConverterProvider instance) {
		unbindJaxComponent(instance);
	}

	public String[] getJaxRSComponentIntents() {
		return jaxRSComponentIntents.toArray(new String[jaxRSComponentIntents.size()]);
	}
}
