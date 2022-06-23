package ngodingkuy.tech.classroomservice.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import ngodingkuy.tech.classroomservice.model.Classroom;
import ngodingkuy.tech.classroomservice.model.Students;

@AllArgsConstructor
public class ClassroomFaker {

	static Students students = new Students();
	static List<Students> studentLists = new ArrayList<>();
	static Classroom classroom = new Classroom();
	public static Classroom create(){
		students.setGrade(80L);
		students.setStudentId(1L);
		studentLists.add(students);
		classroom.setId(1L);
		classroom.setName("Test");
		classroom.setTeacherId(1L);
		classroom.setClassroomId(1L);
		classroom.setStudents(studentLists);
		return classroom;
	}
}

