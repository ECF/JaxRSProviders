/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package com.mycorp.examples.student.remoteservice.host;

import jakarta.ws.rs.Path;

import org.osgi.service.component.annotations.Component;

import com.mycorp.examples.student.StudentService;

// The jax-rs path annotation for this service
@Path("/studentservice")
// The OSGi DS (declarative services) component annotation. 
@Component(immediate = true, property = { "service.exported.interfaces=*", "service.exported.intents=osgi.async",
		"service.exported.intents=jaxrs", "osgi.basic.timeout=5000000", "ecf.jaxrs.server.pathPrefix=/rs1" })
public class StudentServiceImpl extends AbstractStudentService implements StudentService {

}
