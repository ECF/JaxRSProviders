/*******************************************************************************
* Copyright (c) 2018 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package com.mycorp.examples.student;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/studentservice")
public interface StudentService {

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/students")
	List<Student> getStudents();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/studentscf")
	CompletableFuture<List<Student>> getStudentsCF();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/students/{studentId}")
	Student getStudent(@PathParam("studentId") String id);

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/students/{studentName}")
	Student createStudent(@PathParam("studentName") String studentName);

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/students")
	Student updateStudent(Student student);

	@DELETE
	@Path("/students/{studentId}")
	@Produces(MediaType.APPLICATION_JSON)
	Student deleteStudent(@PathParam("studentId") String studentId);
}
