import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { Property, PropertyService } from '../property';
import { Booking } from '../../booking/booking';
import { Auth } from '../../../auth/auth';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-property-list',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './property-list.html',
  styleUrls: ['./property-list.scss']
})
export class PropertyList implements OnInit {
  properties: Property[] = [];
  loading = true;
  error = '';

  constructor(
    private propertyService: PropertyService,
    private bookingService: Booking,
    private authService: Auth,
    private router: Router,
    private toastr: ToastrService
  ) {}

  ngOnInit() {
    this.propertyService.getAll().subscribe({
      next: (data) => {
        this.properties = data || [];
        this.loading = false;
      },
      error: (err) => {
        console.error('Error fetching properties:', err);
        this.error = 'Could not load properties';
        this.loading = false;
        Swal.fire({
          title: 'Error',
          text: this.error,
          icon: 'error',
          confirmButtonColor: '#7b2ff7'
        });
      }
    });
  }

  // 🔹 Booking Button Logic
  bookProperty(property: Property) {
    if (!this.authService.isLoggedIn()) {
      this.promptLogin(`/property/${property.id}`);
      return;
    }

    this.bookingService.createBooking({
      propertyId: property.id,
      bookingDate: new Date().toISOString(),
      status: 'PENDING'
    }).subscribe({
      next: () =>
        Swal.fire({
          title: 'Success!',
          text: 'Booking request sent to seller!',
          icon: 'success',
          confirmButtonColor: '#6a11cb'
        }),
      error: () =>
        Swal.fire({
          title: 'Error',
          text: 'Failed to book property.',
          icon: 'error',
          confirmButtonColor: '#f12711'
        })
    });
  }

  // 🔹 Meeting Request Logic
  scheduleMeeting(property: Property) {
    if (!this.authService.isLoggedIn()) {
      this.promptLogin(`/property/${property.id}`);
      return;
    }

    Swal.fire({
      title: 'Meeting Requested!',
      text: `Your meeting request for "${property.title}" has been sent.`,
      icon: 'success',
      confirmButtonColor: '#6a11cb'
    });
  }

  // 🔹 Redirect to Login if not logged in
  private promptLogin(returnUrl: string) {
    Swal.fire({
      title: 'Login Required',
      text: 'Please log in to continue.',
      icon: 'info',
      showCancelButton: true,
      confirmButtonText: 'Go to Login',
      cancelButtonText: 'Cancel',
      confirmButtonColor: '#6a11cb',
      cancelButtonColor: '#f12711'
    }).then((result) => {
      if (result.isConfirmed) {
        this.router.navigate(['/auth/login'], {
          queryParams: { returnUrl }
        });
      }
    });
  }

  getImageSrc(imageData?: string | null): string {
    if (!imageData) return 'https://via.placeholder.com/600x400?text=No+Image';
    return imageData.startsWith('data:image')
      ? imageData
      : 'data:image/jpeg;base64,' + imageData;
  }
}



// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { Router, RouterLink } from '@angular/router';
// import { ToastrService } from 'ngx-toastr';
// import { Property, PropertyService } from '../property';
// import { Booking } from '../../booking/booking';
// import { Auth } from '../../../auth/auth';
// import Swal from 'sweetalert2';

// @Component({
//   selector: 'app-property-list',
//   standalone: true,
//   imports: [CommonModule, RouterLink],
//   templateUrl: './property-list.html',
//   styleUrls: ['./property-list.scss']
// })
// export class PropertyList implements OnInit {
//   properties: Property[] = [];
//   loading = true;
//   error = '';

//   constructor(
//     private propertyService: PropertyService,
//     private bookingService: Booking,
//     private authService: Auth,
//     private router: Router,
//     private toastr: ToastrService
//   ) {}

//   ngOnInit() {
//     this.propertyService.getAll().subscribe({
//       next: (data) => {
//         this.properties = data || [];
//         this.loading = false;
//       },
//       error: (err) => {
//         console.error('Error fetching properties:', err);
//         this.error = 'Could not load properties';
//         this.loading = false;
//         Swal.fire('Error', this.error, 'error');
//       }
//     });
//   }

//   bookProperty(property: Property) {
//     if (!this.authService.isLoggedIn()) {
//       Swal.fire({
//         title: 'Login Required',
//         text: 'Please log in to book a property.',
//         icon: 'warning',
//         confirmButtonText: 'Go to Login'
//       }).then((result) => {
//         if (result.isConfirmed) this.router.navigate(['/auth/login']);
//       });
//       return;
//     }

//     this.bookingService.createBooking({
//       propertyId: property.id,
//       bookingDate: new Date().toISOString(),
//       status: 'PENDING'
//     }).subscribe({
//       next: () => Swal.fire('Success', 'Booking request sent to seller!', 'success'),
//       error: () => Swal.fire('Error', 'Failed to book property.', 'error')
//     });
//   }

//   scheduleMeeting(property: Property) {
//     if (!this.authService.isLoggedIn()) {
//       Swal.fire({
//         title: 'Login Required',
//         text: 'Please log in to request a meeting.',
//         icon: 'info',
//         confirmButtonText: 'Go to Login'
//       }).then((result) => {
//         if (result.isConfirmed) this.router.navigate(['/auth/login']);
//       });
//       return;
//     }

//     Swal.fire('Request Sent', `Meeting request sent for ${property.title}`, 'success');
//   }

//   getImageSrc(imageData?: string | null): string {
//     if (!imageData) return 'https://via.placeholder.com/600x400?text=No+Image';
//     return imageData.startsWith('data:image')
//       ? imageData
//       : 'data:image/jpeg;base64,' + imageData;
//   }
// }



// <div class="container py-5">
//   <h2 class="mb-5 text-center fw-bold display-6 text-gradient">
//     Featured Properties
//   </h2>

//   <!-- Loading Spinner -->
//   <div *ngIf="loading" class="text-center my-5">
//     <div class="spinner-border text-primary" style="width: 3rem; height: 3rem;"></div>
//   </div>

//   <!-- Error Message -->
//   <div *ngIf="error" class="alert alert-danger text-center mt-4 shadow-sm">
//     {{ error }}
//   </div>

//   <!-- Property Grid -->
//   <div class="row row-cols-1 row-cols-md-3 g-4" *ngIf="!loading && !error && properties.length">
//     <div class="col" *ngFor="let property of properties">
//       <div class="card h-100 border-0 shadow-lg property-card rounded-4">
//         <!-- Carousel -->
//         <div id="carousel{{ property.id }}" class="carousel slide carousel-fade" data-bs-ride="carousel">
//           <div class="carousel-inner rounded-top-4 overflow-hidden">
//             <div class="carousel-item" *ngFor="let img of property.images || []; let i = index"
//               [class.active]="i === 0">
//               <img *ngIf="img?.imageData" [src]="getImageSrc(img.imageData)" alt="{{ property.title }}"
//                 class="d-block w-100 property-img" />
//             </div>

//             <div *ngIf="!property.images?.length" class="no-image d-flex justify-content-center align-items-center">
//               <i class="bi bi-image text-secondary fs-1 me-2"></i>
//               <span>No image</span>
//             </div>
//           </div>

//           <button *ngIf="(property.images?.length || 0) > 1" class="carousel-control-prev" type="button"
//             [attr.data-bs-target]="'#carousel' + property.id" data-bs-slide="prev">
//             <span class="carousel-control-prev-icon"></span>
//           </button>
//           <button *ngIf="(property.images?.length || 0) > 1" class="carousel-control-next" type="button"
//             [attr.data-bs-target]="'#carousel' + property.id" data-bs-slide="next">
//             <span class="carousel-control-next-icon"></span>
//           </button>
//         </div>

//         <!-- Property Info -->
//         <div class="card-body p-4">
//           <h5 class="fw-bold text-dark mb-2">{{ property.title }}</h5>
//           <p class="text-muted small mb-1">
//             <i class="bi bi-geo-alt text-primary me-1"></i>{{ property.address }}
//           </p>
//           <h5 class="text-success fw-bold mb-3">
//             Rs. {{ property.price | number }}
//           </h5>

//           <div *ngIf="property.amenities?.length">
//             <strong class="text-secondary small">Amenities:</strong>
//             <div class="mt-2">
//               <span *ngFor="let a of property.amenities" class="badge bg-gradient me-1 mb-1">{{ a.name }}</span>
//             </div>
//           </div>
//         </div>

//         <div class="card-footer bg-transparent border-0 p-3 text-center ">
//           <a [routerLink]="['/property', property.id]" class="btn btn-gradient w-100 rounded-pill fw-semibold">
//             <i class="bi bi-eye me-1"></i> View Details
//           </a>
//         </div>
//       </div>
//     </div>
//   </div>

//   <!-- No Properties Found -->
//   <div *ngIf="!loading && !error && !properties.length" class="alert alert-info text-center mt-5 shadow-sm">
//     No properties found.
//   </div>
// </div>