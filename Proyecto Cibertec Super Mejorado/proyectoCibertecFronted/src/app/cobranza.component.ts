import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-cobranza',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="cobranza-page">

      <div class="cobranza-header">
        <div>
          <h1>Cobranza / Caja</h1>
          <p>
            Registra pagos totales por deuda, controla los montos pendientes y revisa
            el historial de obligaciones generadas para los puestos del mercado.
          </p>
        </div>

        <div class="cobranza-status-pill">
          Caja activa
        </div>
      </div>

      <div class="cobranza-stats">
        <div class="cobranza-stat-card red">
          <span>Deudas pendientes</span>
          <strong>{{ deudasPendientes.length }}</strong>
        </div>

        <div class="cobranza-stat-card green">
          <span>Deudas pagadas</span>
          <strong>{{ totalPagadas() }}</strong>
        </div>

        <div class="cobranza-stat-card blue">
          <span>Monto pendiente</span>
          <strong>S/ {{ totalPendiente() }}</strong>
        </div>
      </div>

      <div class="cobranza-main-grid">

        <section class="cobranza-panel">
          <div class="cobranza-panel-header">
            <h2>Registrar pago</h2>
            <p>Seleccione una deuda pendiente y confirme el método de pago.</p>
          </div>

          <div class="cobranza-panel-body">

            <ng-container *ngIf="deudasPendientes.length > 0; else sinPendientes">

              <div class="form-group">
                <label>Deuda a pagar</label>
                <select [(ngModel)]="pago.deudaId">
                  <option *ngFor="let d of deudasPendientes" [value]="d.id">
                    Puesto {{ puestosTexto(d) }} - {{ d.conceptoNombre }} - S/ {{ d.monto }}
                  </option>
                </select>
              </div>

              <div class="deuda-selected-card" *ngIf="deudaSeleccionada() as deuda">
                <span>Detalle seleccionado</span>
                <strong>Puesto {{ puestosTexto(deuda) }} - {{ deuda.conceptoNombre }}</strong>
                <strong class="monto">S/ {{ deuda.monto }}</strong>
              </div>

              <div class="form-group">
                <label>Método de pago</label>

                <div class="payment-method-grid">
                  <div
                      class="payment-method"
                      [class.active]="pago.metodoPago === 'EFECTIVO'"
                      (click)="pago.metodoPago = 'EFECTIVO'"
                  >
                    Efectivo
                  </div>

                  <div
                      class="payment-method"
                      [class.active]="pago.metodoPago === 'YAPE'"
                      (click)="pago.metodoPago = 'YAPE'"
                  >
                    Yape / Plin
                  </div>

                  <div
                      class="payment-method"
                      [class.active]="pago.metodoPago === 'TRANSFERENCIA'"
                      (click)="pago.metodoPago = 'TRANSFERENCIA'"
                  >
                    Transferencia
                  </div>

                  <div
                      class="payment-method"
                      [class.active]="pago.metodoPago === 'TARJETA'"
                      (click)="pago.metodoPago = 'TARJETA'"
                  >
                    Tarjeta
                  </div>
                </div>
              </div>

              <div class="cobranza-actions">
                <button class="btn-success" (click)="pagar()">
                  Registrar Pago Total
                </button>
              </div>

            </ng-container>

            <ng-template #sinPendientes>
              <div class="cobranza-empty">
                No hay deudas pendientes por cobrar.
              </div>
            </ng-template>

          </div>
        </section>

        <section class="cobranza-panel">
          <div class="cobranza-panel-header">
            <h2>Resumen operativo</h2>
            <p>Indicadores rápidos del estado actual de cobranza.</p>
          </div>

          <div class="cobranza-panel-body">

            <div class="deuda-selected-card">
              <span>Próxima acción recomendada</span>
              <strong>
                {{ deudasPendientes.length > 0 ? 'Registrar pagos pendientes' : 'No existen cobros pendientes' }}
              </strong>
            </div>

            <div class="deuda-selected-card">
              <span>Método seleccionado</span>
              <strong>{{ pago.metodoPago }}</strong>
            </div>

            <div class="deuda-selected-card">
              <span>Registros totales</span>
              <strong>{{ allDeudas.length }} deudas registradas</strong>
            </div>

          </div>
        </section>

      </div>

      <section class="cobranza-table-card">
        <div class="cobranza-table-header">
          <div>
            <h2>Historial de deudas</h2>
            <p>Consulta general de deudas pagadas y pendientes.</p>
          </div>

          <div class="cobranza-filter">
            <select [(ngModel)]="filtroEstado">
              <option value="TODOS">Todos los estados</option>
              <option value="PENDIENTE">Solo pendientes</option>
              <option value="PAGADO">Solo pagadas</option>
            </select>
          </div>
        </div>

        <table>
          <thead>
          <tr>
            <th>Puesto</th>
            <th>Concepto</th>
            <th>Monto</th>
            <th>Estado</th>
          </tr>
          </thead>

          <tbody>
          <tr *ngFor="let d of deudasFiltradas()">
            <td>
              <div class="deuda-main-cell">
                <strong>Puesto {{ puestosTexto(d) }}</strong>
                <span>ID deuda: {{ d.id }}</span>
              </div>
            </td>

            <td>{{ d.conceptoNombre }}</td>

            <td
                class="monto-cell"
                [class.monto-pendiente]="d.estado === 'PENDIENTE'"
                [class.monto-pagado]="d.estado === 'PAGADO'"
            >
              S/ {{ d.monto }}
            </td>

            <td>
                <span
                    class="badge"
                    [class.estado-pagado]="d.estado === 'PAGADO'"
                    [class.estado-pendiente]="d.estado === 'PENDIENTE'"
                >
                  {{ d.estado }}
                </span>
            </td>
          </tr>
          </tbody>
        </table>

        <div class="empty-state" *ngIf="deudasFiltradas().length === 0">
          No hay registros para el filtro seleccionado.
        </div>
      </section>

    </div>
  `
})
export class CobranzaComponent implements OnInit {
  allDeudas: any[] = [];
  deudasPendientes: any[] = [];

  pago = {
    deudaId: null,
    metodoPago: 'EFECTIVO',
    monto: 0
  };

  filtroEstado: 'TODOS' | 'PENDIENTE' | 'PAGADO' = 'TODOS';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.api.getDeudas().subscribe((res: any[]) => {
      this.allDeudas = res;
      this.deudasPendientes = res.filter(d => d.estado === 'PENDIENTE');

      if (this.deudasPendientes.length > 0) {
        this.pago.deudaId = this.deudasPendientes[0].id;
      } else {
        this.pago.deudaId = null;
      }
    });
  }

  deudaSeleccionada() {
    return this.deudasPendientes.find(d => d.id == this.pago.deudaId);
  }

  puestosTexto(deuda: any) {
    if (!deuda || !deuda.puestoNumeros) {
      return '-';
    }

    return deuda.puestoNumeros.join(', ');
  }

  totalPagadas() {
    return this.allDeudas.filter(d => d.estado === 'PAGADO').length;
  }

  totalPendiente() {
    return this.deudasPendientes
        .reduce((total, d) => total + Number(d.monto || 0), 0)
        .toFixed(2);
  }

  deudasFiltradas() {
    if (this.filtroEstado === 'PENDIENTE') {
      return this.allDeudas.filter(d => d.estado === 'PENDIENTE');
    }

    if (this.filtroEstado === 'PAGADO') {
      return this.allDeudas.filter(d => d.estado === 'PAGADO');
    }

    return this.allDeudas;
  }

  pagar() {
    const deuda = this.deudaSeleccionada();

    if (!deuda) {
      alert('Seleccione una deuda pendiente');
      return;
    }

    this.pago.monto = deuda.monto;

    this.api.registrarPago(this.pago).subscribe({
      next: () => {
        alert('Pago registrado con éxito');
        this.cargar();
      },
      error: (err) => {
        alert('Error: ' + (err.error?.message || 'No se pudo procesar el pago'));
      }
    });
  }
}