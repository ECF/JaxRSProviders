/*******************************************************************************
* Copyright (c) 2020 Patrick Paulin and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Patrick Paulin - initial API and implementation
******************************************************************************/
package org.eclipse.ecf.example.spacex.launch.api;


import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

public class LaunchServiceTest {

	private static LaunchService launchService;
	
	@Test
	public void testLaunchService() throws Exception {
		List<Launch> launches = launchService.getLaunches();
		assertTrue(launches.size() > 0);
	}

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		startRemoteServices();	
		launchService = getService(LaunchService.class);
	}
	
	static void startRemoteServices() throws Exception {
		startBundle("org.eclipse.ecf.osgi.services.distribution");
		startBundle("org.glassfish.jersey.core.jersey-common");
	}

	static void startBundle(String symbolicName) throws BundleException {
		BundleContext context = FrameworkUtil.getBundle(LaunchServiceTest.class).getBundleContext();
		for (Bundle bundle : context.getBundles()) {
			if (bundle.getSymbolicName().equals(symbolicName)) {
				if (bundle.getState() != Bundle.ACTIVE) {
					bundle.start();
				}
			}
		}
	}

	static <T> T getService(Class<T> clazz) {
		Bundle bundle = FrameworkUtil.getBundle(LaunchServiceTest.class);
		bundle.getBundleContext().getBundles();
		if (bundle != null) {
			ServiceTracker<T, T> st = new ServiceTracker<T, T>(bundle.getBundleContext(), clazz, null);
			st.open();
			if (st != null) {
				try {
					// give the runtime some time to startup
					return st.waitForService(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
}
