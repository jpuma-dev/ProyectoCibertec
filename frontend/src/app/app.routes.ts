import { Routes } from '@angular/router';
import { DashboardComponent } from './dashboard.component';
import { SociosComponent } from './socios.component';
import { DeudasComponent } from './deudas.component';
import { PuestosComponent } from './puestos.component';
import { CobranzaComponent } from './cobranza.component';
import { ConceptosComponent } from './conceptos.component';

export const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent },
  { path: 'socios', component: SociosComponent },
  { path: 'puestos', component: PuestosComponent },
  { path: 'conceptos', component: ConceptosComponent },
  { path: 'deudas', component: DeudasComponent },
  { path: 'cobranza', component: CobranzaComponent },
  { path: '', redirectTo: '/dashboard', pathMatch: 'full' }
];
