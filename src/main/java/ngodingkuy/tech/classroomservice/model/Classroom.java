package ngodingkuy.tech.classroomservice.model;

import java.util.List;
import java.util.Objects;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.vladmihalcea.hibernate.type.json.JsonBinaryType;

import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import lombok.*;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Classrooms")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Classroom {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotEmpty(message = "Name is required")
	private String name;

	@Column(name = "classroom_id")
	@JsonProperty("classroom_id")
	@NotNull(message = "Classroom ID is required")
	private Long classroomId;

	@Column(name = "teacher_id")
	@JsonProperty("teacher_id")
	@NotNull(message = "Teacher ID is required")
	private Long teacherId;

	@Type(type = "jsonb")
	@Column(columnDefinition = "jsonb")
	private List<Students> students;

	public Classroom(String name, Long classroomId, Long teacherId, List <Students> students) {
		this.name = name;
		this.classroomId = classroomId;
		this.teacherId = teacherId;
		this.students = students;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Classroom classroom = (Classroom) o;
		return Objects.equals(id, classroom.id) && Objects.equals(name, classroom.name) && Objects.equals(classroomId, classroom.classroomId) && Objects.equals(teacherId, classroom.teacherId) && Objects.equals(students, classroom.students );
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, classroomId, teacherId , students);
	}
}

