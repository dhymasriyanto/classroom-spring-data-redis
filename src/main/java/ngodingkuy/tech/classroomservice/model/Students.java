package ngodingkuy.tech.classroomservice.model;

import lombok.*;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Students implements Serializable{
    @JsonProperty("student_id")
    private Long studentId;
    private Long grade;
}
