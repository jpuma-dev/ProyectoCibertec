import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';

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
            <span>Gestión de Pagos</span>
          </div>
        </div>

        <nav class="sidebar-menu">
          <a routerLink="/socios" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">👥</span>
            <span>Socios</span>
          </a>

          <a routerLink="/puestos" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">🏪</span>
            <span>Puestos</span>
          </a>

          <a routerLink="/conceptos" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">📌</span>
            <span>Conceptos</span>
          </a>

          <a routerLink="/deudas" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">💲</span>
            <span>Generar Deudas</span>
          </a>

          <a routerLink="/cobranza" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">💰</span>
            <span>Cobranza</span>
          </a>

          <a routerLink="/reportes" routerLinkActive="active" class="nav-link">
            <span class="nav-icon">📊</span>
            <span>Reportes</span>
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