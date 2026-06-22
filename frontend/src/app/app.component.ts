import { Component } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive],
  template: `
    <div class="container">
      <aside class="sidebar">
        <div class="brand-box">
          <div class="brand-icon">M</div>

          <div class="brand-text">
            <h1>Mercado</h1>
            <span>Gestion de Pagos</span>
          </div>
        </div>

        <nav class="sidebar-menu">
          <a routerLink="/dashboard" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">DB</span>
            <span>Dashboard</span>
          </a>

          <a routerLink="/socios" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">SO</span>
            <span>Socios</span>
          </a>

          <a routerLink="/puestos" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">PU</span>
            <span>Puestos</span>
          </a>

          <a routerLink="/conceptos" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">CO</span>
            <span>Conceptos</span>
          </a>

          <a routerLink="/deudas" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">DE</span>
            <span>Generar Deudas</span>
          </a>

          <a routerLink="/cobranza" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">CB</span>
            <span>Cobranza</span>
          </a>
        </nav>

        <div class="sidebar-footer">
          <span>Sistema Web</span>
          <strong>DAWI - Cibertec</strong>
        </div>
      </aside>

      <main class="main-content">
        <router-outlet></router-outlet>
      </main>
    </div>
  `,
  styleUrl: './app.component.css'
})
export class AppComponent {}
