import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface UserProfile {
  id: number;
  username: string;
  fullName: string;
  email: string;
  phoneNumber: string;
  addressLine1: string;
  role: string;
  
  profileImageBase64: string | null;
  idProofBase64: string | null;
  houseOwnershipProofBase64: string | null;
}

@Injectable({ providedIn: 'root' })
export class ProfileService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getMyProfile(): Observable<UserProfile> {
    return this.http.get<UserProfile>(`${this.apiUrl}/my-profile`);
  }

  updateProfile(formData: FormData): Observable<any> {
    return this.http.put(`${this.apiUrl}/update-profile`, formData);
  }
}
