// src/app/features/review/review.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface Review {
  id: number;
  propertyId: number;
  propertyTitle?: string;
  userId: number;
  rating: number; // 1-5
  comment?: string;
  createdAt: string;
}

@Injectable({
  providedIn: 'root'
})
export class Review {
  private apiUrl = `${environment.apiUrl}/api/reviews`;

  constructor(private http: HttpClient) {}

  // Get all reviews by current user
  getMyReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/me`);
  }

  // Get reviews for a property
  getReviewsByProperty(propertyId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/property/${propertyId}`);
  }

  // Post a new review
  createReview(data: Partial<Review>): Observable<Review> {
    return this.http.post<Review>(`${this.apiUrl}`, data);
  }

  // Delete a review
  deleteReview(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }
}
