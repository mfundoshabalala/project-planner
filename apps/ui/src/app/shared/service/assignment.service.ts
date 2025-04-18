import { Injectable, Inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { APP_CONFIG, AppConfig } from '../../../app/app.config';

export interface AssignmentDto {
  id: string;
  employeeId: string;
  projectId: string;
  date: string;
  employeeName?: string;
  projectName?: string;
}

@Injectable({ providedIn: 'root' })
export class AssignmentService {
  private apiUrl: string;

  constructor(
    private http: HttpClient,
    @Inject(APP_CONFIG) private config: AppConfig
  ) {
    this.apiUrl = `${this.config.api.baseUrl}/api/assignments`;
  }

  getAssignments(): Observable<AssignmentDto[]> {
    return this.http.get<AssignmentDto[]>(this.apiUrl, {
      withCredentials: true,
    });
  }

  createAssignment(dto: Omit<AssignmentDto, 'id'>): Observable<AssignmentDto> {
    return this.http.post<AssignmentDto>(this.apiUrl, dto);
  }

  deleteAssignment(id: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  updateAssignment(dto: AssignmentDto): Observable<AssignmentDto> {
    return this.http.put<AssignmentDto>(`${this.apiUrl}/${dto.id}`, dto);
  }
}
