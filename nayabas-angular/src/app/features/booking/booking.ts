// src/app/features/booking/booking.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Booking {
  id: number;
  propertyId: number;
  propertyTitle?: string;
  ownerId?: number;
  ownerName?: string;
  userId: number;
  bookingDate: string;
  status: 'PENDING' | 'CONFIRMED' | 'CANCELLED';
  amount?: number;
}

@Injectable({
  providedIn: 'root'
})
export class Booking {
  private apiUrl = `${environment.apiUrl}/api/bookings`;

  constructor(private http: HttpClient) {}

  // Get bookings for current user
  getMyBookings(): Observable<Booking[]> {
    return this.http.get<Booking[]>(`${this.apiUrl}/me`);
  }

  // Get booking by ID
  getBookingById(id: number): Observable<Booking> {
    return this.http.get<Booking>(`${this.apiUrl}/${id}`);
  }

  // Create a new booking
  createBooking(data: Partial<Booking>): Observable<Booking> {
    return this.http.post<Booking>(`${this.apiUrl}`, data);
  }

  // Cancel a booking
  cancelBooking(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
