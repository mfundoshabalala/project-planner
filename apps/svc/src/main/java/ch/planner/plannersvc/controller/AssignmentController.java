package ch.planner.plannersvc.controller;

import ch.planner.plannersvc.model.Assignment;
import ch.planner.plannersvc.service.AssignmentService;
import ch.planner.plannersvc.auth.WithSessionState;
import ch.planner.plannersvc.auth.SessionState;
import ch.planner.plannersvc.controller.converter.AssignmentConverter;
import ch.planner.plannersvc.dto.AssignmentDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import java.util.List;

@RestController
@WithSessionState
@RequiredArgsConstructor
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final SessionState sessionState;
    private final AssignmentService assignmentService;

    @GetMapping
    public ResponseEntity<List<AssignmentDto>> getAssignments() {
        return ResponseEntity.ok(
            assignmentService.getAssignments(sessionState.getUser()).stream()
                .map(AssignmentConverter::toDto)
                .toList()
        );
    }

    @PostMapping
    public ResponseEntity<AssignmentDto> createAssignment(
        @RequestParam String employeeId,
        @RequestParam String projectId,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ResponseEntity.ok(
            AssignmentConverter.toDto(
                assignmentService.createAssignment(
                    sessionState.getUser(),
                    employeeId,
                    projectId,
                    date
                )
            )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAssignment(@PathVariable String id) {
        assignmentService.deleteAssignment(sessionState.getUser(), id);
        return ResponseEntity.noContent().build();
    }
}