package org.eclipse.ecf.example.jersey.server.objectmapper;

import javax.ws.rs.ext.ContextResolver;

import org.osgi.service.component.annotations.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component(immediate=true,property = {
"jaxrs-service-exported-config-target=ecf.jaxrs.jersey.server"})
public class MyObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

	private ObjectMapper mapper = null;

	public MyObjectMapperContextResolver() {
		super();
		// Set fail on unknown properties to false
		mapper = new ObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public ObjectMapper getContext(Class<?> type) {
		if (mapper.canDeserialize(mapper.constructType(type)) && mapper.canSerialize(type)) {
			return mapper;
		}
		return null;
	}
}
