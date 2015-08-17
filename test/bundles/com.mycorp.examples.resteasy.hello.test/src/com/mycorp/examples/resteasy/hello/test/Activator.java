package com.mycorp.examples.resteasy.hello.test;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import com.javacodegeeks.resteasy.ISampleService;

public class Activator implements BundleActivator, ServiceTrackerCustomizer<ISampleService,ISampleService> {

	private ServiceTracker<ISampleService,ISampleService> tracker;
	private BundleContext context;
	
	@Override
	public void start(BundleContext context) throws Exception {
		this.context = context;
		tracker = new ServiceTracker<ISampleService,ISampleService>(context,ISampleService.class, this);
		tracker.open();
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		if (tracker != null) {
			tracker.close();
			tracker = null;
		}
		this.context = null;
	}

	@Override
	public ISampleService addingService(ServiceReference<ISampleService> reference) {
		System.out.println("Got service reference="+reference);
		ISampleService service = context.getService(reference);
		System.out.println("got service="+service);
		// Call hello
		if (service != null)
			try {
				System.out.println("Hello returns="+service.hello());
			} catch (Throwable t) {
				t.printStackTrace();
			}
		return service;
	}

	@Override
	public void modifiedService(ServiceReference<ISampleService> reference, ISampleService service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removedService(ServiceReference<ISampleService> reference, ISampleService service) {
		context.ungetService(reference);
	}

}
