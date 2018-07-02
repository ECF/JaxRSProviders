package org.eclipse.ecf.provider.cxf.server;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.RuntimeType;
import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;

public class CXFServerConfiguration implements Configuration {

	private List<Object> extensions;
	private Map<String, Object> properties;

	public CXFServerConfiguration(List<Object> extensions, Map<String, Object> props) {
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