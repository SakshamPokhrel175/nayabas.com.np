// // src/app/core/auth-interceptor.ts
// import { HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';

// export const AuthInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
//   const token = localStorage.getItem('token'); // Get JWT from localStorage

//   if (token) {
//     req = req.clone({
//       setHeaders: { Authorization: `Bearer ${token}` }
//     });
//   }

//   return next(req);
// };


// src/app/core/auth-interceptor.ts
import { HttpInterceptorFn, HttpRequest, HttpHandlerFn } from '@angular/common/http';

export const AuthInterceptor: HttpInterceptorFn = (req: HttpRequest<unknown>, next: HttpHandlerFn) => {
  // ✅ Always use the same key as where you store token in login
  const token = localStorage.getItem('token') || localStorage.getItem('jwtToken');

  if (token) {
    req = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(req);
};
