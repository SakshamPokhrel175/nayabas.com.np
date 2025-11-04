import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-review-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './review-list.html',
  styleUrls: ['./review-list.scss']
})
export class ReviewList {

  reviews = [
    {
      id: 1,
      property: 'Modern Flat',
      user: 'Bishal Shrestha',
      rating: 5,
      comment: 'Excellent place and very clean!',
      date: '2025-09-15'
    },
    {
      id: 2,
      property: 'Family House',
      user: 'Anita Gurung',
      rating: 4,
      comment: 'Spacious and comfortable for family stay.',
      date: '2025-09-20'
    },
    {
      id: 3,
      property: 'Luxury Apartment',
      user: 'Ram Karki',
      rating: 3,
      comment: 'Nice location but a bit noisy.',
      date: '2025-09-27'
    }
  ];

}
