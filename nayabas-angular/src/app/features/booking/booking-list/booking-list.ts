import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ToastrService } from 'ngx-toastr';

@Component({
  selector: 'app-booking-list',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './booking-list.html',
  styleUrls: ['./booking-list.scss']
})
export class BookingList {
  bookings = [
    { id: 101, property: 'Modern Flat', date: '2025-10-03', status: 'Confirmed', price: 25000 },
    { id: 102, property: 'Family House', date: '2025-10-08', status: 'Pending', price: 40000 }
  ];

  constructor(private toastr: ToastrService) {}

  cancelBooking(id: number) {
    // later replace with API call
    this.toastr.info(`Booking ${id} canceled`, 'Info');
  }
}
