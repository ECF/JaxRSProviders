package org.eclipse.ecf.provider.jaxrs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

import org.eclipse.ecf.core.provider.IContainerInstantiator;
import org.eclipse.ecf.remoteservice.provider.RemoteServiceDistributionProvider;

public class JaxRSDistributionProvider extends RemoteServiceDistributionProvider {

	public static final String JAXRS_COMPONENT_PROPERTY = "jaxrs-component";
	public static final String JAXRS_COMPONENT_PRIORITY_PROPERTY = "jaxrs-component-priority";
	public static final String JAXRS_COMPONENT_CONTRACT_PRIORITY_PROPERTY_ = "jaxrs-component-contract-priority_";
	
	static final Integer DEFAULT_PRIORITY = 0;
	static final Integer NO_PRIORITY = -1;
	
	public static class JaxRSComponent {
		final Object component;
		final Map<Class<?>,Integer> contracts;
		public JaxRSComponent(Object component, Map<Class<?>,Integer> contracts) {
			this.component = component;
			this.contracts = contracts;
		}
		public JaxRSComponent(Object component, Class<?> contract, int priority) {
			this.component = component;
			Map<Class<?>,Integer> contracts = new HashMap<Class<?>,Integer>();
			contracts.put(contract, priority);
			this.contracts = contracts;
		}
		public JaxRSComponent(Object component, Class<?> contract) {
			this(component, contract, NO_PRIORITY);
		}
		public Object getComponent() {
			return this.component;
		}
		public Map<Class<?>,Integer> getContracts() {
			return this.contracts;
		}
	}
	
	private Collection<JaxRSComponent> extensions = new ArrayList<JaxRSComponent>();
	
	public void addJaxRSExtension(JaxRSComponent extension) {
		synchronized (extensions) {
			extensions.add(extension);
		}
	}
	
	public void removeJaxRSExtension(Object component) {
		synchronized (extensions) {
			for(Iterator<JaxRSComponent> it = extensions.iterator(); it.hasNext(); ) {
				JaxRSComponent ext = it.next();
				if (component.equals(ext.component))
					it.remove();
			}
		}
	}
	
	public void addJaxRSExtension(Object instance, Map<Class<?>,Integer> contracts) {
		addJaxRSExtension(new JaxRSComponent(instance,contracts));
	}
	
	public void addJaxRSExtension(Object instance, int priority, Class<?>...contracts) {
		Map<Class<?>,Integer> fullContracts = new HashMap<Class<?>,Integer>();
		for(Class<?> contract: contracts)
			fullContracts.put(contract, priority);
	    addJaxRSExtension(instance,fullContracts);
	}
	
	public void addJaxRSExtension(Object instance, Class<?>... contracts) {
		addJaxRSExtension(instance, NO_PRIORITY, contracts);
	}
	
	public void bindJaxRSExtension(Object instance, @SuppressWarnings("rawtypes") Map serviceProps) {
		// Get objectClass
		List<String> serviceClassNames = Arrays.asList((String[]) serviceProps.get(org.osgi.framework.Constants.OBJECTCLASS));
		// Find contract classes and associated priorities (if any)
		Map<Class<?>,Integer> contracts = new HashMap<Class<?>,Integer>();
		Class<?>[] serviceInstanceClasses = instance.getClass().getInterfaces();
		for(int i=0; i < serviceInstanceClasses.length; i++) {
			Class<?> serviceClass = serviceInstanceClasses[i];
			String serviceClassName = serviceClass.getName();
			// If the service class names list contains the serviceClass name, then
			// we have a contract
			if (serviceClassNames.contains(serviceClassName)) {
				// Get priority for specific contract class
				Object o = serviceProps.get(JAXRS_COMPONENT_CONTRACT_PRIORITY_PROPERTY_+serviceClassName);
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
					contracts.put(serviceClass,  DEFAULT_PRIORITY);
			}
		}
		if (contracts.size() > 0) 
			addJaxRSExtension(instance,contracts);
	}
	
	public void unbindJaxRSExtension(Object instance) {
		removeJaxRSExtension(instance);
	}
	
	public Configurable<?> registerComponents(Configurable<?> configurable) {
		Configurable<?> config = customizeConfigurable(configurable);
		synchronized (extensions) {
			for(JaxRSComponent ext: extensions) {
				List<Class<?>> noPriorityContracts = new ArrayList<Class<?>>();
				Map<Class<?>,Integer> priorityContracts = new HashMap<Class<?>,Integer>();
				for(Iterator<Class<?>> it = ext.contracts.keySet().iterator(); it.hasNext(); ) {
					Class<?> contractClass = it.next();
					Integer priority = ext.contracts.get(contractClass);
					if (priority.equals(NO_PRIORITY)) 
						noPriorityContracts.add(contractClass);
					else
						priorityContracts.put(contractClass, priority);
				}
				if (noPriorityContracts.size() > 0)
					config.register(ext.component, noPriorityContracts.toArray(new Class<?>[noPriorityContracts.size()]));
				if (priorityContracts.size() > 0)
					config.register(ext.component, priorityContracts);
			}
		}
		return config;
	}
	
	protected Configurable<?> customizeConfigurable(Configurable<?> configurable) {
		return configurable;
	}

	public Configuration getConfiguration(Configurable<?> configurable) {
		return registerComponents(configurable).getConfiguration();
	}
	
	public JaxRSDistributionProvider(String name, IContainerInstantiator instantiator, String description,
			boolean server) {
		super(name, instantiator, description, server);
	}

	public JaxRSDistributionProvider(String name, IContainerInstantiator instantiator, String description) {
		super(name, instantiator, description);
	}

	public JaxRSDistributionProvider(String name, IContainerInstantiator instantiator) {
		super(name, instantiator);
	}

}
