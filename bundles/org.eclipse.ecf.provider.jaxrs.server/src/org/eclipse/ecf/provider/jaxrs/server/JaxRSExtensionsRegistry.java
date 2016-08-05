/*******************************************************************************
* Copyright (c) 2016 Erdal Karaca and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Erdal Karaca <erdal.karaca.de@gmail.com> - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.provider.jaxrs.server;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.core.Configurable;

import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

/**
 * A registry that handles JAX-RS extensions. Consumers are expected to call
 * {@link #bindConfigurable(Configurable)} to register the extensions known to
 * this registry. Note that the JAX-RS {@link Configurable} interface has no
 * unregister method, thus, unbinding a configurable is not possible.
 * 
 * @author Erdal Karaca
 *
 */
@Component(service = JaxRSExtensionsRegistry.class)
public class JaxRSExtensionsRegistry {
	private static class Extension {
		public int priority = 0;
		public Object jaxRsComponent;
	}

	private static final String JAX_RS_EXTENSION = "jaxRsExtension";
	private static final String JAX_RS_EXTENSION_PRIO = "bindingPriority";

	private List<HttpService> httpServices = Collections.synchronizedList(new ArrayList<HttpService>());
	private List<WeakReference<Configurable<?>>> configs = Collections
			.synchronizedList(new ArrayList<WeakReference<Configurable<?>>>());
	private ServiceTracker<Object, Object> tracker;

	@Activate
	public void activate(ComponentContext cc) throws InvalidSyntaxException {
		final BundleContext bundleContext = cc.getBundleContext();
		Filter filter = bundleContext
				.createFilter("(&(" + Constants.OBJECTCLASS + "=*)(" + JAX_RS_EXTENSION + "=true))");
		tracker = new ServiceTracker<>(bundleContext, filter, new ServiceTrackerCustomizer<Object, Object>() {

			@Override
			public Object addingService(ServiceReference<Object> reference) {
				Extension extension = new Extension();
				extension.jaxRsComponent = bundleContext.getService(reference);

				try {
					extension.priority = Integer.parseInt((String) reference.getProperty(JAX_RS_EXTENSION_PRIO));
				} catch (NumberFormatException e) {
					extension.priority = 0;
				}

				handleAddition(extension);
				return extension;
			}

			@Override
			public void modifiedService(ServiceReference<Object> reference, Object service) {
			}

			@Override
			public void removedService(ServiceReference<Object> reference, Object service) {
				// we should somehow unregister the service from the
				// Configurable, but the Configurable interface has no
				// unregister methods
			}
		});
		tracker.open();
	}

	private void handleAddition(Extension extension) {
		// lock the list to not miss any configs that might be added while
		// traversing (iterating a synced list is not thread safe)
		synchronized (configs) {
			for (WeakReference<Configurable<?>> weakReference : new ArrayList<>(configs)) {
				Configurable<?> config = weakReference.get();

				if (config == null) {
					configs.remove(weakReference);
				} else {
					registerExtension(config, extension);
				}
			}
		}
	}

	@Deactivate
	public void deactivate() {
		tracker.close();
	}

	@Reference(cardinality = ReferenceCardinality.AT_LEAST_ONE)
	public void bindHttpService(HttpService httpService) {
		if (httpService != null)
			httpServices.add(httpService);
	}

	public void unbindHttpService(HttpService httpService) {
		if (httpService != null)
			httpServices.remove(httpService);
	}

	public List<HttpService> getHttpServices() {
		return Collections.unmodifiableList(httpServices);
	}

	private List<Extension> getJaxRSExtensions() {
		List<Extension> extensions = new ArrayList<>();
		Object[] services = tracker.getServices();

		if (services != null) {
			for (Object serviceObj : services) {
				extensions.add((Extension) serviceObj);
			}
		}

		return extensions;
	}

	/**
	 * Call this method to manage the provided {@link Configurable} instance.
	 * The registry will handle dynamic extension additions. Note that the
	 * provided configurable will be held as a weak reference.
	 * 
	 * @param config
	 *            the configurable to manage by this registry
	 */
	public void bindConfigurable(Configurable<?> config) {
		List<Extension> jaxRSExtensions = getJaxRSExtensions();

		for (Extension extension : jaxRSExtensions) {
			registerExtension(config, extension);
		}

		configs.add(new WeakReference<Configurable<?>>(config));
	}

	private void registerExtension(Configurable<?> config, Extension extension) {
		config.register(extension.jaxRsComponent, extension.priority);
	}

}
