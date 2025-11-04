import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ProfileService, UserProfile } from '../profile.service';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.html',
  styleUrls: ['./profile.scss']
})
export class Profile implements OnInit {
  user: UserProfile | null = null;
  loading = true;
  saving = false;
  error: string | null = null;
  password: string = '';
  selectedFile: File | null = null;
  previewUrl: string | ArrayBuffer | null = null;

  constructor(private profileService: ProfileService) {}

  ngOnInit() {
    this.loadProfile();
  }

  loadProfile() {
    this.loading = true;

    this.profileService.getMyProfile().subscribe({
      next: (data) => {
        this.user = data;
        this.previewUrl = data.profileImageBase64
          ? 'data:image/png;base64,' + data.profileImageBase64
          : null;
        this.loading = false;

        // ✨ Smooth entry animation after loading
        setTimeout(() => {
          document.querySelector('.profile-card')?.classList.add('fade-in');
        }, 200);
      },
      error: () => {
        this.error = 'Failed to load profile.';
        this.loading = false;

        Swal.fire({
          icon: 'error',
          title: 'Failed to load profile!',
          position: 'top',
          toast: true,
          showConfirmButton: false,
          timer: 2500,
          background: 'linear-gradient(135deg, #ff416c, #ff4b2b)',
          color: '#fff',
          customClass: { popup: 'shadow-lg rounded-4 border-0' }
        });
      }
    });
  }

  onFileSelected(event: any) {
    this.selectedFile = event.target.files[0];
    if (this.selectedFile) {
      const reader = new FileReader();
      reader.onload = () => (this.previewUrl = reader.result);
      reader.readAsDataURL(this.selectedFile);
    }

    Swal.fire({
      icon: 'info',
      title: 'Profile picture selected!',
      position: 'top',
      toast: true,
      showConfirmButton: false,
      timer: 1800,
      background: 'linear-gradient(135deg, #6a11cb, #2575fc)',
      color: '#fff',
      customClass: { popup: 'shadow-lg rounded-4 border-0' }
    });
  }

  onSubmit() {
    if (!this.user) return;

    this.saving = true;

    const formData = new FormData();
    formData.append(
      'user',
      new Blob(
        [
          JSON.stringify({
            fullName: this.user.fullName,
            phoneNumber: this.user.phoneNumber,
            addressLine1: (this.user as any).addressLine1,
            password: this.password
          })
        ],
        { type: 'application/json' }
      )
    );

    if (this.selectedFile) formData.append('profileImage', this.selectedFile);

    this.profileService.updateProfile(formData).subscribe({
      next: () => {
        this.saving = false;
        this.password = '';
        this.loadProfile();

        Swal.fire({
          icon: 'success',
          title: 'Profile updated successfully!',
          position: 'top',
          toast: true,
          showConfirmButton: false,
          timer: 2000,
          background: 'linear-gradient(135deg, #9b59b6, #3498db)',
          color: '#fff',
          customClass: { popup: 'shadow-lg rounded-4 border-0' }
        });
      },
      error: () => {
        this.saving = false;

        Swal.fire({
          icon: 'error',
          title: 'Failed to update profile!',
          position: 'top',
          toast: true,
          showConfirmButton: false,
          timer: 2500,
          background: 'linear-gradient(135deg, #ff6b6b, #ff8e53)',
          color: '#fff',
          customClass: { popup: 'shadow-lg rounded-4 border-0' }
        });
      }
    });
  }
}

// import { Component, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ProfileService, UserProfile } from '../profile.service';
// import { ToastrService } from 'ngx-toastr';

// @Component({
//   selector: 'app-profile',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './profile.html',
//   styleUrls: ['./profile.scss']
// })
// export class Profile implements OnInit {
//   user: UserProfile | null = null;
//   loading = true;
//   saving = false;
//   error: string | null = null;
//   password: string = '';
//   selectedFile: File | null = null;
//   previewUrl: string | ArrayBuffer | null = null;

//   constructor(private profileService: ProfileService, private toastr: ToastrService) {}

//   ngOnInit() {
//     this.loadProfile();
//   }

//   loadProfile() {
//     this.loading = true;
//     this.profileService.getMyProfile().subscribe({
//       next: (data) => {
//         this.user = data;
//         this.previewUrl = data.profileImageBase64
//           ? 'data:image/png;base64,' + data.profileImageBase64
//           : null;
//         this.loading = false;
//       },
//       error: () => {
//         this.error = 'Failed to load profile.';
//         this.loading = false;
//       }
//     });
//   }

//   onFileSelected(event: any) {
//     this.selectedFile = event.target.files[0];
//     if (this.selectedFile) {
//       const reader = new FileReader();
//       reader.onload = () => (this.previewUrl = reader.result);
//       reader.readAsDataURL(this.selectedFile);
//     }
//   }

//   onSubmit() {
//     if (!this.user) return;
//     this.saving = true;

//     const formData = new FormData();
//     formData.append('user', new Blob([JSON.stringify({
//       fullName: this.user.fullName,
//       phoneNumber: this.user.phoneNumber,
//       addressLine1: (this.user as any).addressLine1, // include address if exists
//       password: this.password
//     })], { type: 'application/json' }));

//     if (this.selectedFile) formData.append('profileImage', this.selectedFile);

//     this.profileService.updateProfile(formData).subscribe({
//       next: () => {
//         this.toastr.success('Profile updated successfully!');
//         this.saving = false;
//         this.password = '';
//         this.loadProfile(); // refresh immediately
//       },
//       error: () => {
//         this.toastr.error('Failed to update profile.');
//         this.saving = false;
//       }
//     });
//   }
// }