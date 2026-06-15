import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './services/api.service';

@Component({
    selector: 'app-conceptos',
    standalone: true,
    imports: [CommonModule, FormsModule],
    template: `
    <div class="conceptos-page">

      <div class="conceptos-header">
        <div>
          <h1>Conceptos de Cobro</h1>
          <p>
            Gestiona los motivos de pago del mercado, como luz, agua, seguridad,
            alquiler o cuotas extraordinarias.
          </p>
        </div>

        <button class="btn-add" (click)="mostrarFormulario = true">
          + Nuevo Concepto
        </button>
      </div>

      <div class="conceptos-stats">
        <div class="concepto-stat-card blue">
          <span>Total Conceptos</span>
          <strong>{{ conceptos.length }}</strong>
        </div>

        <div class="concepto-stat-card green">
          <span>Recurrentes</span>
          <strong>{{ totalRecurrentes() }}</strong>
        </div>

        <div class="concepto-stat-card orange">
          <span>No Recurrentes</span>
          <strong>{{ totalNoRecurrentes() }}</strong>
        </div>

        <div class="concepto-stat-card purple">
          <span>Monto Promedio</span>
          <strong>S/ {{ montoPromedio() }}</strong>
        </div>
      </div>

      <div class="conceptos-layout" [class.full-width]="!(mostrarFormulario || editando)">

        <section class="concepto-form-card" *ngIf="mostrarFormulario || editando">
          <h2>{{ editando ? 'Editar Concepto' : 'Nuevo Concepto' }}</h2>
          <p>Complete los datos del motivo de cobro.</p>

          <div class="form-group">
            <label>Nombre del concepto</label>
            <input
              [(ngModel)]="nuevoConcepto.nombre"
              placeholder="Ejemplo: Luz mensual"
            >
          </div>

          <div class="form-group">
            <label>Descripción</label>
            <textarea
              [(ngModel)]="nuevoConcepto.descripcion"
              placeholder="Descripción del concepto"
              rows="4"
            ></textarea>
          </div>

          <div class="form-group">
            <label>Monto sugerido (S/)</label>
            <input
              type="number"
              [(ngModel)]="nuevoConcepto.montoSugerido"
              placeholder="Ejemplo: 120.00"
              min="0"
            >
          </div>

          <div class="form-group">
            <label>¿Es recurrente?</label>
            <select [(ngModel)]="nuevoConcepto.recurrente">
              <option [ngValue]="true">Sí, se genera mensualmente</option>
              <option [ngValue]="false">No, solo cuando se requiera</option>
            </select>
          </div>

          <div class="form-actions">
            <button class="btn-primary" (click)="guardar()">
              {{ editando ? 'Actualizar Concepto' : 'Guardar Concepto' }}
            </button>

            <button class="btn-secondary" (click)="limpiar()">
              Cancelar
            </button>
          </div>
        </section>

        <section class="conceptos-table-card">
          <div class="conceptos-table-header">
            <div>
              <h2>Lista de Conceptos</h2>
              <p>Administra los conceptos usados para generar deudas.</p>
            </div>

            <div class="conceptos-tools">
              <input
                [(ngModel)]="busqueda"
                placeholder="Buscar concepto..."
              >

              <select [(ngModel)]="filtroRecurrente">
                <option value="TODOS">Todos</option>
                <option value="RECURRENTES">Recurrentes</option>
                <option value="NO_RECURRENTES">No recurrentes</option>
              </select>
            </div>
          </div>

          <div class="conceptos-table-wrapper">
            <table class="conceptos-table">
              <thead>
                <tr>
                  <th>#</th>
                  <th>Concepto</th>
                  <th>Descripción</th>
                  <th>Monto sugerido</th>
                  <th>Recurrente</th>
                  <th>Acciones</th>
                </tr>
              </thead>

              <tbody>
                <tr *ngFor="let c of conceptosFiltrados(); let i = index">
                  <td>{{ i + 1 }}</td>

                  <td>
                    <div class="concepto-name-cell">
                      <strong>{{ c.nombre }}</strong>
                      <span>Motivo de cobro</span>
                    </div>
                  </td>

                  <td class="concepto-descripcion">
                    {{ c.descripcion || '-' }}
                  </td>

                  <td class="concepto-monto">
                    S/ {{ c.montoSugerido || 0 }}
                  </td>

                  <td>
                    <span
                      class="badge"
                      [class.badge-success]="c.recurrente"
                      [class.badge-warning]="!c.recurrente"
                    >
                      {{ c.recurrente ? 'Sí' : 'No' }}
                    </span>
                  </td>

                  <td>
                    <div class="concepto-actions">
                      <button class="icon-btn edit" (click)="editar(c)" title="Editar">
                        ✎
                      </button>

                      <button class="icon-btn delete" (click)="eliminar(c.id)" title="Eliminar">
                        🗑
                      </button>
                    </div>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>

          <div class="empty-state" *ngIf="conceptosFiltrados().length === 0">
            No se encontraron conceptos registrados.
          </div>
        </section>

      </div>

    </div>
  `
})
export class ConceptosComponent implements OnInit {
    conceptos: any[] = [];

    nuevoConcepto: any = {
        nombre: '',
        descripcion: '',
        montoSugerido: 0,
        recurrente: true
    };

    editando = false;
    mostrarFormulario = false;
    busqueda = '';
    filtroRecurrente: 'TODOS' | 'RECURRENTES' | 'NO_RECURRENTES' = 'TODOS';

    constructor(private api: ApiService) {}

    ngOnInit() {
        this.cargar();
    }

    cargar() {
        this.api.getConceptos().subscribe({
            next: (res: any[]) => {
                this.conceptos = res;
            },
            error: () => {
                alert('No se pudieron cargar los conceptos');
            }
        });
    }

    conceptosFiltrados() {
        const texto = this.busqueda.toLowerCase().trim();

        return this.conceptos.filter((c: any) => {
            const coincideTexto =
                !texto ||
                c.nombre?.toLowerCase().includes(texto) ||
                c.descripcion?.toLowerCase().includes(texto);

            const coincideFiltro =
                this.filtroRecurrente === 'TODOS' ||
                (this.filtroRecurrente === 'RECURRENTES' && c.recurrente) ||
                (this.filtroRecurrente === 'NO_RECURRENTES' && !c.recurrente);

            return coincideTexto && coincideFiltro;
        });
    }

    totalRecurrentes() {
        return this.conceptos.filter(c => c.recurrente).length;
    }

    totalNoRecurrentes() {
        return this.conceptos.filter(c => !c.recurrente).length;
    }

    montoPromedio() {
        if (this.conceptos.length === 0) {
            return '0.00';
        }

        const total = this.conceptos.reduce(
            (sum, c) => sum + Number(c.montoSugerido || 0),
            0
        );

        return (total / this.conceptos.length).toFixed(2);
    }

    editar(concepto: any) {
        this.nuevoConcepto = { ...concepto };
        this.editando = true;
        this.mostrarFormulario = true;
    }

    eliminar(id: number) {
        if (confirm('¿Desea eliminar este concepto de cobro?')) {
            this.api.deleteConcepto(id).subscribe({
                next: () => {
                    alert('Concepto eliminado');
                    this.cargar();
                },
                error: (err) => {
                    alert('Error: ' + (err.error?.message || 'No se pudo eliminar'));
                }
            });
        }
    }

    guardar() {
        if (!this.nuevoConcepto.nombre?.trim()) {
            alert('Ingrese el nombre del concepto');
            return;
        }

        if (this.nuevoConcepto.montoSugerido == null || this.nuevoConcepto.montoSugerido < 0) {
            alert('Ingrese un monto sugerido válido');
            return;
        }

        this.nuevoConcepto.nombre = this.nuevoConcepto.nombre.trim();

        const operacion = this.editando
            ? this.api.updateConcepto(this.nuevoConcepto.id, this.nuevoConcepto)
            : this.api.crearConcepto(this.nuevoConcepto);

        operacion.subscribe({
            next: () => {
                alert(this.editando ? 'Concepto actualizado' : 'Concepto registrado');
                this.limpiar();
                this.cargar();
            },
            error: (err: any) => {
                alert('Error: ' + (err.error?.message || 'Error en operación'));
            }
        });
    }

    limpiar() {
        this.nuevoConcepto = {
            nombre: '',
            descripcion: '',
            montoSugerido: 0,
            recurrente: true
        };

        this.editando = false;
        this.mostrarFormulario = false;
    }
}