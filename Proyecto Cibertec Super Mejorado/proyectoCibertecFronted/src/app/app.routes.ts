import { Routes } from '@angular/router';
import { LoginComponent }    from './login.component';
import { ReportesComponent } from './reportes.component';
import { SociosComponent }   from './socios.component';
import { DeudasComponent }   from './deudas.component';
import { PuestosComponent }  from './puestos.component';
import { CobranzaComponent } from './cobranza.component';
import { authGuard }         from './auth.guard';

export const routes: Routes = [
  // Ruta pública: login
  { path: 'login', component: LoginComponent },

  // Rutas protegidas: requieren autenticación
  { path: 'socios',   component: SociosComponent,   canActivate: [authGuard] },
  { path: 'puestos',  component: PuestosComponent,  canActivate: [authGuard] },
  { path: 'deudas',   component: DeudasComponent,   canActivate: [authGuard] },
  { path: 'cobranza', component: CobranzaComponent, canActivate: [authGuard] },
  { path: 'reportes', component: ReportesComponent, canActivate: [authGuard] },

  // Ruta por defecto → login
  { path: '', redirectTo: '/login', pathMatch: 'full' },

  // Cualquier otra ruta → login
  { path: '**', redirectTo: '/login' }
];
