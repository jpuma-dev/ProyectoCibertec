import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-socios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="socios-page">

      <div class="page-top">
        <div>
          <h1>Gestión de Socios</h1>
          <p>Registro y administración de socios responsables del mercado</p>
        </div>

        <button class="btn-add" (click)="mostrarFormulario = true">
          + Agregar Socio
        </button>
      </div>

      <div class="stats-grid">
        <div class="stat-card">
          <span>Total Socios</span>
          <strong>{{ socios.length }}</strong>
        </div>

        <div class="stat-card green">
          <span>Socios Activos</span>
          <strong>{{ socios.length }}</strong>
        </div>

        <div class="stat-card">
          <span>Registros filtrados</span>
          <strong>{{ sociosFiltrados().length }}</strong>
        </div>
      </div>

      <div class="socio-form-card" *ngIf="mostrarFormulario || editando">
        <h3>{{ editando ? 'Editar Socio' : 'Registrar Socio' }}</h3>

        <p class="form-help">
          Complete los datos personales del socio. El DNI debe tener 8 dígitos y el teléfono debe iniciar con 9.
        </p>

        <div class="socio-form-grid">
          <div class="form-group">
            <label>Nombre</label>
            <input
              [(ngModel)]="nuevoSocio.nombre"
              placeholder="Ejemplo: Juan"
            >
          </div>

          <div class="form-group">
            <label>Apellido</label>
            <input
              [(ngModel)]="nuevoSocio.apellido"
              placeholder="Ejemplo: Pérez"
            >
          </div>

          <div class="form-group">
            <label>DNI</label>
            <input
              [(ngModel)]="nuevoSocio.dni"
              placeholder="Ejemplo: 12345678"
              maxlength="8"
            >
          </div>

          <div class="form-group">
            <label>Teléfono</label>
            <input
              [(ngModel)]="nuevoSocio.telefono"
              placeholder="Ejemplo: 987654321"
              maxlength="9"
            >
          </div>

          <div class="form-group full">
            <label>Email</label>
            <input
              [(ngModel)]="nuevoSocio.email"
              placeholder="correo@email.com"
              (keyup.enter)="guardar()"
            >
          </div>
        </div>

        <div class="form-actions">
          <button class="btn-primary" (click)="guardar()">
            {{ editando ? 'Actualizar Socio' : 'Registrar Socio' }}
          </button>

          <button class="btn-secondary" (click)="limpiar()">
            Cancelar
          </button>
        </div>
      </div>

      <div class="socios-toolbar">
        <div class="search-box">
          <input
            [(ngModel)]="busqueda"
            placeholder="Buscar por nombre, apellido, DNI o teléfono..."
          >
        </div>
      </div>

      <div class="socios-table-card">
        <table>
          <thead>
            <tr>
              <th>Socio</th>
              <th>DNI</th>
              <th>Teléfono</th>
              <th>Email</th>
              <th>Estado</th>
              <th>Acciones</th>
            </tr>
          </thead>

          <tbody>
            <tr *ngFor="let s of sociosFiltrados()">
              <td>
                <div class="socio-cell">
                  <div class="socio-avatar">
                    {{ iniciales(s) }}
                  </div>

                  <div class="socio-info">
                    <strong>{{ s.nombre }} {{ s.apellido }}</strong>
                    <span>Socio del mercado</span>
                  </div>
                </div>
              </td>

              <td>{{ s.dni }}</td>
              <td>{{ s.telefono }}</td>
              <td>{{ s.email || 'Sin correo' }}</td>

              <td>
                <span class="badge badge-success">
                  Activo
                </span>
              </td>

              <td>
                <div class="socio-actions">
                  <button class="icon-btn edit" (click)="editar(s)" title="Editar">
                    ✎
                  </button>

                  <button class="icon-btn delete" (click)="eliminar(s.dni)" title="Eliminar">
                    🗑
                  </button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>

        <div class="empty-state" *ngIf="sociosFiltrados().length === 0">
          No se encontraron socios registrados.
        </div>
      </div>

    </div>
  `
})
export class SociosComponent implements OnInit {
  socios: any[] = [];

  nuevoSocio: any = {
    nombre: '',
    apellido: '',
    dni: '',
    telefono: '',
    email: ''
  };

  editando = false;
  mostrarFormulario = false;
  dniOriginal = '';
  busqueda = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.api.getSocios().subscribe((res: any[]) => {
      this.socios = res;
    });
  }

  sociosFiltrados() {
    const texto = this.busqueda.toLowerCase().trim();

    if (!texto) {
      return this.socios;
    }

    return this.socios.filter((s: any) =>
        s.nombre?.toLowerCase().includes(texto) ||
        s.apellido?.toLowerCase().includes(texto) ||
        s.dni?.includes(texto) ||
        s.telefono?.includes(texto) ||
        s.email?.toLowerCase().includes(texto)
    );
  }

  iniciales(s: any) {
    const nombre = s.nombre ? s.nombre.charAt(0) : '';
    const apellido = s.apellido ? s.apellido.charAt(0) : '';
    return (nombre + apellido).toUpperCase();
  }

  editar(s: any) {
    this.nuevoSocio = { ...s };
    this.dniOriginal = s.dni;
    this.editando = true;
    this.mostrarFormulario = true;
  }

  eliminar(dni: string) {
    if (confirm('¿Desea eliminar este socio?')) {
      this.api.deleteSocio(dni).subscribe({
        next: () => {
          alert('Socio eliminado');
          this.cargar();
        },
        error: (err) => {
          alert('Error: ' + (err.error?.message || 'No se pudo eliminar'));
        }
      });
    }
  }

  guardar() {
    if (!this.nuevoSocio.nombre?.trim()) {
      alert('Ingrese el nombre del socio');
      return;
    }

    if (!this.nuevoSocio.apellido?.trim()) {
      alert('Ingrese el apellido del socio');
      return;
    }

    if (!this.nuevoSocio.dni || this.nuevoSocio.dni.length !== 8) {
      alert('El DNI debe tener 8 dígitos');
      return;
    }

    if (!this.nuevoSocio.telefono || this.nuevoSocio.telefono.length !== 9) {
      alert('El teléfono debe tener 9 dígitos');
      return;
    }

    this.nuevoSocio.nombre = this.nuevoSocio.nombre.trim();
    this.nuevoSocio.apellido = this.nuevoSocio.apellido.trim();
    this.nuevoSocio.dni = this.nuevoSocio.dni.trim();
    this.nuevoSocio.telefono = this.nuevoSocio.telefono.trim();

    const operacion = this.editando
        ? this.api.updateSocio(this.dniOriginal, this.nuevoSocio)
        : this.api.crearSocio(this.nuevoSocio);

    operacion.subscribe({
      next: () => {
        alert(this.editando ? 'Socio actualizado' : 'Socio registrado');
        this.limpiar();
        this.cargar();
      },
      error: (err: any) => {
        alert('Error: ' + (err.error?.message || 'Error en operación'));
      }
    });
  }

  limpiar() {
    this.nuevoSocio = {
      nombre: '',
      apellido: '',
      dni: '',
      telefono: '',
      email: ''
    };

    this.editando = false;
    this.mostrarFormulario = false;
    this.dniOriginal = '';
  }
}
