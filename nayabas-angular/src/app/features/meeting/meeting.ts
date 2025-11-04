import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, Observable, of, throwError } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface CustomerInfo {
  id: number;
  username: string;
  fullName?: string;
  email?: string;
  phoneNumber?: string;
  address?: string;
  profileImage?: string; // optional Base64 display
}

export interface PropertyInfo {
  id: number;
  title?: string;
  location?: string;
  price?: number;
  owner?: { id: number; username: string; email?: string };
}

// Add these changes to your existing Meeting interface in meeting.ts

export interface Meeting {
  id: number;
  property?: PropertyInfo;
  meetingDate: string;
  meetingTime: string;
  customerMessage?: string;
  sellerNote?: string; // All six statuses are correctly defined in a single union type:
  meetingStatus:
    | 'PENDING'
    | 'SCHEDULED'
    | 'REJECTED'
    | 'PROPOSED_CHANGE'
    | 'CHAT_COMPLETED'
    | 'CLOSED';
  customer?: CustomerInfo;
  chatRoomId?: string; // This holds the unique ID needed for the chat link
}

// Ensure MeetingRequest is correctly defined here too, for property-detail.ts to import it
export interface MeetingRequest {
  propertyId: number;
  meetingDate: string;
  meetingTime: string;
  customerMessage?: string;
}

// 💡 NEW: DTO for Seller proposing a change
export interface MeetingProposeChange {
  newDate: string;
  newTime: string;
  sellerNote?: string;
}

@Injectable({ providedIn: 'root' })
export class MeetingService {
  private apiUrl = `${environment.apiUrl}/api/meetings`;

  constructor(private http: HttpClient) {}

  getCustomerMeetings(): Observable<Meeting[]> {
    return this.http.get<Meeting[]>(`${this.apiUrl}/customer`);
  }

  getSellerMeetings(): Observable<Meeting[]> {
    return this.http.get<Meeting[]>(`${this.apiUrl}/seller`).pipe(
      catchError((error: HttpErrorResponse) => {
        // If the status is 200 (OK) but the parsing failed,
        // assume the response body was empty or not valid JSON (e.g., empty string).
        if (error.status === 200 && error.ok === false) {
          console.warn(
            'Backend returned 200 OK but with invalid/empty JSON body. Treating as empty array.'
          );
          return of([]); // Return an empty array Observable
        }
        // For genuine server errors (4xx, 5xx), rethrow the error
        return throwError(() => error);
      })
    );
  }

  createMeeting(req: MeetingRequest): Observable<Meeting> {
    return this.http.post<Meeting>(this.apiUrl, req);
  }

  updateStatus(meetingId: number, status: 'SCHEDULED' | 'REJECTED'): Observable<Meeting> {
    return this.http.put<Meeting>(`${this.apiUrl}/${meetingId}/status?status=${status}`, {});
  }

  // 💡 NEW: Seller proposes new time
  proposeChange(meetingId: number, req: MeetingProposeChange): Observable<Meeting> {
    return this.http.put<Meeting>(`${this.apiUrl}/${meetingId}/propose-change`, req);
  }

  // 💡 NEW: Customer confirms proposed time
  confirmChange(meetingId: number): Observable<Meeting> {
    return this.http.put<Meeting>(`${this.apiUrl}/${meetingId}/confirm-change`, {});
  }
}
