package org.eclipse.ecf.provider.jersey.server;

import javax.ws.rs.core.Configuration;
import javax.ws.rs.core.Feature;
import javax.ws.rs.core.FeatureContext;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;
import org.glassfish.jersey.CommonProperties;
import org.glassfish.jersey.internal.InternalProperties;
import org.glassfish.jersey.internal.util.PropertiesHelper;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.jackson.internal.FilteringJacksonJaxbJsonProvider;
import org.glassfish.jersey.jackson.internal.JacksonFilteringFeature;
import org.glassfish.jersey.message.filtering.EntityFilteringFeature;

import com.fasterxml.jackson.jaxrs.base.JsonMappingExceptionMapper;
import com.fasterxml.jackson.jaxrs.base.JsonParseExceptionMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;

public class ServerJacksonFeature extends JacksonFeature {

		private static final String JSON_FEATURE = JacksonFeature.class.getSimpleName();
		
		private RSARemoteServiceRegistration reg;
		
		public ServerJacksonFeature(RSARemoteServiceRegistration reg) {
			this.reg = reg;
		}
		
	    @Override
	    public boolean configure(final FeatureContext context) {
	        final Configuration config = context.getConfiguration();

	        final String jsonFeature = CommonProperties.getValue(config.getProperties(), config.getRuntimeType(),
	                InternalProperties.JSON_FEATURE, JSON_FEATURE, String.class);
	        // Other JSON providers registered.
	        if (!JSON_FEATURE.equalsIgnoreCase(jsonFeature)) {
	            return false;
	        }

	        // Disable other JSON providers.
	        context.property(PropertiesHelper.getPropertyNameForRuntime(InternalProperties.JSON_FEATURE, config.getRuntimeType()),
	                JSON_FEATURE);

	        // Register Jackson.
	        if (!config.isRegistered(JacksonJaxbJsonProvider.class)) {
	            // add the default Jackson exception mappers
	            context.register(JsonParseExceptionMapper.class);
	            context.register(JsonMappingExceptionMapper.class);

	            if (EntityFilteringFeature.enabled(config)) {
	                context.register(JacksonFilteringFeature.class);
	                context.register(FilteringJacksonJaxbJsonProvider.class, MessageBodyReader.class, MessageBodyWriter.class);
	            } else {
	                context.register(new ServerJacksonJaxbJsonProvider(this.reg), MessageBodyReader.class, MessageBodyWriter.class);
	            }
	        }

	        return true;
	    }
}
