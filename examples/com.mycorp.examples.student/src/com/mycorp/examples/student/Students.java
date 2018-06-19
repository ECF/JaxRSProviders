package com.mycorp.examples.student;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "students")
public class Students implements Serializable {

	public Students() {

	}

	@XmlElement
	public List<Student> getStudents() {
		return students;
	}

	public void setStudents(List<Student> students) {
		this.students = students;
	}

	private static final long serialVersionUID = 3317877620156748787L;

	private List<Student> students;

}
