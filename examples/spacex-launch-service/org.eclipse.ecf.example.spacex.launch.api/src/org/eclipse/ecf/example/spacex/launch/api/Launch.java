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
