package ch.planner.plannersvc.controller.converter;

import ch.planner.plannersvc.dto.AssignmentDto;
import ch.planner.plannersvc.model.Assignment;

public class AssignmentConverter {
    public static AssignmentDto toDto(Assignment assignment) {
        return AssignmentDto.builder()
            .id(assignment.getId())
            .employeeId(assignment.getEmployee().getId())
            .employeeName(assignment.getEmployee().getName())
            .projectId(assignment.getProject().getId())
            .projectName(assignment.getProject().getName())
            .date(assignment.getDate())
            .build();
    }
}