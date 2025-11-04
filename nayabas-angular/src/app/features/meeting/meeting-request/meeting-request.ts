import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MeetingService } from '../meeting';
import { ToastrService } from 'ngx-toastr';
import { Router } from '@angular/router';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-meeting-request',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './meeting-request.html',
  styleUrls: ['./meeting-request.scss']
})
export class MeetingRequest {

 @Input() propertyId!: number;

  meetingDate = '';
  meetingTime = '';
  customerMessage = '';
  sending = false;

  constructor(
    private meetingService: MeetingService,
    private toastr: ToastrService,
    private router: Router
  ) {}
  

  submitRequest() {
    if (!this.propertyId) {
      this.toastr.error('Property not set', 'Error');
      return;
    }
    if (!this.meetingDate || !this.meetingTime) {
      this.toastr.warning('Please choose date and time', 'Validation');
      return;
    }
    this.sending = true;

    // convert time like "10:30" to "10:30:00" for backend LocalTime
    const time = this.meetingTime.length === 5 ? this.meetingTime + ':00' : this.meetingTime;

    const payload = {
      propertyId: this.propertyId,
      meetingDate: this.meetingDate,
      meetingTime: time,
      customerMessage: this.customerMessage
    };

this.meetingService.createMeeting(payload).subscribe({
  next: () => {
    this.sending = false;
    Swal.fire({
      title: 'Meeting Requested!',
      html: `
        <p><strong>Date:</strong> ${this.meetingDate}</p>
        <p><strong>Time:</strong> ${this.meetingTime}</p>
        <p><strong>Message:</strong> ${this.customerMessage || '—'}</p>
      `,
      icon: 'success',
      confirmButtonText: 'OK',
      background: 'linear-gradient(135deg, #4b6cb7, #182848)',
      color: '#fff',
      confirmButtonColor: '#00ffcc'
    });
  },
  error: (err) => {
    console.error(err);
    this.toastr.error('Failed to request meeting', 'Error');
    this.sending = false;
  }
});
  }
}
