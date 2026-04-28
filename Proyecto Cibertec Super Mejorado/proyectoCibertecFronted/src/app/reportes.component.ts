import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from './services/api.service';

@Component({
  templateUrl: './reportes.component.html',
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule],
  template: `
  
    <div class="reportes-page">

      <div class="reportes-header">
        <div>
          <h1>Reportes del Sistema</h1>
          <p>
            Consulta indicadores financieros, flujo de caja diario y deudas pendientes
            consolidadas por socio para el control administrativo del mercado.
          </p>
        </div>

        <div class="reportes-status-pill">
          Reportes activos
        </div>
      </div>

      <div class="reportes-kpi-grid">
        <div class="reporte-kpi-card green">
          <span>Total recaudado hoy</span>
          <strong>S/ {{ totalRecaudado() }}</strong>
          <small>Flujo de caja diario</small>
        </div>

        <div class="reporte-kpi-card blue">
          <span>Pagos procesados</span>
          <strong>{{ pagosProcesados() }}</strong>
          <small>Operaciones registradas</small>
        </div>

        <div class="reporte-kpi-card red">
          <span>Total pendiente</span>
          <strong>S/ {{ totalPendiente() }}</strong>
          <small>Cuentas por cobrar</small>
        </div>
      </div>

      <div class="reportes-main-grid">

        <section class="reportes-table-card">
          <div class="reportes-table-header">
            <div>
              <h2>Deudas pendientes por socio</h2>
              <p>Consolidado de cuentas pendientes agrupadas por socio.</p>
            </div>
          </div>

          <div class="reportes-table-wrapper">
            <table>
              <thead>
              <tr>
                <th>Socio</th>
                <th>DNI</th>
                <th style="text-align: right;">Total pendiente</th>
              </tr>
              </thead>

              <tbody>
              <tr *ngFor="let row of reporteDeudas">
                <td>
                  <div class="socio-report-cell">
                    <strong>{{ row[0] }}</strong>
                    <span>Socio responsable</span>
                  </div>
                </td>

                <td>{{ row[1] }}</td>

                <td class="reporte-deuda-monto">
                  S/ {{ row[2] }}
                </td>
              </tr>
              </tbody>
            </table>

            <div class="reportes-empty" *ngIf="reporteDeudas.length === 0">
              No hay deudas pendientes para mostrar.
            </div>
          </div>
        </section>

        <aside class="reportes-action-card">
          <div class="reportes-action-header">
            <h2>Exportación</h2>
            <p>Descarga el reporte para sustento administrativo.</p>
          </div>

          <div class="reportes-action-body">
            <div class="report-action-item">
              <span>Tipo de reporte</span>
              <strong>Deudas por socio</strong>
            </div>

            <div class="report-action-item">
              <span>Formato</span>
              <strong>Excel (.xlsx)</strong>
            </div>

            <div class="report-action-item">
              <span>Registros incluidos</span>
              <strong>{{ reporteDeudas.length }}</strong>
            </div>

            <button class="btn-primary" (click)="exportarExcel()">
              Descargar Excel
            </button>
          </div>
        </aside>

      </div>

    </div>
  `
})
export class ReportesComponent implements OnInit {
  filtro = 'todos';

  reporteCaja: any;
  reporteDeudas: any[] = [];
  deudas = [
    { cliente: 'Luis', estado: 'moroso' },
    { cliente: 'Ana', estado: 'al dia' }
  ];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getReporteCaja().subscribe((res: any) => {
      this.reporteCaja = res;
    });

    this.api.getReporteDeudasSocio().subscribe((res: any[]) => {
      this.reporteDeudas = res;
    });
  }

  totalRecaudado() {
    return this.reporteCaja?.totalRecaudado || 0;
  }

  pagosProcesados() {
    return this.reporteCaja?.cantidadPagos || 0;
  }

  totalPendiente() {
    return this.reporteDeudas
        .reduce((total, row) => total + Number(row[2] || 0), 0)
        .toFixed(2);
  }

  exportarExcel() {
    window.open('http://localhost:8080/api/reportes/deudas/socio/export/excel', '_blank');
  }
  filtrados() {
    if (this.filtro === 'todos') return this.deudas;
    return this.deudas.filter(d => d.estado === this.filtro);
  }
}
