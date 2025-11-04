import { Routes } from '@angular/router';
import { authGuard } from './core/auth-guard';
import { adminGuard } from './core/admin-guard';
import { sellerGuard } from './core/seller-guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/property/property-list/property-list').then((m) => m.PropertyList),
  },
  {
    path: 'property/:id',
    loadComponent: () =>
      import('./features/property/property-detail/property-detail').then((m) => m.PropertyDetail),
  },
  {
    path: 'auth/login',
    loadComponent: () => import('./auth/login/login').then((m) => m.Login),
  },
  {
    path: 'auth/register',
    loadComponent: () => import('./auth/register/register').then((m) => m.Register),
  },
  {
    path: 'booking',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/booking/booking-list/booking-list').then((m) => m.BookingList),
  },
  {
    path: 'customer/meeting',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/meeting/meeting-customer-list/meeting-customer-list').then((m) => m.MeetingCustomerList),
  },
  {
    path: 'review',
    canActivate: [authGuard],
    loadComponent: () =>
      import('./features/review/review-list/review-list').then((m) => m.ReviewList),
  },
  {
  path: 'admin/dashboard',
  canActivate: [authGuard, adminGuard],
  loadComponent: () => import('./features/admin/admin-dashboard/admin-dashboard')
    .then(m => m.AdminDashboard),
},
{
    path: 'admin/edit-property/:id',
    canActivate: [authGuard, adminGuard],
    loadComponent: () =>
      import('./features/property/add-property/add-property').then((m) => m.AddProperty),
},
{
  path: 'seller/dashboard',
  canActivate: [authGuard, sellerGuard],
  loadComponent: () => import('./features/seller/seller-dashboard/seller-dashboard')
    .then(m => m.SellerDashboard),
},
{
  path: 'customer/profile',
  canActivate: [authGuard],
  loadComponent: () => import('./features/profile/profile/profile').then(m => m.Profile)
},
{
  path: 'seller/profile',
  canActivate: [authGuard],
  loadComponent: () => import('./features/profile/profile/profile').then(m => m.Profile)
},
{
  path: 'seller/add-property',
  canActivate: [authGuard, sellerGuard],
  loadComponent: () =>
    import('./features/property/add-property/add-property').then((m) => m.AddProperty),
},
{
  path: 'seller/edit-property/:id',
  canActivate: [authGuard, sellerGuard],
  loadComponent: () =>
    import('./features/property/add-property/add-property').then((m) => m.AddProperty),
},

{
  path: 'seller/meetings',
  canActivate: [authGuard, sellerGuard],
  loadComponent: () => import('./features/meeting/meeting-seller-list/meeting-seller-list').then(m => m.MeetingSellerList)
},
    {
        path: 'booking/create/:id',
        canActivate: [authGuard],
        loadComponent: () =>
            // Assuming you have a BookingCreation component, but if not, 
            // we can redirect to a generic booking form.
            // For now, let's point it to a hypothetical component or use the main booking list if no form exists.
            import('./features/booking/booking-list/booking-list').then((m) => m.BookingList),
        // NOTE: You should ideally create a dedicated BookingFormComponent for creation.
    },
        {
      path: 'chat/:roomId', // The :roomId URL segment will be used to identify the WebSocket topic
      canActivate: [authGuard], // Only authenticated users (customer/seller) can chat
      loadComponent: () => 
        import('./features/chat/chat-room/chat-room').then(m => m.ChatRoom)
    },



];
