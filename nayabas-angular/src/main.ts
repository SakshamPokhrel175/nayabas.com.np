// 💡 FIX LINE: Polyfill for libraries (like Stomp/SockJS) that rely on the Node.js 'global' object.
(window as any)['global'] = window;

import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { provideAnimations } from '@angular/platform-browser/animations';
import { provideToastr } from 'ngx-toastr';

import { appConfig } from './app/app.config';
import { App } from './app/app';
import { routes } from './app/app.routes';
import { AuthInterceptor } from './app/core/auth-interceptor';

bootstrapApplication(App, {
  ...appConfig,
  providers: [
    ...appConfig.providers,

    // ✅ Attach JWT interceptor
    provideHttpClient(withInterceptors([AuthInterceptor])),

    // ✅ Routing
    provideRouter(routes),

    // ✅ Toastr setup (modern way, no NgModules needed)
    provideAnimations(),
    provideToastr({
      timeOut: 3000,
      positionClass: 'toast-top-right',
      preventDuplicates: true,
    }),
  ],
}).catch((err) => console.error(err));
