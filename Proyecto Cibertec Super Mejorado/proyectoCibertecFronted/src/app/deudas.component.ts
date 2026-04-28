import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-deudas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="deudas-page">

      <div class="deudas-header">
        <div>
          <h1>Generación de Deudas</h1>
          <p>
            Configura cargos masivos para los puestos del mercado, definiendo el tipo de distribución,
            el concepto de cobro, el monto y la fecha límite de pago.
          </p>
        </div>

        <div class="deudas-status-pill">
          {{ conceptos.length }} conceptos disponibles
        </div>
      </div>

      <div class="deudas-grid-main">

        <section class="deudas-panel">
          <div class="deudas-panel-header">
            <h2>Configuración del cargo</h2>
            <p>Completa los pasos para generar las deudas correctamente.</p>
          </div>

          <div class="deudas-panel-body">

            <div class="deuda-step">
              <div class="deuda-step-number">1</div>

              <div class="deuda-step-content">
                <h3>Selecciona el tipo de distribución</h3>
                <p>Define si el monto será fijo por puesto o si un monto total será repartido.</p>

                <div class="deuda-options">
                  <div
                    class="deuda-option-card"
                    [class.active]="tipoCobro === 'INDIVIDUAL'"
                    (click)="tipoCobro = 'INDIVIDUAL'"
                  >
                    <strong>Monto fijo por puesto</strong>
                    <span>El mismo importe será asignado a cada puesto seleccionado.</span>
                  </div>

                  <div
                    class="deuda-option-card"
                    [class.active]="tipoCobro === 'TOTAL'"
                    (click)="tipoCobro = 'TOTAL'"
                  >
                    <strong>Dividir monto total</strong>
                    <span>El sistema distribuirá el monto total entre los puestos aplicables.</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="deuda-step">
              <div class="deuda-step-number">2</div>

              <div class="deuda-step-content">
                <h3>Ingresa los datos del cobro</h3>
                <p>Selecciona el concepto de deuda, monto y fecha límite.</p>

                <div class="deuda-form-grid">
                  <div class="form-group" *ngIf="tipoCobro === 'INDIVIDUAL'">
                    <label>Monto por puesto (S/)</label>
                    <input
                      type="number"
                      [(ngModel)]="data.monto"
                      placeholder="Ejemplo: 50.00"
                      min="0"
                    >
                  </div>

                  <div class="form-group" *ngIf="tipoCobro === 'TOTAL'">
                    <label>Monto total a repartir (S/)</label>
                    <input
                      type="number"
                      [(ngModel)]="data.montoTotal"
                      placeholder="Ejemplo: 500.00"
                      min="0"
                    >
                  </div>

                  <div class="form-group">
                    <label>Concepto de deuda</label>
                    <select [(ngModel)]="data.conceptoId">
                      <option [ngValue]="null" disabled>Seleccione un concepto...</option>
                      <option *ngFor="let c of conceptos" [ngValue]="c.id">
                        {{ c.nombre }}
                      </option>
                    </select>
                  </div>

                  <div class="form-group full">
                    <label>Último día de pago</label>
                    <input
                      type="date"
                      [(ngModel)]="data.fecha"
                      [min]="hoy"
                    >
                  </div>
                </div>
              </div>
            </div>

            <div class="deuda-step">
              <div class="deuda-step-number">3</div>

              <div class="deuda-step-content">
                <h3>Define los puestos afectados</h3>
                <p>Puedes aplicar el cargo a todos los puestos o escribir IDs específicos.</p>

                <div class="form-group">
                  <label>Puestos específicos</label>
                  <input
                    [(ngModel)]="puestoIdsStr"
                    placeholder="Ejemplo: 1, 5, 12"
                  >
                </div>

                <div class="deuda-note">
                  Si este campo queda vacío, el sistema generará la deuda para todos los puestos aplicables.
                  Si deseas limitar el cobro, escribe los IDs separados por comas.
                </div>
              </div>
            </div>

          </div>
        </section>

        <aside class="deuda-summary">
          <div class="deuda-summary-header">
            <h2>Resumen del cargo</h2>
            <p>Vista previa antes de generar las deudas.</p>
          </div>

          <div class="deuda-summary-body">

            <div class="summary-line">
              <span>Distribución</span>
              <strong>{{ tipoDistribucionTexto() }}</strong>
            </div>

            <div class="summary-line">
              <span>Concepto</span>
              <strong>{{ conceptoSeleccionado() }}</strong>
            </div>

            <div class="summary-line">
              <span>Aplicación</span>
              <strong>{{ aplicacionTexto() }}</strong>
            </div>

            <div class="summary-line">
              <span>Fecha límite</span>
              <strong>{{ data.fecha }}</strong>
            </div>

            <div class="summary-total-box">
              <span>Monto configurado</span>
              <strong>S/ {{ montoMostrado() }}</strong>
            </div>

            <div class="summary-actions">
              <button class="btn-primary" (click)="generar()">
                Generar Deudas
              </button>

              <button class="btn-secondary" (click)="limpiar()">
                Limpiar formulario
              </button>
            </div>

          </div>
        </aside>

      </div>

    </div>
  `
})
export class DeudasComponent implements OnInit {
  tipoCobro: 'INDIVIDUAL' | 'TOTAL' = 'INDIVIDUAL';

  conceptos: any[] = [];
  hoy = new Date().toISOString().split('T')[0];

  data: any = {
    monto: 0,
    montoTotal: 0,
    conceptoId: null,
    fecha: new Date().toISOString().split('T')[0],
    puestoIds: [] as number[]
  };

  puestoIdsStr = '';

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getConceptos().subscribe((res: any[]) => {
      this.conceptos = res;
    });
  }

  tipoDistribucionTexto() {
    return this.tipoCobro === 'INDIVIDUAL'
        ? 'Monto fijo por puesto'
        : 'Monto total dividido';
  }

  montoMostrado() {
    if (this.tipoCobro === 'INDIVIDUAL') {
      return this.data.monto || 0;
    }

    return this.data.montoTotal || 0;
  }

  conceptoSeleccionado() {
    const concepto = this.conceptos.find(c => c.id == this.data.conceptoId);
    return concepto ? concepto.nombre : 'Sin seleccionar';
  }

  aplicacionTexto() {
    return this.puestoIdsStr.trim()
        ? 'Puestos específicos'
        : 'Todos los puestos aplicables';
  }

  limpiar() {
    this.tipoCobro = 'INDIVIDUAL';

    this.data = {
      monto: 0,
      montoTotal: 0,
      conceptoId: null,
      fecha: new Date().toISOString().split('T')[0],
      puestoIds: []
    };

    this.puestoIdsStr = '';
  }

  generar() {
    if (!this.data.conceptoId) {
      alert('Seleccione un concepto de deuda');
      return;
    }

    if (this.tipoCobro === 'INDIVIDUAL') {
      if (!this.data.monto || this.data.monto <= 0) {
        alert('Ingrese un monto por puesto válido');
        return;
      }
    }

    if (this.tipoCobro === 'TOTAL') {
      if (!this.data.montoTotal || this.data.montoTotal <= 0) {
        alert('Ingrese un monto total válido');
        return;
      }
    }

    if (!this.data.fecha) {
      alert('Seleccione la fecha límite de pago');
      return;
    }

    if (this.puestoIdsStr.trim()) {
      this.data.puestoIds = this.puestoIdsStr
          .split(',')
          .map(id => parseInt(id.trim()))
          .filter(id => !isNaN(id));
    } else {
      this.data.puestoIds = null;
    }

    if (this.tipoCobro === 'INDIVIDUAL') {
      this.data.montoTotal = null;
    } else {
      this.data.monto = null;
    }

    this.api.generarDeudaMasiva(this.data).subscribe({
      next: () => {
        alert('¡Deudas generadas con éxito!');
        this.limpiar();
      },
      error: (err) => {
        alert('Error: ' + (err.error?.message || 'No se pudo generar las deudas'));
      }
    });
  }
}