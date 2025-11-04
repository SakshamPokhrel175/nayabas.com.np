import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule, ActivatedRoute } from '@angular/router';
import { HttpClientModule } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Auth, JwtResponse, LoginRequest } from '../auth';
import Swal from 'sweetalert2';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterModule, ReactiveFormsModule, HttpClientModule],
  templateUrl: './login.html',
  styleUrls: ['./login.scss'],
})
export class Login {
  form: FormGroup;
  loading = false;

  constructor(
    private fb: FormBuilder,
    private auth: Auth,
    private toastr: ToastrService,
    private router: Router,
    private route: ActivatedRoute // ✅ Added to read returnUrl
  ) {
    this.form = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required],
    });
  }

  submit() {
    if (this.form.invalid) {
      Swal.fire({
        icon: 'warning',
        title: 'Incomplete Details',
        text: 'Please fill in all required fields.',
        confirmButtonColor: '#6C63FF',
        background: '#f7f8fc',
      });
      return;
    }

    this.loading = true;
    const loginData: LoginRequest = this.form.value;

    this.auth.login(loginData).subscribe({
      next: (res: JwtResponse) => {
        this.auth.loginSuccess(res);

        // ✅ Get user role and optional account status
        const role = this.auth.getUserRole();
        const status = res?.status || 'PENDING';
        // ✅ Capture return URL (from property-list, meeting, etc.)
        const returnUrl = this.route.snapshot.queryParams['returnUrl'] || '/';

        // ===================== SELLER ROLE =====================
        if (role === 'SELLER') {
          if (status === 'REJECTED') {
            Swal.fire({
              icon: 'error',
              title: 'Account Rejected',
              text: 'Your seller account has been rejected by admin. Please contact support for details.',
              confirmButtonText: 'Okay',
              confirmButtonColor: '#6C63FF',
              background: '#f9f9fc',
            });
            this.auth.logout();
            this.loading = false;
            return;
          }

          if (status === 'PENDING') {
            Swal.fire({
              icon: 'info',
              title: 'Account Under Review',
              text: 'Your seller account is awaiting admin approval. Please try again later.',
              confirmButtonText: 'Got it',
              confirmButtonColor: '#6C63FF',
              background: '#f9f9fc',
            });
            this.auth.logout();
            this.loading = false;
            return;
          }

          Swal.fire({
            icon: 'success',
            title: 'Welcome Back!',
            text: 'Login successful. Redirecting to your dashboard...',
            timer: 1500,
            showConfirmButton: false,
            background: 'linear-gradient(135deg, #6C63FF, #8E2DE2)',
            color: '#fff',
          }).then(() => {
            this.router.navigate(['/seller/dashboard']);
          });

          this.loading = false;
          return;
        }

        // ===================== ADMIN ROLE =====================
        if (role === 'ADMIN') {
          Swal.fire({
            icon: 'success',
            title: 'Welcome Admin!',
            text: 'Redirecting to admin panel...',
            timer: 1500,
            showConfirmButton: false,
            background: 'linear-gradient(135deg, #007bff, #00c6ff)',
            color: '#fff',
          }).then(() => this.router.navigate(['/admin/dashboard']));
          this.loading = false;
          return;
        }

        // ===================== CUSTOMER ROLE =====================
        Swal.fire({
          icon: 'success',
          title: 'Login Successful!',
          text: 'Welcome back to NayaBas!',
          timer: 1500,
          showConfirmButton: false,
          background: 'linear-gradient(135deg, #00c6ff, #007bff)',
          color: '#fff',
        }).then(() => {
          // ✅ Redirect user back to property or home
          this.router.navigateByUrl(returnUrl);
        });

        this.loading = false;
      },
      error: (err) => {
        Swal.fire({
          icon: 'error',
          title: 'Login Failed',
          text: err?.error?.message || 'Invalid credentials. Please try again.',
          confirmButtonColor: '#6C63FF',
          background: '#f7f8fc',
        });
        this.loading = false;
      },
    });
  }
}


// // src/app/auth/login.ts
// import { CommonModule } from '@angular/common';
// import { Component } from '@angular/core';
// import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
// import { Router, RouterModule } from '@angular/router';
// import { HttpClientModule } from '@angular/common/http';
// import { ToastrService } from 'ngx-toastr';
// import { Auth, JwtResponse, LoginRequest } from '../auth';

// @Component({
//   selector: 'app-login',
//   standalone: true,
//   imports: [CommonModule, RouterModule, ReactiveFormsModule, HttpClientModule],
//   templateUrl: './login.html',
//   styleUrls: ['./login.scss'],
// })
// export class Login {
//   form: FormGroup;
//   loading = false;

//   constructor(
//     private fb: FormBuilder,
//     private auth: Auth,
//     private toastr: ToastrService,
//     private router: Router
//   ) {
//     this.form = this.fb.group({
//       username: ['', Validators.required],
//       password: ['', Validators.required],
//     });
//   }

//   submit() {
//     if (this.form.invalid) {
//       this.toastr.warning('Please fill in all required fields', 'Validation');
//       return;
//     }

//     this.loading = true;
//     const loginData: LoginRequest = this.form.value;

//     this.auth.login(loginData).subscribe({
//       next: (res: JwtResponse) => {
//         // store token and update BehaviorSubjects
//         this.auth.loginSuccess(res);

//         this.toastr.success('Login successful!', 'Success');

//         // navigate based on role (immediate read, computeUserRole reads token)
//         // in login.ts submit()
//         const role = this.auth.getUserRole();

//         if (role === 'ADMIN') {
//           this.router.navigate(['/admin/dashboard']);
//         } else if (role === 'SELLER') {
//           this.router.navigate(['/seller/dashboard']);
//         } else {
//           this.router.navigate(['/']); // CUSTOMER home
//         }

//         this.loading = false;
//       },
//       error: (err) => {
//         this.toastr.error(err?.error?.message || 'Login failed', 'Error');
//         this.loading = false;
//       },
//     });
//   }
// }
