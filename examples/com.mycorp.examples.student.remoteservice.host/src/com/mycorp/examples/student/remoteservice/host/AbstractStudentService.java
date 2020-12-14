/*******************************************************************************
* Copyright (c) 2019 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package com.mycorp.examples.student.remoteservice.host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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

import com.mycorp.examples.student.Address;
import com.mycorp.examples.student.Student;

public class AbstractStudentService {

	// Provide a map-based storage of students
	private static Map<String, Student> students = Collections.synchronizedMap(new HashMap<String, Student>());
	// Create a single student and add to students map
	static {
		Student s = new Student("Joe Senior");
		s.setId(UUID.randomUUID().toString());
		s.setGrade("First");
		Address a = new Address();
		a.setCity("New York");
		a.setState("NY");
		a.setPostalCode("11111");
		a.setStreet("111 Park Ave");
		s.setAddress(a);
		students.put(s.getId(), s);
	}

	// Implementation of StudentService based upon the students map
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/students")
	public List<Student> getStudents() {
		return new ArrayList<Student>(students.values());
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/studentscf")
	public CompletableFuture<List<Student>> getStudentsCF() {
		CompletableFuture<List<Student>> cf = new CompletableFuture<List<Student>>();
		cf.complete(new ArrayList<Student>(students.values()));
		return cf;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/students/{studentId}")
	public Student getStudent(@PathParam("studentId") String id) {
		return students.get(id);
	}

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/students/{studentName}")
	public Student createStudent(@PathParam("studentName") String studentName) {
		if (studentName == null)
			return null;
		synchronized (students) {
			Student s = new Student(studentName);
			s.setId(UUID.randomUUID().toString());
			students.put(s.getId(), s);
			return s;
		}
	}

	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Path("/students")
	public Student updateStudent(Student student) {
		Student result = null;
		if (student != null) {
			String id = student.getId();
			if (id != null) {
				synchronized (students) {
					result = students.get(student.getId());
					if (result != null) {
						String newName = student.getName();
						if (newName != null)
							result.setName(newName);
						result.setGrade(student.getGrade());
						result.setAddress(student.getAddress());
					}
				}
			}
		}
		return result;
	}

	@DELETE
	@Path("/students/{studentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Student deleteStudent(@PathParam("studentId") String studentId) {
		return students.remove(studentId);
	}

}
