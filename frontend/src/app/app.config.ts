import { ApplicationConfig, provideZoneChangeDetection } from '@angular/core';
import { provideRouter, withInMemoryScrolling } from '@angular/router';
import { provideHttpClient, withFetch, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { providePrimeNG } from 'primeng/config';
import Aura from '@primeng/themes/aura';
import { definePreset } from '@primeng/themes';

import { routes } from './app.routes';
import { authInterceptor } from '@/core/interceptors/auth.interceptor';

// Preset vert institutionnel ENSI
const EnsiPreset = definePreset(Aura, {
  semantic: {
    primary: {
      50: '#e9f5ef',
      100: '#c9e7d7',
      200: '#9fd3b9',
      300: '#6dba96',
      400: '#3f9e74',
      500: '#1f7d56',
      600: '#0b5d3b',
      700: '#0a4d31',
      800: '#083d27',
      900: '#06301f',
      950: '#031a11'
    }
  }
});

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(
      routes,
      withInMemoryScrolling({ scrollPositionRestoration: 'enabled', anchorScrolling: 'enabled' })
    ),
    provideHttpClient(withFetch(), withInterceptors([authInterceptor])),
    provideAnimationsAsync(),
    providePrimeNG({
      ripple: true,
      theme: {
        preset: EnsiPreset,
        options: { darkModeSelector: '.app-dark' }
      }
    })
  ]
};
