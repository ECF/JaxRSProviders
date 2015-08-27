package com.mycorp.examples.student.client;

import java.util.List;

import com.mycorp.examples.student.Student;
import com.mycorp.examples.student.StudentService;

public class StudentServiceClient {

	void bindStudentService(StudentService service) {
		System.out.println("Discovered student service=" + service);
		// Get students
		List<Student> originalStudents = service.getStudents();
		// Print list
		System.out.println("students=" + originalStudents);
		// Get first student
		Student s = originalStudents.get(0);
		System.out.println("Student 0=" + s);
		if (s != null) {
			// Get this student via id
			s = service.getStudent(s.getId());
			System.out.println("Student with id=" + s.getId() + "=" + s);
		}
		// Add a new student
		Student newStudent = service.addStudent("April Snow");
		System.out.println("Created student=" + newStudent);
		// Update with grade
		newStudent.setGrade("First");
		newStudent = service.updateStudent(newStudent);
		System.out.println("Updated student=" + newStudent);
		// Delete student
		System.out.println("Deleted student=" + service.deleteStudent(newStudent.getId()));
	}

}
