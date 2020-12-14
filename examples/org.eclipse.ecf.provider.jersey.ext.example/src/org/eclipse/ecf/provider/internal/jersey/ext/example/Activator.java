package org.eclipse.ecf.provider.internal.jersey.ext.example;

import java.net.URI;
import java.util.Map;

import jakarta.ws.rs.core.Configurable;
import jakarta.ws.rs.core.Configuration;

import org.eclipse.ecf.core.ContainerCreateException;
import org.eclipse.ecf.core.ContainerTypeDescription;
import org.eclipse.ecf.core.IContainer;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerContainerInstantiator;
import org.eclipse.ecf.provider.jaxrs.server.JaxRSServerDistributionProvider;
import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.glassfish.jersey.server.ResourceConfig;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	public static final String EXT_JERSEY_SERVER_CONFIG = "ecf.jaxrs.jersey.server.ext";

	@Override
	public void start(BundleContext context) throws Exception {
		// This sets up the 'ecf.jaxrs.jersey.server.ext' remote service provider
		context.registerService(IRemoteServiceDistributionProvider.class, new JaxRSServerDistributionProvider(
				EXT_JERSEY_SERVER_CONFIG, new JaxRSServerContainerInstantiator(EXT_JERSEY_SERVER_CONFIG) {
					@Override
					public IContainer createInstance(ContainerTypeDescription description, Map<String, ?> parameters,
							Configuration configuration) throws ContainerCreateException {
						URI uri = getUri(parameters, EXT_JERSEY_SERVER_CONFIG);
						checkOSGIIntents(description, uri, parameters);
						return new ExtJerseyServerContainer(description.getName(), createJaxRSID(uri), context,
								(ResourceConfig) configuration);
					}

					@Override
					protected boolean supportsOSGIConfidentialIntent(ContainerTypeDescription description) {
						return true;
					}

					@Override
					protected boolean supportsOSGIPrivateIntent(ContainerTypeDescription description) {
						return true;
					}

					@Override
					protected boolean supportsOSGIAsyncIntent(ContainerTypeDescription description) {
						return true;
					}
				}, "Extended Jersey Server Remote Service Provider", true) {
			@Override
			protected Configurable<?> createConfigurable() {
				return new ResourceConfig();
			}
		}, null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
