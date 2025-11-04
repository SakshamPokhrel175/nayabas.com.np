// src/app/core/admin.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../auth/auth';

export const adminGuard: CanActivateFn = (route, state) => {
  const auth = inject(Auth);
  const router = inject(Router);

  const role = auth.getUserRole();

  if (role !== 'ADMIN') {
    router.navigate(['/auth/login']);
    return false;
  }
  return true;
};