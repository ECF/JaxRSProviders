package org.eclipse.ecf.provider.jaxrs.server;

import org.eclipse.ecf.core.util.BundleStarter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final String[] dependencies = { "org.eclipse.ecf.provider.jaxrs",
			"com.fasterxml.jackson.jaxrs.jackson-jaxrs-base",
			"com.fasterxml.jackson.jaxrs.jackson-jaxrs-json-provider",
			"jakarta.xml.bind-api",
			"com.fasterxml.jackson.module.jackson-module-jaxb-annotations",
			"javax.annotation",
			"org.glassfish.hk2.external.jakarta.inject",
			"org.glassfish.hk2.osgi-resource-locator",
			"org.glassfish.jersey.core.jersey-common",
			"org.glassfish.jersey.media.jersey-media-json-jackson"
			};
	
	@Override
	public void start(BundleContext context) throws Exception {
		BundleStarter.startDependents(context, dependencies, Bundle.ACTIVE | Bundle.STARTING);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// do nothing
	}

}
