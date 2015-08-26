package com.mycorp.examples.student.webapp.host;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.mycorp.examples.student.Address;
import com.mycorp.examples.student.Student;
import com.mycorp.examples.student.StudentService;

@Path("/studentservice")
public class StudentResource implements StudentService {

	private static Map<String,Student> students = Collections.synchronizedMap(new HashMap<String, Student>());
	
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
		students.put(s.getId(),s);
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/students")
	public List<Student> getStudents() {
		return new ArrayList<Student>(students.values());
	}
	
	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/students/{studentId}")
	public Student getStudent(@PathParam("studentId") String id) {
		return students.get(id);
	}
	
	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("/students/add/{studentName}")
	public Student addStudent(@PathParam("studentName") String studentName) {
		if (studentName == null) return null;
		Student s = new Student(studentName);
		s.setId(UUID.randomUUID().toString());
		return s;
	}
	
	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@Path("/students/update")
	public Student updateStudent(Student student) {
		Student results = null;
		if (student != null) {
			String id = student.getId();
			if (id != null) {
				synchronized (students) {
					Student existing = students.get(student.getId());
					if (existing != null) {
						String newName = student.getName();
						if (newName != null)
							existing.setName(newName);
						existing.setGrade(student.getGrade());
						existing.setAddress(student.getAddress());
						results = existing;
					}
				}
			}
		}
		return results;
	}
	
	@DELETE
	@Path("/students/delete/{studentId}")
	@Produces(MediaType.APPLICATION_XML)
	public boolean deleteStudent(@PathParam("studentId") String studentId) {
		return students.get(studentId) != null;
	}
}
