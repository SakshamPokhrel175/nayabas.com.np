import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormsModule, NgForm } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import Swal from 'sweetalert2';
import { AmenitiesSelector } from '../amenities-selector/amenities-selector';
import { PropertyService } from '../property';
import { Auth } from '../../../auth/auth';

@Component({
  selector: 'app-add-property',
  standalone: true,
  imports: [CommonModule, FormsModule, AmenitiesSelector],
  templateUrl: './add-property.html',
  styleUrls: ['./add-property.scss']
})
export class AddProperty implements OnInit {
  property: any = {
    title: '',
    description: '',
    address: '',
    houseNumber: '',
    city: '',
    district: '',
    price: 0,
    bedrooms: 0,
    latitude: 0,
    longitude: 0,
    amenityIds: [],
    images: []
  };

  selectedFiles: File[] = [];
  previewUrls: string[] = [];
  existingImageUrls: { id: number; url: string }[] = [];

  saving = false;
  submitted = false;
  isEditMode = false;
  propertyId?: number;

  constructor(
    private propertyService: PropertyService,
    private toastr: ToastrService,
    private router: Router,
    private route: ActivatedRoute,
    public authService: Auth
  ) {}

  // 🔹 Load property data for edit mode
  ngOnInit(): void {
    this.route.paramMap.subscribe((params) => {
      this.propertyId = Number(params.get('id'));
      if (this.propertyId) {
        this.isEditMode = true;
        this.loadProperty();
      }
    });
  }

  private loadProperty(): void {
    this.propertyService.getById(this.propertyId!).subscribe({
      next: (data) => {
        this.property = {
          ...data,
          amenityIds: data.amenities?.map((a) => a.id) || []
        };
        this.existingImageUrls =
          data.images?.map((img: any) => ({
            id: img.id,
            url: `data:image/jpeg;base64,${img.imageData}`
          })) || [];
      },
      error: (err) => {
        this.showAlert(
          'error',
          'Failed to Load Property',
          err?.error?.message ||
            'Unable to fetch property details. Please try again later.'
        );
      }
    });
  }

  // 🔹 File input handler
  onFileSelected(event: any): void {
    const files = event.target.files;
    this.selectedFiles = Array.from(files);
    this.previewUrls = [];

    for (let file of this.selectedFiles) {
      const reader = new FileReader();
      reader.onload = (e: any) => this.previewUrls.push(e.target.result);
      reader.readAsDataURL(file);
    }
  }

  // 🔹 Delete an existing image + Refresh from backend
  // deleteExistingImage(imageId: number): void {
  //   Swal.fire({
  //     title: 'Delete Image?',
  //     text: 'Are you sure you want to remove this image?',
  //     icon: 'warning',
  //     showCancelButton: true,
  //     confirmButtonText: 'Yes, Delete',
  //     cancelButtonText: 'Cancel',
  //     confirmButtonColor: '#d33',
  //     cancelButtonColor: '#6c757d'
  //   }).then((result) => {
  //     if (result.isConfirmed) {
  //       this.propertyService.deleteImage(imageId).subscribe({
  //         next: () => {
  //           this.toastr.success('Image deleted successfully');
  //           // ✅ Reload property from backend to stay in sync
  //           if (this.propertyId) {
  //             this.propertyService.getById(this.propertyId).subscribe({
  //               next: (updated) => {
  //                 this.existingImageUrls =
  //                   updated.images?.map((img) => ({
  //                     id: img.id,
  //                     url: `data:image/jpeg;base64,${img.imageData}`
  //                   })) || [];
  //               }
  //             });
  //           }
  //         },
  //         error: () =>
  //           this.toastr.error('Failed to delete image. Please try again.')
  //       });
  //     }
  //   });
  // }

  //   // 🔹 Delete an existing image
  deleteExistingImage(imageId: number): void {
    Swal.fire({
      title: 'Delete Image?',
      text: 'Are you sure you want to remove this image?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Yes, Delete',
      cancelButtonText: 'Cancel',
      confirmButtonColor: '#d33',
      cancelButtonColor: '#6c757d'
    }).then((result) => {
      if (result.isConfirmed) {
        this.propertyService.deleteImage(imageId).subscribe({
          next: () => {
            this.existingImageUrls = this.existingImageUrls.filter(
              (img) => img.id !== imageId
            );
            this.toastr.success('Image deleted successfully');
          },
          error: () =>
            this.toastr.error('Failed to delete image. Please try again.')
        });
      }
    });
  }

  // 🔹 Submit handler
  async onSubmit(form: NgForm) {
    this.submitted = true;
    if (!form.valid) {
      this.showToast('warning', 'Please fill all required fields!');
      return;
    }

    if (!this.isEditMode && this.selectedFiles.length === 0) {
      this.showToast('error', 'Please upload at least one image!');
      return;
    }

    this.saving = true;

    try {
      const imageBase64 = await Promise.all(
        this.selectedFiles.map(
          (file) =>
            new Promise<string>((resolve) => {
              const reader = new FileReader();
              reader.onload = (e: any) => {
                const base64 = (e.target.result as string).split(',')[1];
                resolve(base64);
              };
              reader.readAsDataURL(file);
            })
        )
      );

      this.property.images = imageBase64;

      if (this.isEditMode) {
        this.updateProperty();
      } else {
        this.addNewProperty();
      }
    } catch {
      this.showAlert(
        'error',
        'Image Processing Failed!',
        'Please reselect images and try again.'
      );
      this.saving = false;
    }
  }

  // 🔹 Add new property
  private addNewProperty(): void {
    this.propertyService.create(this.property).subscribe({
      next: () => {
        this.showSuccessPopup(
          'Property Added Successfully!',
          'Your listing is now live.'
        );
        this.router.navigate(['/seller/dashboard']);
      },
      error: (err) => {
        this.showAlert(
          'error',
          'Failed to Add Property',
          err?.error?.message || 'An unexpected error occurred. Please try again.'
        );
      },
      complete: () => (this.saving = false)
    });
  }

  // 🔹 Update property
  private updateProperty(): void {
    Swal.fire({
      title: 'Confirm Update',
      text: 'Do you want to save these changes?',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Yes, Save Changes',
      cancelButtonText: 'Cancel',
      confirmButtonColor: '#4e54c8',
      cancelButtonColor: '#999'
    }).then((result) => {
      if (result.isConfirmed) {
        this.propertyService.update(this.propertyId!, this.property).subscribe({
          next: () => {
            this.showSuccessPopup(
              'Property Updated!',
              'Your listing details were successfully updated.'
            );
            this.router.navigate(['/seller/dashboard']);
          },
          error: (err) => {
            this.showAlert(
              'error',
              'Failed to Update Property',
              err?.error?.message || 'Please try again later.'
            );
          },
          complete: () => (this.saving = false)
        });
      } else {
        this.saving = false;
      }
    });
  }

  // 🔹 SweetAlert / Toastr Helpers
  private showToast(icon: any, title: string) {
    Swal.fire({
      toast: true,
      icon,
      title,
      position: 'top-end',
      showConfirmButton: false,
      timer: 2200,
      background: 'linear-gradient(135deg, #6a11cb, #2575fc)',
      color: '#fff'
    });
  }

  private showSuccessPopup(title: string, text: string) {
    Swal.fire({
      icon: 'success',
      title,
      text,
      confirmButtonText: 'Go to Dashboard',
      confirmButtonColor: '#6a11cb',
      background: 'linear-gradient(135deg, #43cea2, #185a9d)',
      color: '#fff'
    });
  }

  private showAlert(icon: any, title: string, text: string) {
    Swal.fire({
      icon,
      title,
      text,
      confirmButtonColor: '#4e54c8',
      background: 'linear-gradient(145deg, #f8faff, #e8eaf6)',
      color: '#333'
    });
  }
}



// import { CommonModule } from '@angular/common';
// import { Component, OnInit } from '@angular/core';
// import { FormsModule, NgForm } from '@angular/forms';
// import { ActivatedRoute, Router } from '@angular/router';
// import { ToastrService } from 'ngx-toastr';
// import Swal from 'sweetalert2';
// import { AmenitiesSelector } from '../amenities-selector/amenities-selector';
// import { PropertyService } from '../property';
// import { Auth } from '../../../auth/auth';

// @Component({
//   selector: 'app-add-property',
//   standalone: true,
//   imports: [CommonModule, FormsModule, AmenitiesSelector],
//   templateUrl: './add-property.html',
//   styleUrls: ['./add-property.scss']
// })
// export class AddProperty implements OnInit {
//   property: any = {
//     title: '',
//     description: '',
//     address: '',
//     houseNumber: '',
//     city: '',
//     district: '',
//     price: 0,
//     bedrooms: 0,
//     latitude: 0,
//     longitude: 0,
//     amenityIds: [],
//     images: []
//   };

//   selectedFiles: File[] = [];
//   previewUrls: string[] = [];
//   existingImageUrls: { id: number; url: string }[] = [];

//   saving = false;
//   submitted = false;
//   isEditMode = false;
//   propertyId?: number;

//   constructor(
//     private propertyService: PropertyService,
//     private toastr: ToastrService,
//     private router: Router,
//     private route: ActivatedRoute,
//     public authService: Auth
//   ) {}

//   ngOnInit(): void {
//     this.propertyId = Number(this.route.snapshot.paramMap.get('id'));
//     if (this.propertyId) {
//       this.isEditMode = true;
//       this.loadProperty();
//     }
//   }

//   // 🔹 Load property for edit mode
//   loadProperty(): void {
//     this.propertyService.getById(this.propertyId!).subscribe({
//       next: (data) => {
//         this.property = {
//           ...data,
//           amenityIds: data.amenities?.map((a) => a.id) || []
//         };

//         this.existingImageUrls =
//           data.images?.map((img: any) => ({
//             id: img.id,
//             url: `data:image/jpeg;base64,${img.imageData}`
//           })) || [];
//       },
//       error: (err) => {
//         this.showAlert(
//           'error',
//           'Failed to Load Property',
//           err?.error?.message ||
//             'Unable to fetch property details. Please try again later.'
//         );
//       }
//     });
//   }

//   // 🔹 File input changed
//   onFileSelected(event: any): void {
//     const files = event.target.files;
//     this.selectedFiles = Array.from(files);
//     this.previewUrls = [];

//     for (let file of this.selectedFiles) {
//       const reader = new FileReader();
//       reader.onload = (e: any) => this.previewUrls.push(e.target.result);
//       reader.readAsDataURL(file);
//     }
//   }

//   // 🔹 Delete an existing image
//   deleteExistingImage(imageId: number): void {
//     Swal.fire({
//       title: 'Delete Image?',
//       text: 'Are you sure you want to remove this image?',
//       icon: 'warning',
//       showCancelButton: true,
//       confirmButtonText: 'Yes, Delete',
//       cancelButtonText: 'Cancel',
//       confirmButtonColor: '#d33',
//       cancelButtonColor: '#6c757d'
//     }).then((result) => {
//       if (result.isConfirmed) {
//         this.propertyService.deleteImage(imageId).subscribe({
//           next: () => {
//             this.existingImageUrls = this.existingImageUrls.filter(
//               (img) => img.id !== imageId
//             );
//             this.toastr.success('Image deleted successfully');
//           },
//           error: () =>
//             this.toastr.error('Failed to delete image. Please try again.')
//         });
//       }
//     });
//   }

//   // 🔹 Form submit
//   async onSubmit(form: NgForm) {
//     this.submitted = true;
//     if (!form.valid) {
//       this.showToast('warning', 'Please fill all required fields!');
//       return;
//     }

//     if (!this.isEditMode && this.selectedFiles.length === 0) {
//       this.showToast('error', 'Please upload at least one image!');
//       return;
//     }

//     this.saving = true;

//     try {
//       const imageBase64 = await Promise.all(
//         this.selectedFiles.map(
//           (file) =>
//             new Promise<string>((resolve) => {
//               const reader = new FileReader();
//               reader.onload = (e: any) => {
//                 const base64 = (e.target.result as string).split(',')[1];
//                 resolve(base64);
//               };
//               reader.readAsDataURL(file);
//             })
//         )
//       );

//       this.property.images = imageBase64;

//       if (this.isEditMode) {
//         this.updateProperty();
//       } else {
//         this.addNewProperty();
//       }
//     } catch (err) {
//       this.showAlert(
//         'error',
//         'Image Processing Failed!',
//         'Please reselect images and try again.'
//       );
//       this.saving = false;
//     }
//   }

//   // 🔹 Add new property
//   addNewProperty(): void {
//     this.propertyService.create(this.property).subscribe({
//       next: () => {
//         this.showSuccessPopup('Property Added Successfully!', 'Your listing is now live.');
//         this.router.navigate(['/seller/dashboard']);
//       },
//       error: (err) => {
//         this.showAlert(
//           'error',
//           'Failed to Add Property',
//           err?.error?.message || 'An unexpected error occurred. Please try again.'
//         );
//       },
//       complete: () => (this.saving = false)
//     });
//   }

//   // 🔹 Update property
//   updateProperty(): void {
//     Swal.fire({
//       title: 'Confirm Update',
//       text: 'Do you want to save these changes?',
//       icon: 'question',
//       showCancelButton: true,
//       confirmButtonText: 'Yes, Save Changes',
//       cancelButtonText: 'Cancel',
//       confirmButtonColor: '#4e54c8',
//       cancelButtonColor: '#999'
//     }).then((result) => {
//       if (result.isConfirmed) {
//         this.propertyService.update(this.propertyId!, this.property).subscribe({
//           next: () => {
//             this.showSuccessPopup(
//               'Property Updated!',
//               'Your listing details were successfully updated.'
//             );
//             this.router.navigate(['/seller/dashboard']);
//           },
//           error: (err) => {
//             this.showAlert(
//               'error',
//               'Failed to Update Property',
//               err?.error?.message || 'Please try again later.'
//             );
//           },
//           complete: () => (this.saving = false)
//         });
//       } else {
//         this.saving = false;
//       }
//     });
//   }

//   // 🔹 SweetAlert Helpers
//   private showToast(icon: any, title: string) {
//     Swal.fire({
//       toast: true,
//       icon,
//       title,
//       position: 'top-end',
//       showConfirmButton: false,
//       timer: 2200,
//       background: 'linear-gradient(135deg, #6a11cb, #2575fc)',
//       color: '#fff'
//     });
//   }

//   private showSuccessPopup(title: string, text: string) {
//     Swal.fire({
//       icon: 'success',
//       title,
//       text,
//       confirmButtonText: 'Go to Dashboard',
//       confirmButtonColor: '#6a11cb',
//       background: 'linear-gradient(135deg, #43cea2, #185a9d)',
//       color: '#fff'
//     });
//   }

//   private showAlert(icon: any, title: string, text: string) {
//     Swal.fire({
//       icon,
//       title,
//       text,
//       confirmButtonColor: '#4e54c8',
//       background: 'linear-gradient(145deg, #f8faff, #e8eaf6)',
//       color: '#333'
//     });
//   }
//   onDeleteImage(imageId: number) {
//   if (confirm('Are you sure you want to remove this image?')) {
//     this.propertyService.deleteImage(imageId).subscribe({
//       next: () => {
//         this.existingImageUrls = this.existingImageUrls.filter(img => img.id !== imageId);
//         this.toastr.success('Image deleted successfully');
//       },
//       error: (err) => {
//         console.error(err);
//         this.toastr.error('Failed to delete image. Please try again.');
//       }
//     });
//   }
// }

// }

// import { CommonModule } from '@angular/common';
// import { Component } from '@angular/core';
// import { FormsModule, NgForm } from '@angular/forms';
// import { Router } from '@angular/router';
// import { ToastrService } from 'ngx-toastr';
// import { AmenitiesSelector } from '../amenities-selector/amenities-selector';
// import { PropertyService } from '../property';
// import { Auth } from '../../../auth/auth';
// import Swal from 'sweetalert2'
// @Component({
//   selector: 'app-add-property',
//   standalone: true,
//   imports: [CommonModule, FormsModule, AmenitiesSelector],
//   templateUrl: './add-property.html',
//   styleUrls: ['./add-property.scss']
// })
// export class AddProperty {
//   property: any = {
//     title: '',
//     description: '',
//     address: '',
//     houseNumber: '',
//     city: '',
//     district: '',
//     price: 0,
//     bedrooms: 0,
//     latitude: 0,
//     longitude: 0,
//     amenityIds: [],
//     images: []
//   };

//   selectedFiles: File[] = [];
//   previewUrls: string[] = [];
//   saving = false;
//   submitted = false;

//   constructor(
//     private propertyService: PropertyService,
//     private toastr: ToastrService,
//     private router: Router,
//     public authService: Auth
//   ) {}

//   // 📸 Handle image preview
//   onFileSelected(event: any) {
//     const files = event.target.files;
//     this.selectedFiles = Array.from(files);
//     this.previewUrls = [];

//     for (let file of this.selectedFiles) {
//       const reader = new FileReader();
//       reader.onload = (e: any) => this.previewUrls.push(e.target.result);
//       reader.readAsDataURL(file);
//     }
//   }


//   // 🏠 Submit property
// async onSubmit(form: any) {
//   this.submitted = true;

//   if (!form.valid) {
//     Swal.fire({
//       icon: 'warning',
//       title: 'Please fill all required fields!',
//       position: 'top',
//       toast: true,
//       showConfirmButton: false,
//       timer: 2500,
//       background: 'linear-gradient(135deg, #9b59b6, #3498db)',
//       color: '#fff',
//       customClass: {
//         popup: 'shadow-lg rounded-4 border-0'
//       }
//     });
//     return;
//   }

//   if (this.selectedFiles.length === 0) {
//     Swal.fire({
//       icon: 'error',
//       title: 'Please upload at least one property image!',
//       position: 'top',
//       toast: true,
//       showConfirmButton: false,
//       timer: 2500,
//       background: 'linear-gradient(135deg, #ff6b6b, #ff8e53)',
//       color: '#fff',
//       customClass: {
//         popup: 'shadow-lg rounded-4 border-0'
//       }
//     });
//     return;
//   }

//   this.saving = true;

//   try {


//     this.property.images = await Promise.all(
//   this.selectedFiles.map(
//     (file) =>
//       new Promise<string>((resolve) => {
//         const reader = new FileReader();
//         reader.onload = (e: any) => {
//           const base64 = (e.target.result as string).split(',')[1]; // ✅ remove "data:image/jpeg;base64,"
//           resolve(base64);
//         };
//         reader.readAsDataURL(file);
//       })
//   )
// );


//     this.propertyService.create(this.property).subscribe({
//       next: () => {
//         Swal.fire({
//           icon: 'success',
//           title: 'Property added successfully!',
//           position: 'top',
//           toast: true,
//           showConfirmButton: false,
//           timer: 2000,
//           background: 'linear-gradient(135deg, #6a11cb, #2575fc)',
//           color: '#fff',
//           customClass: {
//             popup: 'shadow-lg rounded-4 border-0'
//           }
//         });

//         this.saving = false;
//         this.submitted = false;
//         form.resetForm();
//         this.previewUrls = [];
//         this.selectedFiles = [];
//         this.router.navigate(['/seller/dashboard']);
//       },
//       error: (err) => {
//         console.error(err);
//         Swal.fire({
//           icon: 'error',
//           title: 'Failed to add property. Please try again!',
//           position: 'top',
//           toast: true,
//           showConfirmButton: false,
//           timer: 2500,
//           background: 'linear-gradient(135deg, #ff416c, #ff4b2b)',
//           color: '#fff',
//           customClass: {
//             popup: 'shadow-lg rounded-4 border-0'
//           }
//         });
//         this.saving = false;
//       }
//     });
//   } catch (err) {
//     console.error(err);
//     Swal.fire({
//       icon: 'error',
//       title: 'Image processing failed!',
//       position: 'top',
//       toast: true,
//       showConfirmButton: false,
//       timer: 2500,
//       background: 'linear-gradient(135deg, #ff416c, #ff4b2b)',
//       color: '#fff',
//       customClass: {
//         popup: 'shadow-lg rounded-4 border-0'
//       }
//     });
//     this.saving = false;
//   }
// }

// }