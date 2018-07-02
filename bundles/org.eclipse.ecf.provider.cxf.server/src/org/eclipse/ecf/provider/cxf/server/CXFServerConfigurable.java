package org.eclipse.ecf.provider.cxf.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.Configurable;
import javax.ws.rs.core.Configuration;

@SuppressWarnings("rawtypes")
public class CXFServerConfigurable implements Configurable {

	private Map<String, Object> properties = new HashMap<String, Object>();
	private List<Object> extensions = new ArrayList<Object>();

	@Override
	public Configuration getConfiguration() {
		return new CXFServerConfiguration(extensions, properties);
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