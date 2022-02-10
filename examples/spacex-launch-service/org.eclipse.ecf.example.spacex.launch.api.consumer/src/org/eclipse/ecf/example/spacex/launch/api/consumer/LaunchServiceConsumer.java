package org.eclipse.ecf.example.spacex.launch.api.consumer;

import java.util.List;

import org.eclipse.ecf.example.spacex.launch.api.Launch;
import org.eclipse.ecf.example.spacex.launch.api.LaunchService;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class LaunchServiceConsumer {

	@Reference
	private LaunchService launchService;

	@Activate
	void activate() {
		// Call some methods on injected launch service
		System.out.println("Calling sync getLaunches");
		List<Launch> launches = launchService.getLaunches();
		launches.forEach(launch -> System.out.println("\tLaunch=" + launch));
		// Calling asynchronously
		System.out.println("Calling async getLaunches");
		launchService.getLaunchesAsync().whenComplete((aLaunches, e) -> {
			if (e != null) {
				System.out.println("getLaunches failed with exception");
				e.printStackTrace();
			} else {
				aLaunches.forEach(l -> System.out.println("\tLaunch=" + l));
			}
		});
		if (launches.size() > 0) {
			System.out.println("Calling getLaunch for first launch");
			Launch l = launchService.getLaunch(1);
			System.out.println("Launch(1) info: " + l);
			System.out.println("Calling getLaunch for last launch");
			l = launchService.getLaunch(launches.size() - 1);
			System.out.println("Launch(" + (launches.size() - 1) + ") info: " + l);
			System.out.println("Finished activate");
		}
	}

}
