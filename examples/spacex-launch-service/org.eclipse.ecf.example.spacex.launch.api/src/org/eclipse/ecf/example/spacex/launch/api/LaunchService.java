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

import java.util.List;
import java.util.concurrent.CompletableFuture;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

/**
 * Client for the r/SpaceX Launch Service. Specifications for this service can be found at:
 * 
 * https://documenter.getpostman.com/view/2025350/RWaEzAiG?version=latest
 * 	
 * @author Patrick Paulin
 */
@Path("/launches")
public interface LaunchService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public List<Launch> getLaunches();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/")
	public CompletableFuture<List<Launch>> getLaunchesAsync();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/latest")
	public Launch getLatestLaunch();
}