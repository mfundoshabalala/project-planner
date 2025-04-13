package ch.planner.plannersvc.service;

import ch.planner.plannersvc.model.Assignment;
import ch.planner.plannersvc.model.User;
import ch.planner.plannersvc.service.EmployeeService;
import ch.planner.plannersvc.service.ProjectService;
import ch.planner.plannersvc.repository.AssignmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final EmployeeService employeeService;
    private final ProjectService projectService;

    public List<Assignment> getAssignments(User user) {
        return assignmentRepository.findAllByCompanyId(user.getCompanyId());
    }

    public Assignment createAssignment(User user, String employeeId, String projectId, LocalDate date) {
        final var employee = employeeService.getEmployee(user, employeeId);
        final var project = projectService.getProject(user, projectId);

        return assignmentRepository.save(
            Assignment.builder()
                .employee(employee)
                .project(project)
                .date(date)
                .companyId(user.getCompanyId())
                .build()
        );
    }

    public void deleteAssignment(User user, String assignmentId) {
        final var assignment = assignmentRepository.findByIdAndCompanyId(assignmentId, user.getCompanyId())
            .orElseThrow();
        assignmentRepository.delete(assignment);
    }
}