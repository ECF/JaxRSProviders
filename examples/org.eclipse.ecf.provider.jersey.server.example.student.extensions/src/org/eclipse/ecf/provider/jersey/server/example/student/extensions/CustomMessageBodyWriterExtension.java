package org.eclipse.ecf.provider.jersey.server.example.student.extensions;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import org.osgi.service.component.annotations.Component;

import com.mycorp.examples.student.Student;

@Component(property = "jaxrs-configurable-target=org.eclipse.ecf.provider.jersey.server.JerseyServerDistributionProvider")
public class CustomMessageBodyWriterExtension implements MessageBodyWriter<Student> {

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// TODO Auto-generated method stub
		return mediaType.isCompatible(MediaType.APPLICATION_XML_TYPE);
	}

	@Override
	public long getSize(Student t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeTo(Student t, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
			throws IOException, WebApplicationException {
		// TODO Auto-generated method stub
		
	}

}
