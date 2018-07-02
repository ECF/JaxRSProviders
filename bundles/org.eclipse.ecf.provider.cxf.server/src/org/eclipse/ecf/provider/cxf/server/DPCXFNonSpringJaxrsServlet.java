package org.eclipse.ecf.provider.cxf.server;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.ws.rs.core.Application;

import org.apache.cxf.Bus;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;
import org.apache.cxf.jaxrs.servlet.CXFNonSpringJaxrsServlet;
import org.apache.cxf.jaxrs.utils.ResourceUtils;
import org.eclipse.ecf.provider.jaxrs.server.ServerJacksonJaxbJsonProvider;
import org.eclipse.ecf.remoteservice.RSARemoteServiceContainerAdapter.RSARemoteServiceRegistration;

public class DPCXFNonSpringJaxrsServlet extends CXFNonSpringJaxrsServlet {

	private static final long serialVersionUID = -2618572428261717260L;

	private RSARemoteServiceRegistration registration;
	private CXFServerConfiguration config;

	public DPCXFNonSpringJaxrsServlet(final RSARemoteServiceRegistration registration,
			CXFServerConfiguration config) {
		super(new Application() {
			@Override
			public Set<Class<?>> getClasses() {
				Set<Class<?>> results = new HashSet<Class<?>>();
				results.add(registration.getService().getClass());
				return results;
			}
		});
		this.registration = registration;
		this.config = config;
	}

	@Override
	protected void createServerFromApplication(ServletConfig servletConfig) throws ServletException {
		Bus bus = getBus();
		Application app = getApplication();
		JAXRSServerFactoryBean bean = ResourceUtils.createApplication(app, isIgnoreApplicationPath(servletConfig),
				getStaticSubResolutionValue(servletConfig), isAppResourceLifecycleASingleton(app, servletConfig),
				bus);
		bean.setApplication(app);
		List<Object> extensions = new ArrayList<Object>(config.getExtensions());
		extensions.add(new ServerJacksonJaxbJsonProvider(this.registration));
		bean.setProviders(extensions);
		bean.create();
	}
}