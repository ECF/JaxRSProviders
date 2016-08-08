package org.eclipse.ecf.provider.jersey.server.example.student.extensions;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyReader;

import org.osgi.service.component.annotations.Component;

import com.mycorp.examples.student.Student;

@Component(property = "jaxrs-configurable-target=org.eclipse.ecf.provider.jersey.server.JerseyServerDistributionProvider")
public class CustomMessageBodyReaderExtension implements MessageBodyReader<Student> {

	@Override
	public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Student readFrom(Class<Student> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, String> httpHeaders, InputStream entityStream)
			throws IOException, WebApplicationException {
		// TODO Auto-generated method stub
		return null;
	}

}
