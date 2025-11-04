import { CanActivateFn, Router } from '@angular/router';
import { Auth } from '../auth/auth';
import { inject } from '@angular/core';

export const sellerGuard: CanActivateFn = (route, state) => {
  const auth = inject(Auth);
  const router = inject(Router);

  const role = auth.getUserRole();

  if (role !== 'SELLER') {
    router.navigate(['/auth/login']);
    return false;
  }
  return true;
};
