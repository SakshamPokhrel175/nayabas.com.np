import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';

// ===== Dashboard stats =====
export interface AdminStats {
  label: string;
  value: number;
  key: 'totalSellers' | 'totalBuyers' | 'pendingVerifications';
}

// ===== User from backend =====
export interface User {
  id: number;
  fullName: string;
  username: string;
  email?: string;
  requestedAt?: string;
  role: 'SELLER' | 'CUSTOMER' | 'ADMIN' | string;
  status: string; // APPROVED, PENDING
}

// ===== Pending seller =====
export interface PendingSeller extends User {}

@Injectable({ providedIn: 'root' })
export class Admin {
  private apiUrl = `${environment.apiUrl}/api/admin`;

  constructor(private http: HttpClient) {}

  // ===== Dashboard stats =====
  getStats(): Observable<AdminStats[]> {
    return this.http.get<{ [key: string]: number }>(`${this.apiUrl}/stats`).pipe(
      map(res => [
        { label: 'Total Sellers', value: res['totalSellers'] ?? 0, key: 'totalSellers' },
        { label: 'Total Buyers', value: res['totalBuyers'] ?? 0, key: 'totalBuyers' },
        {
          label: 'Pending Verifications',
          value: res['pendingVerifications'] ?? 0,
          key: 'pendingVerifications',
        },
      ])
    );
  }

  // ===== Pending sellers =====
  getPendingSellers(): Observable<PendingSeller[]> {
    return this.http.get<User[]>(`${this.apiUrl}/sellers/pending`).pipe(
      map(users =>
        users.map(u => ({
          ...u,
          role: u.role || 'SELLER',
          status: u.status || 'PENDING',
        }))
      )
    );
  }

  // ===== Approve / Reject seller =====
  approveSeller(id: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/sellers/${id}/approve`, {});
  }

  rejectSeller(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/sellers/${id}/reject`);
  }

  // ===== All sellers =====
  getAllSellers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/sellers`);
  }

  // ===== All buyers =====
  getAllBuyers(): Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/buyers`);
  }
}
