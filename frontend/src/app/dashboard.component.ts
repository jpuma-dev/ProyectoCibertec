import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  template: `
    <div class="dashboard-page">
      <section class="dashboard-hero">
        <div class="dashboard-hero-copy">
          <span class="dashboard-eyebrow">Resumen operativo</span>
          <h1>Dashboard</h1>
          <p>Indicadores clave para revisar cobranza, ocupacion y cuentas pendientes del mercado.</p>
        </div>

        <div class="dashboard-hero-panel">
          <span>Recaudacion de hoy</span>
          <strong>S/ {{ dinero(totalRecaudado()) }}</strong>
          <small>{{ fechaCorta() }}</small>
          <button class="btn-primary" (click)="exportarExcel()">Exportar Excel</button>
        </div>
      </section>

      <section class="dashboard-summary-grid">
        <article class="dashboard-card metric-card metric-success">
          <div class="metric-topline">
            <span>Recaudado hoy</span>
            <b>S/</b>
          </div>
          <strong>{{ dinero(totalRecaudado()) }}</strong>
          <small>{{ numero(pagosProcesados()) }} pagos registrados</small>
        </article>

        <article class="dashboard-card metric-card metric-warning">
          <div class="metric-topline">
            <span>Pendiente por cobrar</span>
            <b>PE</b>
          </div>
          <strong>S/ {{ dinero(totalPendiente()) }}</strong>
          <small>{{ numero(stats?.cantidadDeudasPendientes) }} deudas pendientes</small>
        </article>

        <article class="dashboard-card metric-card metric-danger">
          <div class="metric-topline">
            <span>Deudas vencidas</span>
            <b>!</b>
          </div>
          <strong>{{ numero(stats?.cantidadDeudasVencidas) }}</strong>
          <small>S/ {{ dinero(stats?.montoTotalVencido) }} vencidos</small>
        </article>

        <article class="dashboard-card metric-card metric-primary">
          <div class="metric-topline">
            <span>Ocupacion de puestos</span>
            <b>%</b>
          </div>
          <strong>{{ ocupacionPuestos() }}%</strong>
          <small>{{ numero(stats?.puestosOcupados) }} de {{ numero(stats?.totalPuestos) }} ocupados</small>
        </article>
      </section>

      <section class="dashboard-layout">
        <div class="dashboard-main-column">
          <article class="dashboard-card">
            <div class="card-heading">
              <div>
                <h2>Estado de cobranza</h2>
                <p>Lectura rapida del riesgo y avance operativo.</p>
              </div>
            </div>

            <div class="status-grid">
              <div class="status-item">
                <span>Riesgo vencido</span>
                <strong>{{ riesgoVencido() }}%</strong>
                <div class="progress-track danger">
                  <span [style.width.%]="riesgoVencido()"></span>
                </div>
              </div>

              <div class="status-item">
                <span>Puestos ocupados</span>
                <strong>{{ ocupacionPuestos() }}%</strong>
                <div class="progress-track">
                  <span [style.width.%]="ocupacionPuestos()"></span>
                </div>
              </div>
            </div>

            <div class="insight-strip">
              <div>
                <span>Mayor deuda</span>
                <strong>S/ {{ dinero(mayorDeuda()) }}</strong>
              </div>
              <div>
                <span>Promedio pendiente</span>
                <strong>S/ {{ dinero(promedioPendiente()) }}</strong>
              </div>
              <div>
                <span>Socios con deuda</span>
                <strong>{{ numero(reporteDeudas.length) }}</strong>
              </div>
            </div>

            <div class="dashboard-alert">
              <div>
                <span>Prioridad del dia</span>
                <strong>{{ prioridadDelDia() }}</strong>
              </div>
              <p>{{ recomendacionCobranza() }}</p>
            </div>
          </article>

          <article class="dashboard-card">
            <div class="card-heading">
              <div>
                <h2>Deudas pendientes por socio</h2>
                <p>Ordena la atencion empezando por los montos mas altos.</p>
              </div>
            </div>

            <div class="dashboard-table-wrapper">
              <table class="dashboard-table">
                <thead>
                  <tr>
                    <th>Nro.</th>
                    <th>Socio</th>
                    <th>DNI</th>
                    <th>Prioridad</th>
                    <th style="text-align: right;">Total pendiente</th>
                  </tr>
                </thead>

                <tbody>
                  <tr *ngFor="let row of deudasOrdenadas(); let i = index">
                    <td>{{ i + 1 }}</td>
                    <td>
                      <div class="socio-report-cell">
                        <strong>{{ row[0] }}</strong>
                        <span>Socio responsable</span>
                      </div>
                    </td>
                    <td>{{ row[1] }}</td>
                    <td>
                      <span class="priority-badge" [ngClass]="prioridadClase(row[2])">
                        {{ prioridadTexto(row[2]) }}
                      </span>
                    </td>
                    <td class="reporte-deuda-monto">S/ {{ dinero(row[2]) }}</td>
                  </tr>
                </tbody>
              </table>

              <div class="reportes-empty" *ngIf="reporteDeudas.length === 0">
                No hay deudas pendientes para mostrar.
              </div>
            </div>
          </article>
        </div>

        <aside class="dashboard-side-column">
          <article class="dashboard-card side-summary-card">
            <div class="card-heading">
              <div>
                <h2>Resumen operativo</h2>
                <p>Estado actual de registros y puestos.</p>
              </div>
            </div>

            <dl class="summary-list">
              <div>
                <dt>Socios</dt>
                <dd>{{ numero(stats?.totalSocios) }}</dd>
              </div>
              <div>
                <dt>Puestos libres</dt>
                <dd>{{ numero(stats?.puestosDisponibles) }}</dd>
              </div>
              <div>
                <dt>Conceptos de deuda</dt>
                <dd>{{ numero(stats?.totalConceptos) }}</dd>
              </div>
              <div>
                <dt>Deudas pagadas</dt>
                <dd>{{ numero(stats?.cantidadDeudasPagadas) }}</dd>
              </div>
            </dl>
          </article>

          <article class="dashboard-card export-card">
            <div class="card-heading">
              <div>
                <h2>Reporte Excel</h2>
                <p>Deudas agrupadas por socio.</p>
              </div>
            </div>

            <div class="export-detail-grid">
              <div class="export-detail">
                <span>Registros</span>
                <strong>{{ reporteDeudas.length }}</strong>
              </div>
              <div class="export-detail">
                <span>Total</span>
                <strong>S/ {{ dinero(totalPendiente()) }}</strong>
              </div>
            </div>

            <button class="btn-primary" (click)="exportarExcel()">Descargar reporte</button>
          </article>

          <article class="dashboard-card compact-card">
            <div class="card-heading">
              <div>
                <h2>Distribucion</h2>
                <p>Socios por nivel de deuda.</p>
              </div>
            </div>

            <div class="priority-list">
              <div>
                <span class="priority-dot high"></span>
                <strong>Alta</strong>
                <b>{{ conteoPrioridad('Alta') }}</b>
              </div>
              <div>
                <span class="priority-dot medium"></span>
                <strong>Media</strong>
                <b>{{ conteoPrioridad('Media') }}</b>
              </div>
              <div>
                <span class="priority-dot low"></span>
                <strong>Baja</strong>
                <b>{{ conteoPrioridad('Baja') }}</b>
              </div>
            </div>
          </article>
        </aside>
      </section>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  reporteCaja: any;
  reporteDeudas: any[] = [];
  stats: any;

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getDashboardStats().subscribe((res: any) => {
      this.stats = res;
    });

    this.api.getReporteCaja().subscribe((res: any) => {
      this.reporteCaja = res;
    });

    this.api.getReporteDeudasSocio().subscribe((res: any[]) => {
      this.reporteDeudas = res;
    });
  }

  totalRecaudado() {
    return Number(this.reporteCaja?.totalRecaudado || this.stats?.recaudacionHoy || 0);
  }

  pagosProcesados() {
    return Number(this.reporteCaja?.cantidadPagos || 0);
  }

  totalPendiente() {
    if (this.stats?.montoTotalPendiente !== undefined) {
      return Number(this.stats.montoTotalPendiente || 0);
    }

    return this.reporteDeudas.reduce((total, row) => total + Number(row[2] || 0), 0);
  }

  ocupacionPuestos() {
    const total = Number(this.stats?.totalPuestos || 0);
    const ocupados = Number(this.stats?.puestosOcupados || 0);

    if (!total) {
      return 0;
    }

    return Math.round((ocupados / total) * 100);
  }

  riesgoVencido() {
    const pendientes = Number(this.stats?.cantidadDeudasPendientes || 0);
    const vencidas = Number(this.stats?.cantidadDeudasVencidas || 0);

    if (!pendientes) {
      return 0;
    }

    return Math.round((vencidas / pendientes) * 100);
  }

  mayorDeuda() {
    return this.reporteDeudas.reduce((mayor, row) => {
      const monto = Number(row[2] || 0);
      return monto > mayor ? monto : mayor;
    }, 0);
  }

  promedioPendiente() {
    if (!this.reporteDeudas.length) {
      return 0;
    }

    return this.totalPendiente() / this.reporteDeudas.length;
  }

  deudasOrdenadas() {
    return [...this.reporteDeudas].sort((a, b) => Number(b[2] || 0) - Number(a[2] || 0));
  }

  conteoPrioridad(tipo: string) {
    return this.reporteDeudas.filter((row) => this.prioridadTexto(row[2]) === tipo).length;
  }

  prioridadDelDia() {
    const altas = this.conteoPrioridad('Alta');

    if (altas > 0) {
      return `${altas} cuenta(s) en prioridad alta`;
    }

    if (this.reporteDeudas.length > 0) {
      return 'Seguimiento regular';
    }

    return 'Sin cuentas pendientes';
  }

  recomendacionCobranza() {
    if (this.conteoPrioridad('Alta') > 0) {
      return 'Contacta primero a los socios con mayor deuda y registra compromiso de pago.';
    }

    if (this.reporteDeudas.length > 0) {
      return 'Mantener seguimiento de saldos pendientes y actualizar pagos del dia.';
    }

    return 'No hay deudas pendientes para gestionar en este momento.';
  }

  prioridadTexto(valor: any) {
    const monto = Number(valor || 0);

    if (monto >= 500) {
      return 'Alta';
    }

    if (monto >= 100) {
      return 'Media';
    }

    return 'Baja';
  }

  prioridadClase(valor: any) {
    const monto = Number(valor || 0);

    if (monto >= 500) {
      return 'priority-high';
    }

    if (monto >= 100) {
      return 'priority-medium';
    }

    return 'priority-low';
  }

  numero(valor: any) {
    return Number(valor || 0).toLocaleString('es-PE');
  }

  dinero(valor: any) {
    return Number(valor || 0).toLocaleString('es-PE', {
      minimumFractionDigits: 2,
      maximumFractionDigits: 2
    });
  }

  fechaCorta() {
    return new Date().toLocaleDateString('es-PE', {
      day: '2-digit',
      month: 'short',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  exportarExcel() {
    window.open('http://localhost:8080/api/reportes/deudas/socio/export/excel', '_blank');
  }
}
