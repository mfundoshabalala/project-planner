import {Component, computed, signal, WritableSignal} from '@angular/core';

import {Store} from '@ngrx/store';
import {AppState} from '../shared/state/app.state';
import {loadEmployees, loadProjects} from '../shared/state/data/data.actions';
import {EmployeeDto, ProjectDto} from '../../generated';
import {selectEmployees, selectProjects} from '../shared/state/data/data.selectors';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import { AssignmentDto, AssignmentService } from '../shared/service/assignment.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-planner',
  templateUrl: './planner.component.html'
})
export class PlannerComponent {
  employees: EmployeeDto[] = [];
  projects: ProjectDto[] = [];
  assignments: AssignmentDto[] = [];
  currentDate = new Date();
  private subs = new Subscription();
  selectedProjects: { [employeeId: string]: { [date: string]: string | null } } = {};

  constructor(
    private store: Store<AppState>,
    private modalService: NgbModal,
    private assignmentService: AssignmentService
  ) {
    this.store.dispatch(loadEmployees());
    this.store.dispatch(loadProjects());

    this.store.select(selectEmployees).subscribe((employees) => {
      this.employees = employees;
    });
    this.store.select(selectProjects).subscribe((projects) => {
      this.projects = projects;
    });

    this.loadAssignments();
  }

  ngOnDestroy(): void {
    this.subs.unsubscribe();
  }

  private loadAssignments(): void {
    this.subs.add(
      this.assignmentService.getAssignments().subscribe({
        next: assignments => this.assignments = assignments,
        error: err => console.error('Failed to load assignments', err)
      })
    );
  }

  getSelectedProject(employeeId: string, date: Date): string | null {
    const dateStr = date.toISOString().split('T')[0];
    return this.selectedProjects[employeeId]?.[dateStr] || null;
  }

  onProjectSelected(employeeId: string, projectId: string | null, date: Date): void {
    const dateStr = this.getFormattedDate(date); // Use the helper method to format the date

    // Update the selectedProjects object
    if (!this.selectedProjects[employeeId]) {
      this.selectedProjects[employeeId] = {};
    }
    this.selectedProjects[employeeId][dateStr] = projectId;

    // Handle the backend update logic
    const existing = this.assignments.find(a =>
      a.employeeId === employeeId && a.date.split('T')[0] === dateStr
    );

    if (projectId) {
      const dto = { employeeId, projectId, date: date.toISOString() };

      const operation = existing?.id
        ? this.assignmentService.updateAssignment({ ...dto, id: existing.id })
        : this.assignmentService.createAssignment(dto);

      this.subs.add(
        operation.subscribe({
          next: assignment => {
            this.assignments = this.assignments.filter(a => a.id !== assignment.id);
            this.assignments.push(assignment);
          },
          error: err => console.error('Failed to save assignment', err),
        })
      );
    } else if (existing?.id) {
      this.subs.add(
        this.assignmentService.deleteAssignment(existing.id).subscribe({
          next: () => {
            this.assignments = this.assignments.filter(a => a.id !== existing.id);
          },
          error: err => console.error('Failed to delete assignment', err),
        })
      );
    }
  }

  previousWeek() {
    this.currentDate.setDate(this.currentDate.getDate() - 7);
    this.loadAssignments();
  }

  nextWeek() {
    this.currentDate.setDate(this.currentDate.getDate() + 7);
    this.loadAssignments();
  }

  // nthDayOfWeek = (n: number) => {
  //   const cd = this.currentDate;
  //   const d = new Date(Date.UTC(cd.getFullYear(), cd.getMonth(), cd.getDate()));
  //   const dayNum = d.getUTCDay() || 7;
  //   d.setUTCDate(d.getUTCDate() + 1 - dayNum);
  //   d.setUTCDate(d.getUTCDate() + n);
  //   return d;
  // };

  nthDayOfWeek(n: number) {
    const cd = new Date(this.currentDate);
    const day = cd.getDate() - cd.getDay() + n;
    return new Date(cd.setDate(day));
  }

  // getWeekNumber(): number {
  //   const cd = this.currentDate;
  //   const d = new Date(Date.UTC(cd.getFullYear(), cd.getMonth(), cd.getDate()));
  //   const dayNum = d.getUTCDay() || 7;
  //   d.setUTCDate(d.getUTCDate() + 4 - dayNum);
  //   const yearStart = new Date(Date.UTC(d.getUTCFullYear(), 0, 1));
  //   return Math.ceil(((d.getTime() - yearStart.getTime()) / 86400000 + 1) / 7);
  // }

  getWeekNumber(): number {
    const date = new Date(this.currentDate);
    date.setHours(0, 0, 0, 0);
    date.setDate(date.getDate() + 3 - (date.getDay() + 6) % 7);
    const week1 = new Date(date.getFullYear(), 0, 4);
    return 1 + Math.round(((date.getTime() - week1.getTime()) / 86400000
                          - 3 + (week1.getDay() + 6) % 7) / 7);
  }

  getFormattedDate(date: Date): string {
    return date.toISOString().split('T')[0]; // Formats the date as 'yyyy-MM-dd'
  }

  trackByProject(index: number, project: { id: string; name: string }): string {
    return project.id; // Use the unique 'id' of the project as the tracking key
  }
}
