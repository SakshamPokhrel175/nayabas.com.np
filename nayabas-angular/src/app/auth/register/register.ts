import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { Auth } from '../auth';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule],
  templateUrl: './register.html',
  styleUrls: ['./register.scss']
})
export class Register {
  form: FormGroup;
  role: 'CUSTOMER' | 'SELLER' = 'CUSTOMER';
  loading = false;

  idProof?: File;
  houseOwnershipProof?: File;

  idProofPreview?: string;
  houseOwnershipProofPreview?: string;

  uploadProgress = {
    idProof: 0,
    houseOwnershipProof: 0
  };

  constructor(
    private fb: FormBuilder,
    private authService: Auth,
    private router: Router
  ) {
    this.form = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(4)]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      email: ['', [Validators.required, Validators.email]],
      fullName: ['', Validators.required],
      addressLine1: ['', Validators.required],
      phoneNumber: ['', [Validators.pattern(/^[0-9]{10}$/)]]
    });
  }

  setRole(role: 'CUSTOMER' | 'SELLER') {
    this.role = role;
  }

onFileSelect(event: any, type: 'idProof' | 'houseOwnershipProof') {
  const file = event.target.files?.[0];
  if (!file) return;

  const reader = new FileReader();
  reader.onload = () => {
    if (type === 'idProof') {
      this.idProof = file;
      this.idProofPreview = reader.result as string;
    } else {
      this.houseOwnershipProof = file;
      this.houseOwnershipProofPreview = reader.result as string;
    }
  };
  reader.readAsDataURL(file);

  // upload immediately to backend
  this.authService.uploadFile(file).subscribe({
    next: (progressOrUrl) => {
      if (typeof progressOrUrl === 'number') {
        this.uploadProgress[type] = progressOrUrl;
      } else if (typeof progressOrUrl === 'string' && progressOrUrl) {
        this.uploadProgress[type] = 100;
        // Store URL temporarily for form submission
        this.form.patchValue({ [type + 'Url']: progressOrUrl });
      }
    },
    error: () => {
      Swal.fire({
        icon: 'error',
        title: 'Upload Failed',
        text: `Failed to upload ${type}. Please try again.`,
        confirmButtonColor: '#6C63FF',
      });
    }
  });
}

  // ✅ FIX — Add this missing method
  isImage(file: File | null | undefined): boolean {
    if (!file) return false;
    return file.type.startsWith('image/');
  }

  async submit() {
    if (this.form.invalid) {
      Swal.fire({
        icon: 'warning',
        title: 'Incomplete Details',
        text: 'Please fill all required fields correctly.',
        confirmButtonColor: '#6C63FF',
        background: '#f7f8fc',
      });
      return;
    }

    this.loading = true;

    Swal.fire({
      title: 'Uploading files...',
      html: 'Please wait while we register your account.',
      allowOutsideClick: false,
      didOpen: () => Swal.showLoading(),
      background: '#f7f8fc',
    });

    try {
      if (this.role === 'CUSTOMER') {
        await this.authService.registerCustomer(this.form.value).toPromise();
        Swal.fire({
          icon: 'success',
          title: 'Customer Registered!',
          text: 'You can now login with your account.',
          confirmButtonColor: '#6C63FF',
          background: 'linear-gradient(135deg, #00c6ff, #007bff)',
          color: '#fff'
        }).then(() => this.router.navigate(['/auth/login']));
      } else {
        const formData = new FormData();
        Object.entries(this.form.value).forEach(([key, value]) => {
          if (value) formData.append(key, value as string);
        });
        if (this.idProof) formData.append('idProof', this.idProof);
        if (this.houseOwnershipProof) formData.append('houseOwnershipProof', this.houseOwnershipProof);

        this.authService.registerSellerWithFiles(formData).subscribe({
          next: (progressOrResponse) => {
            if (typeof progressOrResponse === 'number') {
              this.uploadProgress.idProof = progressOrResponse;
              this.uploadProgress.houseOwnershipProof = progressOrResponse;
            } else if (progressOrResponse?.token) {
              Swal.fire({
                icon: 'info',
                title: 'Seller Registration Submitted!',
                text: 'Your account is under review. Admin will approve it shortly.',
                confirmButtonColor: '#6C63FF',
                background: 'linear-gradient(135deg, #6C63FF, #8E2DE2)',
                color: '#fff'
              }).then(() => this.router.navigate(['/auth/login']));
            }
          },
          error: (err) => {
            Swal.fire({
              icon: 'error',
              title: 'Registration Failed',
              text: err?.error || 'Something went wrong. Please try again.',
              confirmButtonColor: '#6C63FF',
              background: '#f7f8fc',
            });
            this.loading = false;
          },
          complete: () => (this.loading = false)
        });
      }
    } catch (err: any) {
      Swal.fire({
        icon: 'error',
        title: 'Registration Failed',
        text: err?.error || 'Something went wrong. Please try again.',
        confirmButtonColor: '#6C63FF',
        background: '#f7f8fc',
      });
    } finally {
      this.loading = false;
    }
  }
}