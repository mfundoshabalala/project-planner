package ch.planner.plannersvc.controller;

import ch.planner.plannersvc.api.ProjectsApi;
import ch.planner.plannersvc.auth.IsUser;
import ch.planner.plannersvc.auth.SessionState;
import ch.planner.plannersvc.auth.WithSessionState;
import ch.planner.plannersvc.controller.converter.ProjectConverter;
import ch.planner.plannersvc.dto.ProjectDto;
import ch.planner.plannersvc.dto.ProjectProperties;
import ch.planner.plannersvc.model.Project;
import ch.planner.plannersvc.service.ProjectService;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@WithSessionState
@AllArgsConstructor
public class ProjectController implements ProjectsApi {

  private final SessionState sessionState;
  private final ProjectService projectService;

  @Override
  @IsUser
  public ResponseEntity<ProjectDto> createProject(ProjectProperties projectProperties) {
    final Project project = ProjectConverter.fromProperties(projectProperties);

    final Project saved = projectService.createProject(sessionState.getUser(), project);

    return ResponseEntity.ok(ProjectConverter.toDto(saved));
  }

  @Override
  @IsUser
  public ResponseEntity<List<ProjectDto>> getProjects() {
    final List<Project> projects = projectService.getProjects(sessionState.getUser());

    final List<ProjectDto> dtos = ProjectConverter.toDtos(projects);

    return ResponseEntity.ok(dtos);
  }

  @Override
  @IsUser
  public ResponseEntity<ProjectDto> updateProject(String projectId, ProjectProperties projectProperties) {
      final Project project = ProjectConverter.fromProperties(projectProperties);
      final Project updated = projectService.updateProject(sessionState.getUser(), projectId, project);
      return ResponseEntity.ok(ProjectConverter.toDto(updated));
  }
}
