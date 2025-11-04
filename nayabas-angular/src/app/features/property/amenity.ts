import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Amenity {
  id?: number;
  name: string;
}
@Injectable({
  providedIn: 'root'
})
export class Amenity {
  
 private apiUrl = `${environment.apiUrl}/api/amenities`;

  constructor(private http: HttpClient) {}

  // ✅ Get all amenities (public)
  getAll(): Observable<Amenity[]> {
    return this.http.get<Amenity[]>(this.apiUrl);
  }

  // ✅ Add a new amenity (requires SELLER or ADMIN)
  addAmenity(name: string): Observable<Amenity> {
    return this.http.post<Amenity>(this.apiUrl, { name });
  }
}

