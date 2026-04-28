import { Routes } from '@angular/router';
import { LayoutComponent } from './components/layout/layout.component';

export const routes: Routes = [
  {
    path: '',
    component: LayoutComponent,
    children: [
      { path: '', redirectTo: 'socios', pathMatch: 'full' },
      { 
        path: 'socios', 
        loadComponent: () => import('./components/socios/socios.component').then(m => m.SociosComponent) 
      },
      { 
        path: 'conceptos', 
        loadComponent: () => import('./components/conceptos/conceptos.component').then(m => m.ConceptosComponent) 
      },
      { 
        path: 'deudas', 
        loadComponent: () => import('./components/deudas/deudas.component').then(m => m.DeudasComponent) 
      },
      { 
        path: 'pagos', 
        loadComponent: () => import('./components/pagos/pagos.component').then(m => m.PagosComponent) 
      },
      { 
        path: 'puestos', 
        loadComponent: () => import('./components/puestos/puestos.component').then(m => m.PuestosComponent) 
      }
    ]
  },
  { path: '**', redirectTo: '' }
];
