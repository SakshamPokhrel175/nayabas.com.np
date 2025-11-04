import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { ToastrService } from 'ngx-toastr';
import { Property, PropertyService } from '../property';
import { Auth } from '../../../auth/auth';
import Swal from 'sweetalert2';
import { FormsModule, NgForm } from '@angular/forms'; // Ensure NgForm is imported if needed for template
import { MeetingService, MeetingRequest } from '../../meeting/meeting'; // Assuming MeetingRequest is now part of meeting.ts

@Component({
  selector: 'app-property-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  templateUrl: './property-detail.html',
  styleUrls: ['./property-detail.scss']
})
export class PropertyDetail implements OnInit {
  propertyId!: number;
  property?: Property;
  loading = true;
  error = '';
  
  // Model properties for the form
  meetingDate = '';
  meetingTime = '';
  customerMessage = '';

  constructor(
    private route: ActivatedRoute,
    private propertyService: PropertyService,
    private meetingService: MeetingService,
    private toastr: ToastrService,
    public authService: Auth
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.error = 'Invalid property ID';
      this.loading = false;
      return;
    }
    this.propertyId = Number(idParam);

    this.propertyService.getById(this.propertyId).subscribe({
      next: (data) => {
        this.property = data;
        this.loading = false;
      },
      error: () => {
        this.error = 'Property not found';
        this.toastr.error(this.error);
        this.loading = false;
      }
    });
  }

  // 1. FIXED SYNTAX AND SERVICE CALL
  sendMeetingRequest() {
    if (!this.meetingDate || !this.meetingTime) {
      this.toastr.warning('Please select both date and time.');
      return;
    }
      
    const request: MeetingRequest = {
      propertyId: this.propertyId,
      meetingDate: this.meetingDate,
      meetingTime: this.meetingTime,
      customerMessage: this.customerMessage
    };

    // Call the service and handle response
    this.meetingService.createMeeting(request).subscribe({
      next: () => {
        // Show success SweetAlert
        Swal.fire({
          title: 'Meeting Requested!',
          html: `
            <p><strong>Date:</strong> ${this.meetingDate}</p>
            <p><strong>Time:</strong> ${this.meetingTime}</p>
            <p><strong>Message:</strong> ${this.customerMessage || '—'}</p>
            <p class="mt-3 text-warning">Waiting for Seller confirmation.</p>
          `,
          icon: 'success',
          confirmButtonText: 'View My Meetings',
          background: 'linear-gradient(135deg, #4b6cb7, #182848)',
          color: '#fff',
          confirmButtonColor: '#00ffcc'
        });
        
        // Clear the form fields after successful submission
        this.meetingDate = '';
        this.meetingTime = '';
        this.customerMessage = '';
      },
      error: (err) => {
        // Use proper error type hint (explicitly 'any' or map error response)
        const msg = err.error?.message || 'Failed to send request. Are you logged in as a Customer?';
        this.toastr.error(msg, 'Request Failed');
      }
    });
  }
  
  // 2. REINSTATED FUNCTION (used in template, error TS2551)
  bookProperty() {
    Swal.fire({
      title: 'Confirm Booking?',
      text: `Do you want to book "${this.property?.title}"?`,
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Yes, book it!',
      cancelButtonText: 'Cancel',
      background: 'linear-gradient(135deg, #4b6cb7, #182848)',
      color: '#fff',
      confirmButtonColor: '#00ffcc',
      cancelButtonColor: '#ff4b2b'
    }).then((result) => {
      if (result.isConfirmed) {
        this.toastr.success('Property booked successfully!');
      }
    });
  }

  // 3. REINSTATED FUNCTION (used in template, error TS2304)
  getImageSrc(imageData?: string | null): string {
    if (!imageData) return 'assets/default-property.jpg';
    return imageData.startsWith('data:image')
      ? imageData
      : 'data:image/jpeg;base64,' + imageData;
  }
}

// import { Component, OnInit } from '@angular/core';
// import { ActivatedRoute, RouterLink } from '@angular/router';
// import { CommonModule } from '@angular/common';
// import { ToastrService } from 'ngx-toastr';
// import { Property, PropertyService } from '../property';
// import { Auth } from '../../../auth/auth';
// import Swal from 'sweetalert2';
// import { FormsModule } from '@angular/forms';

// @Component({
//   selector: 'app-property-detail',
//   standalone: true,
//   imports: [CommonModule, RouterLink, FormsModule],
//   templateUrl: './property-detail.html',
//   styleUrls: ['./property-detail.scss']
// })
// export class PropertyDetail implements OnInit {
//   propertyId!: number;
//   property?: Property;
//   loading = true;
//   error = '';
//   meetingDate = '';
//   meetingTime = '';
//   customerMessage = '';

//   constructor(
//     private route: ActivatedRoute,
//     private propertyService: PropertyService,
//     private toastr: ToastrService,
//     public authService: Auth
//   ) {}

//   ngOnInit(): void {
//     const idParam = this.route.snapshot.paramMap.get('id');
//     if (!idParam) {
//       this.error = 'Invalid property ID';
//       this.loading = false;
//       return;
//     }
//     this.propertyId = Number(idParam);

//     this.propertyService.getById(this.propertyId).subscribe({
//       next: (data) => {
//         this.property = data;
//         this.loading = false;
//       },
//       error: () => {
//         this.error = 'Property not found';
//         this.toastr.error(this.error);
//         this.loading = false;
//       }
//     });
//   }

//   sendMeetingRequest() {
//     if (!this.meetingDate || !this.meetingTime) {
//       this.toastr.warning('Please select both date and time.');
//       return;
//     }

//     Swal.fire({
//       title: 'Meeting Requested!',
//       html: `
//         <p><strong>Date:</strong> ${this.meetingDate}</p>
//         <p><strong>Time:</strong> ${this.meetingTime}</p>
//         <p><strong>Message:</strong> ${this.customerMessage || '—'}</p>
//       `,
//       icon: 'success',
//       confirmButtonText: 'OK',
//       background: 'linear-gradient(135deg, #4b6cb7, #182848)',
//       color: '#fff',
//       confirmButtonColor: '#00ffcc'
//     });
//   }

//   bookProperty() {
//     Swal.fire({
//       title: 'Confirm Booking?',
//       text: `Do you want to book "${this.property?.title}"?`,
//       icon: 'question',
//       showCancelButton: true,
//       confirmButtonText: 'Yes, book it!',
//       cancelButtonText: 'Cancel',
//       background: 'linear-gradient(135deg, #4b6cb7, #182848)',
//       color: '#fff',
//       confirmButtonColor: '#00ffcc',
//       cancelButtonColor: '#ff4b2b'
//     }).then((result) => {
//       if (result.isConfirmed) {
//         this.toastr.success('Property booked successfully!');
//       }
//     });
//   }

//   getImageSrc(imageData?: string | null): string {
//     if (!imageData) return 'assets/default-property.jpg';
//     return imageData.startsWith('data:image')
//       ? imageData
//       : 'data:image/jpeg;base64,' + imageData;
//   }
// }