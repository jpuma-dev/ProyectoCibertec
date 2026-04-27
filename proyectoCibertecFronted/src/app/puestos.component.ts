import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-puestos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="puestos-page">

      <div class="page-top">
        <div>
          <h1>Gestión de Puestos</h1>
          <p>Registro y administración de locales físicos</p>
        </div>

        <button class="btn-add" (click)="mostrarFormulario = true">
          + Agregar Puesto
        </button>
      </div>

      <div class="stats-grid">
        <div class="stat-card">
          <span>Total Puestos</span>
          <strong>{{ puestos.length }}</strong>
        </div>

        <div class="stat-card green">
          <span>Puestos Ocupados</span>
          <strong>{{ puestosOcupados() }}</strong>
        </div>

        <div class="stat-card">
          <span>Puestos Disponibles</span>
          <strong>{{ puestosDisponibles() }}</strong>
        </div>
      </div>

      <div class="form-card" *ngIf="mostrarFormulario || editando">
        <h3>{{ editando ? 'Editar Puesto' : 'Registrar Puesto' }}</h3>

        <p class="form-help">
          Formato requerido: letras y números de 2 a 5 caracteres. Ejemplo: A01, B02, 101.
        </p>

        <div class="form-grid">
          <div class="form-group">
            <label>Nro. de Puesto</label>
            <input
                [(ngModel)]="nuevoPuesto.numero"
                placeholder="Ejemplo: A01"
                maxlength="5"
                (keyup.enter)="guardar()"
            >
          </div>

          <div class="form-group">
            <label>Descripción</label>
            <input
                [(ngModel)]="nuevoPuesto.descripcion"
                placeholder="Ejemplo: Puesto de frutas"
                (keyup.enter)="guardar()"
            >
          </div>

          <div class="form-group">
            <label>Socio / Dueño</label>
            <select [(ngModel)]="nuevoPuesto.socioId">
              <option [ngValue]="null">Asociación</option>
              <option *ngFor="let s of socios" [ngValue]="s.id">
                {{ s.nombre }} {{ s.apellido }}
              </option>
            </select>
          </div>
        </div>

        <div class="form-actions">
          <button class="btn-primary" (click)="guardar()">
            {{ editando ? 'Actualizar Puesto' : 'Registrar Puesto' }}
          </button>

          <button class="btn-secondary" (click)="limpiar()">
            Cancelar
          </button>
        </div>
      </div>

      <div class="filter-tabs">
        <button
            [class.active]="filtro === 'TODOS'"
            (click)="filtro = 'TODOS'"
        >
          Todos ({{ puestos.length }})
        </button>

        <button
            [class.active]="filtro === 'OCUPADOS'"
            (click)="filtro = 'OCUPADOS'"
        >
          Ocupados ({{ puestosOcupados() }})
        </button>

        <button
            [class.active]="filtro === 'DISPONIBLES'"
            (click)="filtro = 'DISPONIBLES'"
        >
          Disponibles ({{ puestosDisponibles() }})
        </button>
      </div>

      <div class="puestos-grid">
        <div
            class="puesto-card"
            *ngFor="let p of puestosFiltrados()"
            [class.disponible]="!p.socioNombreCompleto"
        >
          <div class="puesto-card-header">
            <div>
              <h3>Puesto {{ p.numero }}</h3>
              <p>{{ p.descripcion || 'Sin descripción' }}</p>
            </div>

            <div class="puesto-actions">
              <button class="icon-btn edit" (click)="editar(p)" title="Editar">
                ✎
              </button>

              <button class="icon-btn delete" (click)="eliminar(p.id)" title="Eliminar">
                🗑
              </button>
            </div>
          </div>

          <div class="owner-box" *ngIf="p.socioNombreCompleto; else sinSocio">
            <strong>{{ p.socioNombreCompleto }}</strong>
            <span>Socio asignado</span>
          </div>

          <ng-template #sinSocio>
            <div class="owner-box empty">
              Sin socio asignado
            </div>
          </ng-template>
        </div>
      </div>

      <div class="empty-state" *ngIf="puestosFiltrados().length === 0">
        No hay puestos registrados para este filtro.
      </div>

    </div>
  `
})
export class PuestosComponent implements OnInit {
  puestos: any[] = [];
  socios: any[] = [];

  nuevoPuesto: any = {
    numero: '',
    descripcion: '',
    socioId: null
  };

  editando = false;
  mostrarFormulario = false;

  filtro: 'TODOS' | 'OCUPADOS' | 'DISPONIBLES' = 'TODOS';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.cargar();

    this.api.getSocios().subscribe((res: any[]) => {
      this.socios = res;
    });
  }

  cargar() {
    this.api.getPuestos().subscribe((res: any[]) => {
      this.puestos = res;
    });
  }

  puestosOcupados() {
    return this.puestos.filter(p => p.socioNombreCompleto).length;
  }

  puestosDisponibles() {
    return this.puestos.filter(p => !p.socioNombreCompleto).length;
  }

  puestosFiltrados() {
    if (this.filtro === 'OCUPADOS') {
      return this.puestos.filter(p => p.socioNombreCompleto);
    }

    if (this.filtro === 'DISPONIBLES') {
      return this.puestos.filter(p => !p.socioNombreCompleto);
    }

    return this.puestos;
  }

  editar(p: any) {
    this.nuevoPuesto = { ...p };

    const socio = this.socios.find(
        s => (s.nombre + ' ' + s.apellido) === p.socioNombreCompleto
    );

    this.nuevoPuesto.socioId = socio ? socio.id : null;
    this.editando = true;
    this.mostrarFormulario = true;
  }

  eliminar(id: number) {
    if (confirm('¿Desea eliminar este puesto?')) {
      this.api.deletePuesto(id).subscribe({
        next: () => {
          alert('Puesto eliminado');
          this.cargar();
        },
        error: (err) => {
          alert('Error: ' + (err.error?.message || 'No se pudo eliminar'));
        }
      });
    }
  }

  guardar() {
    if (!this.nuevoPuesto.numero) {
      alert('Ingrese el número del puesto');
      return;
    }

    this.nuevoPuesto.numero = this.nuevoPuesto.numero.trim().toUpperCase();

    const operacion = this.editando
        ? this.api.updatePuesto(this.nuevoPuesto.id, this.nuevoPuesto)
        : this.api.crearPuesto(this.nuevoPuesto);

    operacion.subscribe({
      next: () => {
        alert(this.editando ? 'Puesto actualizado' : 'Puesto registrado');
        this.limpiar();
        this.cargar();
      },
      error: (err: any) => {
        alert('Error: ' + (err.error?.message || 'Error en operación'));
      }
    });
  }

  limpiar() {
    this.nuevoPuesto = {
      numero: '',
      descripcion: '',
      socioId: null
    };

    this.editando = false;
    this.mostrarFormulario = false;
  }
}


