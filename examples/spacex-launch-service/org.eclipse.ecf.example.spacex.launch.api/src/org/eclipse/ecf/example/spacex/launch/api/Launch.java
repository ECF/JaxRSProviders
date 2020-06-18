package org.eclipse.ecf.example.spacex.launch.api;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class) // r/SpaceX uses underscored field names
public class Launch {

	private String flightNumber;
	private String missionName;
	
	public String getFlightNumber() {
		return flightNumber;
	}
	
	public String getMissionName() {
		return missionName;
	}
}
