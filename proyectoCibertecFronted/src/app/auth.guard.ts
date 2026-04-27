import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { ApiService } from './services/api.service';

/**
 * Guard que protege todas las rutas excepto /login.
 * Si el usuario no está autenticado, lo redirige al login.
 */
export const authGuard: CanActivateFn = () => {
  const api    = inject(ApiService);
  const router = inject(Router);

  if (api.estaAutenticado()) {
    return true;
  }
  router.navigate(['/login']);
  return false;
};
