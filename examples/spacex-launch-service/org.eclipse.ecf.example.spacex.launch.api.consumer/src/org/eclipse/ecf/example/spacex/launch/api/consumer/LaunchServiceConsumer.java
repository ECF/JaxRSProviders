package org.eclipse.ecf.example.spacex.launch.api.consumer;

import java.util.List;

import org.eclipse.ecf.example.spacex.launch.api.Launch;
import org.eclipse.ecf.example.spacex.launch.api.LaunchService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component
public class LaunchServiceConsumer {

	@Reference
	void bindLaunchService(LaunchService launchService) {
		// Call some methods on injected launch service
		System.out.println("Calling sync getLaunches");
		List<Launch> launches = launchService.getLaunches();
		launches.forEach(launch -> System.out.println("\tLaunch="+ launch));
		// Calling asynchronously
		System.out.println("Calling async getLaunches");
		launchService.getLaunchesAsync().whenComplete((aLaunches, e) -> {
			if (e != null) {
				System.out.println("getLaunches failed with exception");
				e.printStackTrace();
			} else {
				aLaunches.forEach(l -> System.out.println("\tLaunch="+ l));
			}
		});
		System.out.println("Calling getLaunch(99)");
		Launch l = launchService.getLaunch(99);
		System.out.println("Launch(99) info: "+l);
		System.out.println("Finished activate");
	}

}
