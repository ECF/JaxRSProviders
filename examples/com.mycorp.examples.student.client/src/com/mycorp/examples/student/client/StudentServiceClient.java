/*******************************************************************************
* Copyright (c) 2015 Composent, Inc. and others. All rights reserved. This
* program and the accompanying materials are made available under the terms of
* the Eclipse Public License v1.0 which accompanies this distribution, and is
* available at http://www.eclipse.org/legal/epl-v10.html
*
* Contributors:
*   Composent, Inc. - initial API and implementation
******************************************************************************/
package com.mycorp.examples.student.client;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.mycorp.examples.student.Address;
import com.mycorp.examples.student.Student;
import com.mycorp.examples.student.StudentService;
import com.mycorp.examples.student.Students;

@Component(immediate = true)
public class StudentServiceClient {

	private StudentService studentService;

	@Reference
	void bindStudentService(StudentService service) throws Exception {
		this.studentService = service;
		System.out.println("Discovered student service=" + this.studentService);
		// Get all students
		Students students = studentService.getStudents();
		// Get first student from list
		Student s0 = students.getStudents().get(0);
		// Print out first student
		System.out.println("Student0=" + s0);
		// If there is anyone there, then update
		if (s0 != null) {
			s0.setGrade("Eighth");
			// And update
			System.out.println("Updated Student0=" + studentService.updateStudent(s0));
		}

		// Get student with completablefuture
		studentService.getStudentsCF().whenComplete((s, except) -> {
			if (except != null)
				except.printStackTrace();
			else
				System.out.println("Student=0=" + s.getStudents().get(0));
		});
		// Create a new student
		Student newstudent = studentService.createStudent("April Snow");
		System.out.println("Created student=" + newstudent);
		// when done change the grade to first
		newstudent.setGrade("First");
		Address addr = new Address();
		addr.setStreet("111 NE 1st");
		addr.setCity("Austin");
		addr.setState("Oregon");
		addr.setPostalCode("97200");
		newstudent.setAddress(addr);
		// update
		Student updatednewstudent = studentService.updateStudent(newstudent);
		System.out.println("Updated student=" + updatednewstudent);
		// Then delete new student
		Student deletedstudent = studentService.deleteStudent(updatednewstudent.getId());
		System.out.println("Deleted student=" + deletedstudent);
	}

	void unbindStudentService(StudentService service) throws Exception {
		this.studentService = null;
	}
}
