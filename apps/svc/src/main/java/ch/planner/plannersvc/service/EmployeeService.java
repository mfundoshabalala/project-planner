package ch.planner.plannersvc.service;

import ch.planner.plannersvc.model.Employee;
import ch.planner.plannersvc.model.Language;
import ch.planner.plannersvc.model.User;
import ch.planner.plannersvc.repository.EmployeeRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@AllArgsConstructor
@Validated
@Slf4j
public class EmployeeService {

  private final EmployeeRepository employeeRepository;

  public List<Employee> getEmployees(User user) {
    return employeeRepository.findAllByCompanyId(user.getCompanyId());
  }

  public Employee getEmployee(User user, String employeeId) {
    return employeeRepository.findByIdAndCompanyId(employeeId, user.getCompanyId()).orElseThrow();
  }

  public Employee createEmployee(User user, Employee employee) {
    employee.setCompanyId(user.getCompanyId());
    employee.setLang(Language.DE);
    return employeeRepository.save(employee);
  }

  public Employee updateEmployee(User user, String employeeId, Employee employee) {
    final Employee existing = employeeRepository.findByIdAndCompanyId(employeeId, user.getCompanyId()).orElseThrow();

    existing.setName(employee.getName());
    existing.setLang(employee.getLang() != null ? employee.getLang() : existing.getLang());

    return employeeRepository.save(existing);
  }
}
