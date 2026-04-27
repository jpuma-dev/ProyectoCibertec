import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-reportes',
  standalone: true,
  imports: [CommonModule],
  template: `
    <h2>Reportes del Sistema</h2>
    
    <div *ngIf="reporteCaja" class="card" style="background: #f0f9ff; border-color: #bae6fd;">
      <h3 style="margin-top:0">Flujo de Caja (Hoy)</h3>
      <div style="display: flex; gap: 2rem;">
        <div>
          <label>Total Recaudado</label>
          <span style="font-size: 1.5rem; font-weight: bold; color: #0369a1;">S/ {{ reporteCaja.totalRecaudado }}</span>
        </div>
        <div>
          <label>Pagos Procesados</label>
          <span style="font-size: 1.5rem; font-weight: bold; color: #0369a1;">{{ reporteCaja.cantidadPagos }}</span>
        </div>
      </div>
    </div>

    <div style="display: flex; justify-content: space-between; align-items: center; margin-top: 2rem;">
      <h3>Deudas Pendientes por Socio</h3>
      <button class="btn btn-primary" (click)="exportarExcel()">
        Descargar Excel
      </button>
    </div>
    
    <table>
      <thead>
        <tr>
          <th>Socio</th>
          <th>DNI</th>
          <th style="text-align: right;">Total Pendiente</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let row of reporteDeudas">
          <td><strong>{{ row[0] }}</strong></td>
          <td>{{ row[1] }}</td>
          <td style="text-align: right; color: #ef4444; font-weight: 500;">S/ {{ row[2] }}</td>
        </tr>
      </tbody>
    </table>
  `
})
export class ReportesComponent implements OnInit {
  reporteCaja: any;
  reporteDeudas: any[] = [];

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.api.getReporteCaja().subscribe((res: any) => this.reporteCaja = res);
    this.api.getReporteDeudasSocio().subscribe((res: any[]) => this.reporteDeudas = res);
  }

  exportarExcel() {
    window.open('http://localhost:8080/api/reportes/deudas/socio/export/excel', '_blank');
  }
}
