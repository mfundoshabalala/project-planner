package ch.planner.plannersvc.service;

import ch.planner.plannersvc.model.Project;
import ch.planner.plannersvc.model.User;
import ch.planner.plannersvc.repository.ProjectRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@AllArgsConstructor
@Validated
@Slf4j
public class ProjectService {

  private final ProjectRepository projectRepository;

  public List<Project> getProjects(User user) {
    return projectRepository.findAllByCompanyId(user.getCompanyId());
  }

  public Project getProject(User user, String projectId) {
    return projectRepository.findByIdAndCompanyId(projectId, user.getCompanyId()).orElseThrow();
  }

  public Project createProject(User user, Project project) {
    project.setCompanyId(user.getCompanyId());
    return projectRepository.save(project);
  }

  public Project updateProject(User user, String projectId, Project project) {
    final Project existing = projectRepository.findByIdAndCompanyId(projectId, user.getCompanyId()).orElseThrow();

    existing.setName(project.getName());
    existing.setColor(project.getColor());

    return projectRepository.save(existing);
  }
}
