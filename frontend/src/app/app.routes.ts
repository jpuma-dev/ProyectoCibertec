import { Routes } from '@angular/router';
import { ReportesComponent } from './reportes.component';
import { SociosComponent } from './socios.component';
import { DeudasComponent } from './deudas.component';
import { PuestosComponent } from './puestos.component';
import { CobranzaComponent } from './cobranza.component';
import { ConceptosComponent } from './conceptos.component';

export const routes: Routes = [
  { path: 'socios', component: SociosComponent },
  { path: 'puestos', component: PuestosComponent },
  { path: 'conceptos', component: ConceptosComponent },
  { path: 'deudas', component: DeudasComponent },
  { path: 'cobranza', component: CobranzaComponent },
  { path: 'reportes', component: ReportesComponent },
  { path: '', redirectTo: '/reportes', pathMatch: 'full' }
];
