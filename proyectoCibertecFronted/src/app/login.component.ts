import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { ApiService } from './services/api.service';

/**
 * Componente de Login / Registro.
 * Consume POST /api/auth/login y POST /api/auth/register del backend.
 * Las contraseñas se almacenan cifradas con BCrypt en la BD.
 */
@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="login-bg">
      <div class="login-card">

        <!-- Logo -->
        <div class="login-logo">
          <div class="logo-circle">M</div>
          <h1>Mercado</h1>
          <span>Sistema de Gestión de Pagos</span>
        </div>

        <!-- Tabs login / register -->
        <div class="login-tabs">
          <button [class.active]="modo === 'login'"    (click)="modo = 'login'">Iniciar Sesión</button>
          <button [class.active]="modo === 'register'" (click)="modo = 'register'">Registrarse</button>
        </div>

        <!-- Error -->
        <div class="login-error" *ngIf="error">{{ error }}</div>

        <!-- Formulario -->
        <div class="login-form">
          <div class="form-group">
            <label>Usuario</label>
            <input [(ngModel)]="username" placeholder="Ingresa tu usuario"
                   (keyup.enter)="submit()" autocomplete="username">
          </div>

          <div class="form-group">
            <label>Contraseña</label>
            <input [(ngModel)]="password" type="password"
                   placeholder="Ingresa tu contraseña"
                   (keyup.enter)="submit()" autocomplete="current-password">
          </div>

          <button class="btn-login" (click)="submit()" [disabled]="cargando">
            {{ cargando ? 'Procesando...' : (modo === 'login' ? 'Iniciar Sesión' : 'Registrarse') }}
          </button>
        </div>

        <p class="login-footer">DAWI - Cibertec 2025</p>
      </div>
    </div>
  `,
  styles: [`
    .login-bg {
      min-height: 100vh;
      background: linear-gradient(135deg, #1e3a5f 0%, #2563eb 100%);
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .login-card {
      background: white;
      border-radius: 1.5rem;
      padding: 2.5rem 2rem;
      width: 100%;
      max-width: 400px;
      box-shadow: 0 25px 60px rgba(0,0,0,0.3);
    }
    .login-logo {
      text-align: center;
      margin-bottom: 1.5rem;
    }
    .logo-circle {
      width: 60px; height: 60px;
      background: linear-gradient(135deg, #2563eb, #38bdf8);
      border-radius: 50%;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      color: white;
      font-size: 1.8rem;
      font-weight: 900;
      margin-bottom: 0.5rem;
    }
    .login-logo h1 { margin: 0; font-size: 1.5rem; color: #0f172a; }
    .login-logo span { color: #64748b; font-size: 0.85rem; }
    .login-tabs {
      display: flex;
      background: #f1f5f9;
      border-radius: 0.75rem;
      padding: 4px;
      margin-bottom: 1.25rem;
    }
    .login-tabs button {
      flex: 1; border: none; background: transparent;
      padding: 0.6rem; border-radius: 0.6rem;
      cursor: pointer; font-weight: 600; color: #64748b;
      transition: all 0.2s;
    }
    .login-tabs button.active {
      background: white; color: #2563eb;
      box-shadow: 0 2px 8px rgba(0,0,0,0.1);
    }
    .login-error {
      background: #fef2f2; color: #dc2626;
      border: 1px solid #fecaca;
      border-radius: 0.5rem;
      padding: 0.75rem 1rem;
      margin-bottom: 1rem;
      font-size: 0.9rem;
    }
    .login-form .form-group { margin-bottom: 1rem; }
    .login-form label {
      display: block; margin-bottom: 0.4rem;
      font-weight: 600; color: #374151; font-size: 0.9rem;
    }
    .login-form input {
      width: 100%; box-sizing: border-box;
      padding: 0.75rem 1rem;
      border: 1.5px solid #e2e8f0;
      border-radius: 0.75rem;
      font-size: 0.95rem;
      transition: border-color 0.2s;
    }
    .login-form input:focus {
      outline: none; border-color: #2563eb;
      box-shadow: 0 0 0 3px rgba(37,99,235,0.1);
    }
    .btn-login {
      width: 100%; padding: 0.85rem;
      background: linear-gradient(135deg, #2563eb, #38bdf8);
      color: white; border: none;
      border-radius: 0.75rem;
      font-size: 1rem; font-weight: 700;
      cursor: pointer; margin-top: 0.5rem;
      transition: opacity 0.2s;
    }
    .btn-login:hover { opacity: 0.9; }
    .btn-login:disabled { opacity: 0.6; cursor: not-allowed; }
    .login-footer {
      text-align: center; color: #94a3b8;
      font-size: 0.8rem; margin-top: 1.5rem; margin-bottom: 0;
    }
  `]
})
export class LoginComponent {
  modo: 'login' | 'register' = 'login';
  username = '';
  password = '';
  error = '';
  cargando = false;

  constructor(private api: ApiService, private router: Router) {}

  submit() {
    this.error = '';
    if (!this.username.trim() || !this.password.trim()) {
      this.error = 'Ingresa usuario y contraseña.';
      return;
    }
    this.cargando = true;

    const operacion = this.modo === 'login'
      ? this.api.login(this.username.trim(), this.password.trim())
      : this.api.register(this.username.trim(), this.password.trim());

    operacion.subscribe({
      next: () => {
        this.cargando = false;
        this.router.navigate(['/reportes']);
      },
      error: (err) => {
        this.cargando = false;
        this.error = err.error?.message || 'Credenciales incorrectas. Intenta de nuevo.';
      }
    });
  }
}
