package org.eclipse.ecf.provider.cxf.server;

import org.eclipse.ecf.remoteservice.provider.IRemoteServiceDistributionProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	@Override
	public void start(BundleContext context) throws Exception {
		// Register CXF Server distribution provider
		context.registerService(IRemoteServiceDistributionProvider.class, new CXFServerDistributionProvider(context), null);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
	}

}
