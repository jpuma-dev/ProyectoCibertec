import { Component } from '@angular/core';
import { RouterOutlet, RouterLink, RouterLinkActive } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-layout',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, CommonModule],
  template: `
    <div class="d-flex" style="min-height: 100vh;">
      <!-- Sidebar -->
      <div class="bg-dark text-white p-3" style="width: var(--sidebar-width); flex-shrink: 0;">
        <div class="d-flex align-items-center mb-4 mt-2 px-2">
          <i class="fas fa-store fs-3 me-2 text-primary"></i>
          <h4 class="m-0 fw-bold">MercadoAdmin</h4>
        </div>
        
        <ul class="nav flex-column mb-auto">
          <li class="nav-item mb-2">
            <a routerLink="/socios" routerLinkActive="active" class="nav-link text-white rounded d-flex align-items-center">
              <i class="fas fa-users fa-fw me-3"></i> Socios
            </a>
          </li>
          <li class="nav-item mb-2">
            <a routerLink="/conceptos" routerLinkActive="active" class="nav-link text-white rounded d-flex align-items-center">
              <i class="fas fa-tags fa-fw me-3"></i> Conceptos de Deuda
            </a>
          </li>
          <li class="nav-item mb-2">
            <a routerLink="/deudas" routerLinkActive="active" class="nav-link text-white rounded d-flex align-items-center">
              <i class="fas fa-file-invoice-dollar fa-fw me-3"></i> Deudas
            </a>
          </li>
          <li class="nav-item mb-2">
            <a routerLink="/pagos" routerLinkActive="active" class="nav-link text-white rounded d-flex align-items-center">
              <i class="fas fa-money-bill-wave fa-fw me-3"></i> Pagos
            </a>
          </li>
          <li class="nav-item mb-2">
            <a routerLink="/puestos" routerLinkActive="active" class="nav-link text-white rounded d-flex align-items-center">
              <i class="fas fa-store-alt fa-fw me-3"></i> Puestos
            </a>
          </li>
        </ul>
      </div>

      <!-- Main Content -->
      <div class="flex-grow-1 d-flex flex-column" style="min-width: 0;">
        <!-- Top Navbar -->
        <nav class="navbar navbar-expand-lg navbar-light bg-white border-bottom px-4 py-3">
          <div class="container-fluid">
            <span class="navbar-brand mb-0 h1">Panel de Control</span>
            <div class="d-flex align-items-center">
              <div class="dropdown">
                <a href="#" class="d-flex align-items-center text-dark text-decoration-none dropdown-toggle" id="dropdownUser1" data-bs-toggle="dropdown" aria-expanded="false">
                  <img src="https://ui-avatars.com/api/?name=Admin&background=4361ee&color=fff" alt="mdo" width="32" height="32" class="rounded-circle me-2">
                  <strong>Administrador</strong>
                </a>
                <ul class="dropdown-menu dropdown-menu-end shadow" aria-labelledby="dropdownUser1">
                  <li><a class="dropdown-item" href="#">Configuración</a></li>
                  <li><hr class="dropdown-divider"></li>
                  <li><a class="dropdown-item text-danger" href="#">Cerrar sesión</a></li>
                </ul>
              </div>
            </div>
          </div>
        </nav>

        <!-- Page Content -->
        <main class="p-4 flex-grow-1 bg-light overflow-auto">
          <router-outlet></router-outlet>
        </main>
      </div>
    </div>
  `,
  styles: [`
    .nav-link {
      opacity: 0.8;
      transition: all 0.3s ease;
    }
    .nav-link:hover, .nav-link.active {
      opacity: 1;
      background-color: rgba(255,255,255,0.1);
      transform: translateX(5px);
    }
  `]
})
export class LayoutComponent {}
