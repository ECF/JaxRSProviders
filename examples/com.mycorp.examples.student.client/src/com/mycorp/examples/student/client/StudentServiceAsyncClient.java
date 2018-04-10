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

import com.mycorp.examples.student.Student;
import com.mycorp.examples.student.StudentServiceAsync;

@Component(immediate=true)
public class StudentServiceAsyncClient {

	private StudentServiceAsync studentService;

	@Reference
	void bindStudentServiceAsync(StudentServiceAsync service) throws Exception {
		this.studentService = service;
		System.out.println("Discovered student servic async=" + this.studentService);
		// Get all students
		studentService.getStudentsAsync().whenComplete((studs, t) -> {
			if (t == null) {
				// Get first student from list
				Student s0 = studs.getStudents().get(0);
				// Print out first student
				System.out.println("getStudentsAsync complete.  Student0=" + s0);
				if (s0 != null) {
					s0.setGrade("Ninth");
					// And update
					studentService.updateStudentAsync(s0).whenComplete((s, e) -> {
						if (e == null) {
							System.out.println("updateStudentAsync complete.  Student0 updated=" + s);
						} else {
							System.out.println("update student error");
							e.printStackTrace();
						}
					});
				}
			} else {
				System.out.println("get students error");
				t.printStackTrace();
			}
		});
	}

	void unbindStudentServiceAsync(StudentServiceAsync service) throws Exception {
		this.studentService = null;
	}
}
