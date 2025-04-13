package ch.planner.plannersvc.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;

@Data
@Builder
public class AssignmentDto {
    private String id;
    private String employeeId;
		private String employeeName;
    private String projectId;
		private String projectName;

		@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate date;
}