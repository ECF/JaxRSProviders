package org.eclipse.ecf.example.spacex.launch.api.consumer;

import org.eclipse.ecf.example.spacex.launch.api.Launch;
import org.eclipse.ecf.example.spacex.launch.api.LaunchService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class LaunchServiceConsumer {

	@Reference
	void bindLaunchService(LaunchService launchService) {
		// Call some methods on injected launch service
		System.out.println("Calling getLatestLaunch");
		Launch latestLaunch = launchService.getLatestLaunch();
		System.out.println("latestLaunch=" + latestLaunch);
		// Calling asynchronously
		System.out.println("Calling async getLaunches");
		launchService.getLaunchesAsync().whenComplete((launches, e) -> {
			if (e != null) {
				System.out.println("getLaunches failed with exception");
				e.printStackTrace();
			} else {
				for (Launch launch : launches) {
					System.out.println("\tLaunch=" + launch);
				}
			}
		});
		System.out.println("Finished activate");
	}

}
