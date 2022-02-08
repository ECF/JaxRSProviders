package org.eclipse.ecf.provider.jersey.server;

import org.eclipse.ecf.core.util.BundleStarter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	private static final String[] dependents = { 
			"org.eclipse.ecf.provider.jaxrs.server",
			"javax.inject",
			"javassist",
			"javax.servlet",
			"javax.validation.api",
			"com.sun.activation.javax.activation",
			"org.glassfish.hk2.api",
			"org.glassfish.hk2.external.aopalliance-repackaged", 
			"org.glassfish.hk2.locator",
			"org.glassfish.hk2.utils",
			"org.glassfish.jersey.containers.jersey-container-servlet",
			"org.glassfish.jersey.containers.jersey-container-servlet-core",
			"org.glassfish.jersey.core.jersey-common",
			"org.glassfish.jersey.core.jersey-client",
			"org.glassfish.jersey.core.jersey-server",
			"org.glassfish.jersey.ext.jersey-entity-filtering",
			"org.glassfish.jersey.inject.jersey-hk2"};

	@Override
	public void start(BundleContext context) throws Exception {
		BundleStarter.startDependents(context, dependents, Bundle.RESOLVED | Bundle.STARTING);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		// TODO Auto-generated method stub

	}

}
