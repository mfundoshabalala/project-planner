package ch.planner.plannersvc.repository;

import ch.planner.plannersvc.model.Assignment;
import java.util.Optional;
import java.util.List;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AssignmentRepository extends CrudRepository<Assignment, String> {
    List<Assignment> findAllByCompanyId(String companyId);
    Optional<Assignment> findByEmployeeIdAndProjectIdAndDate(String employeeId, String projectId, java.time.LocalDate date);
    Optional<Assignment> findByIdAndCompanyId(String assignmentId, String companyId);
}