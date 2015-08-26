package com.mycorp.examples.student;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/studentservice")
public interface StudentService {

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/students")
	List<Student> getStudents();

	@GET
	@Produces(MediaType.APPLICATION_XML)
	@Path("/students/{studentId}")
	Student getStudent(@PathParam("studentId") String id);

	@POST
	@Produces(MediaType.APPLICATION_XML)
	@Path("/students/add/{studentName}")
	Student addStudent(@PathParam("studentName") String studentName);

	@POST
	@Consumes(MediaType.APPLICATION_XML)
	@Produces(MediaType.APPLICATION_XML)
	@Path("/students/update")
	Student updateStudent(Student student);

	@DELETE
	@Path("/students/delete/{studentId}")
	@Produces(MediaType.APPLICATION_XML)
	boolean deleteStudent(@PathParam("studentId") String studentId);
}
