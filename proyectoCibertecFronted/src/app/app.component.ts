import { Component } from '@angular/core';
import { RouterOutlet, RouterLink } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  template: `
    <div class="container">
      <aside class="sidebar">
        <h1>Mercado App</h1>
        <nav>
          <a routerLink="/socios" class="nav-link">Socios</a>
          <a routerLink="/puestos" class="nav-link">Puestos</a>
          <a routerLink="/deudas" class="nav-link">Generar Deudas</a>
          <a routerLink="/cobranza" class="nav-link">Cobranza</a>
          <a routerLink="/reportes" class="nav-link">Reportes</a>
        </nav>
      </aside>

      <main class="main-content">
        <div class="card">
          <router-outlet></router-outlet>
        </div>
      </main>
    </div>
  `
})
export class AppComponent {}
