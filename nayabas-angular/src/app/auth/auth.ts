import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType } from '@angular/common/http';
import { Observable, BehaviorSubject } from 'rxjs';
import { map } from 'rxjs/operators';
import { jwtDecode } from 'jwt-decode';
import { environment } from '../../environments/environment';
import { Router } from '@angular/router';

export type UserRole = 'ADMIN' | 'CUSTOMER' | 'SELLER' | string;

export interface CustomerRegisterRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
  addressLine1: string;
}
export interface SellerRegisterRequest {
  username: string;
  password: string;
  email: string;
  fullName: string;
  phoneNumber: string;
  addressLine1: string;
  idProofUrl?: string;
  houseOwnershipProofUrl?: string;
}
export interface LoginRequest {
  username: string;
  password: string;
}
export interface JwtResponse {
  token: string;
  username: string;
  role?: string;
  status: string;
}

@Injectable({ providedIn: 'root' })
export class Auth {
  private baseUrl = `${environment.apiUrl}/api/auth`;
  public isLoggedIn$ = new BehaviorSubject<boolean>(!!localStorage.getItem('token'));
  public userRole$ = new BehaviorSubject<UserRole | null>(this.computeUserRole());

  constructor(private http: HttpClient, private router: Router) {}
  

  // ===== CUSTOMER REGISTER =====
  registerCustomer(data: CustomerRegisterRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.baseUrl}/register/customer`, data);
  }

  // ===== SELLER REGISTER (JSON only) =====
  registerSeller(data: SellerRegisterRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.baseUrl}/register/seller`, data);
  }

  // ===== SELLER REGISTER (with files + real progress) =====
  registerSellerWithFiles(formData: FormData): Observable<number | any> {
    return this.http.post(`${this.baseUrl}/register/seller/upload`, formData, {
      reportProgress: true,
      observe: 'events'
    }).pipe(
      map((event: HttpEvent<any>) => {
        switch (event.type) {
          case HttpEventType.UploadProgress:
            return Math.round((100 * event.loaded) / (event.total || 1));
          case HttpEventType.Response:
            return event.body; // final response from backend
          default:
            return 0;
        }
      })
    );
  }

  // ===== LOGIN =====
  login(data: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.baseUrl}/login`, data);
  }

  // ===== Handle login success =====
  loginSuccess(res: JwtResponse) {
    if (res?.token) localStorage.setItem('token', res.token);
    if (res?.username) localStorage.setItem('username', res.username);
    if (res?.role) localStorage.setItem('role', res.role);

    this.isLoggedIn$.next(true);
    this.userRole$.next(this.computeUserRole());
  }

  logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('username');
    localStorage.removeItem('role');
    this.isLoggedIn$.next(false);
    this.userRole$.next(null);
    this.router.navigate(['/']);
  }

  getToken(): string | null {
    return localStorage.getItem('token');
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  getUserRole(): UserRole | null {
    return this.computeUserRole();
  }

  private computeUserRole(): UserRole | null {
    const storedRole = localStorage.getItem('role');
    if (storedRole) return this.normalizeRole(storedRole);

    const token = this.getToken();
    if (!token) return null;

    try {
      const decoded: any = jwtDecode(token);
      let roleCandidate =
        decoded?.role ?? decoded?.roles ?? decoded?.authorities ?? decoded?.authority;
      if (Array.isArray(roleCandidate)) roleCandidate = roleCandidate[0];
      if (typeof roleCandidate === 'string') return this.normalizeRole(roleCandidate);
    } catch {
      return null;
    }

    return null;
  }

  private normalizeRole(raw: string): string {
    return raw ? raw.replace(/^ROLE_/, '').toUpperCase() : raw;
  }

  uploadFile(file: File): Observable<number | string> {
  const formData = new FormData();
  formData.append('file', file);

  return this.http.post(`${this.baseUrl}/upload/file`, formData, {
    reportProgress: true,
    observe: 'events'
  }).pipe(
    map((event: HttpEvent<any>) => {
      switch (event.type) {
        case HttpEventType.UploadProgress:
          return Math.round((100 * event.loaded) / (event.total || 1));
        case HttpEventType.Response:
          return event.body?.fileUrl || ''; // backend should return file URL
        default:
          return 0;
      }
    })
  );
}
}