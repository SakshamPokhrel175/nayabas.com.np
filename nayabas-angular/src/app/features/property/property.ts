import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

// Image returned by backend
export interface PropertyImage {
  id: number;
  imageData: string; // e.g. "data:image/jpeg;base64,...."
}

// Property interface matching backend PropertyResponse
export interface Property {
  id: number;
  title: string;
  price: number;
  address: string;
  description?: string;
  bedrooms?: number;
  houseNumber?: string;
  city?: string;
  district?: string;
  latitude?: number;
  longitude?: number;
  images?: PropertyImage[];
  amenities?: { id: number; name: string }[];
  owner?: {
    id: number;
    username: string;
    email: string;
    fullName: string;
    role: string;
  };
  createdAt?: string;
}

// Payload for create/update
export interface PropertyCreate {
  title: string;
  description?: string;
  address: string;
  houseNumber: string;
  city: string;
  district: string;
  price: number;
  bedrooms: number;
  latitude: number;
  longitude: number;
  amenityIds?: number[];
  images?: string[]; // Base64 strings
}

@Injectable({ providedIn: 'root' })
export class PropertyService {
  private apiUrl = `${environment.apiUrl}/api/properties`;

  constructor(private http: HttpClient) {}

  getAll(): Observable<Property[]> {
    return this.http.get<Property[]>(this.apiUrl);
  }

  getById(id: number): Observable<Property> {
    return this.http.get<Property>(`${this.apiUrl}/${id}`);
  }

  create(property: PropertyCreate): Observable<Property> {
    return this.http.post<Property>(this.apiUrl, property);
  }

  update(id: number, property: PropertyCreate): Observable<Property> {
    return this.http.put<Property>(`${this.apiUrl}/${id}`, property);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getMyProperties(): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.apiUrl}/my`);
  }

  getAllWithOwners(): Observable<Property[]> {
    return this.http.get<Property[]>(`${this.apiUrl}/all-with-owners`);
  }

  getMonthlyEarnings(): Observable<{ [month: string]: number }> {
    return this.http.get<{ [month: string]: number }>(`${this.apiUrl}/earnings/monthly`);
  }
  // ✅ Fix: Single image delete endpoint
deleteImage(imageId: number): Observable<any> {
return this.http.delete(`${this.apiUrl}/images/${imageId}`);
}


}