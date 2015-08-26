package com.mycorp.examples.student.client;

import java.util.List;

import com.mycorp.examples.student.Student;
import com.mycorp.examples.student.StudentService;

public class StudentServiceClient {

	void bindStudentService(StudentService service) {
		System.out.println("Discovered student service=" + service);
		List<Student> students = service.getStudents();
		System.out.println("students=" + students);
		// Get first student
		Student s = students.get(0);
		if (s != null) {
			printStudent(s);
			// Change grade to 7
			s.setGrade("Seven");
			s = service.updateStudent(s);
			printStudent(s);
		}
	}

	void printStudent(Student s) {
		if (s == null)
			System.out.println("Student is null");
		else
			System.out.println("Student id=" + s.getId() + ";name=" + s.getName() + ";grade=" + s.getGrade());
	}
}
