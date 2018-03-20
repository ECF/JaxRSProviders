package org.eclipse.ecf.provider.jaxrs.server;

import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

@Component(immediate=true)
public class HttpServiceHolder implements IHttpServiceHolder {

	private static final long DEFAULT_TIMEOUT = 30000L;
	
	private BundleContext context;
	
	@Activate
	void activate(BundleContext c) {
		this.context = c;
	}
	
	@Deactivate
	void deactivate() {
		this.context = null;
	}
	
	@Override
	public HttpService getHttpService(long timeout) {
		ServiceTracker<HttpService,HttpService> st = new ServiceTracker<HttpService,HttpService>(context,HttpService.class,null);
		st.open();
		try {
			st.waitForService(timeout);
		} catch (InterruptedException e) {
			return null;
		}
		return st.getService();
	}

	@Override
	public HttpService getHttpService() {
		return getHttpService(DEFAULT_TIMEOUT);
	}

}
