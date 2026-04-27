import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-deudas',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>📄 Generación Masiva de Deudas</h2>
    <p style="color: var(--text-muted); margin-bottom: 2rem;">Asigna cobros masivos a los socios de forma proporcional o individual.</p>
    
    <div style="max-width: 600px;">
      <div class="form-group">
        <label>Tipo de distribución</label>
        <select [(ngModel)]="tipoCobro">
          <option value="INDIVIDUAL">Monto fijo por cada puesto</option>
          <option value="TOTAL">Dividir monto total entre puestos</option>
        </select>
      </div>

      <div class="form-group" *ngIf="tipoCobro === 'INDIVIDUAL'">
        <label>Monto por puesto (S/)</label>
        <input type="number" [(ngModel)]="data.monto" placeholder="0.00">
      </div>

      <div class="form-group" *ngIf="tipoCobro === 'TOTAL'">
        <label>Monto TOTAL a repartir (S/)</label>
        <input type="number" [(ngModel)]="data.montoTotal" placeholder="0.00">
      </div>
      
      <div class="form-group">
        <label>Concepto de Deuda</label>
        <select [(ngModel)]="data.conceptoId">
          <option [ngValue]="null" disabled>Seleccione un concepto...</option>
          <option *ngFor="let c of conceptos" [value]="c.id">{{ c.nombre }}</option>
        </select>
      </div>

      <div class="form-group">
        <label>Último día de pago</label>
        <input type="date" [(ngModel)]="data.fecha" [min]="hoy" style="width: 100%; padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
      </div>
      
      <div class="form-group">
        <label>Puestos específicos (Opcional)</label>
        <input [(ngModel)]="puestoIdsStr" placeholder="Ejm: 1, 5, 12 (dejar vacío para TODOS)">
      </div>
      
      <div style="margin-top: 2rem;">
        <button class="btn btn-primary" (click)="generar()" style="width: 100%; font-size: 1.1rem;">
          Generar Deudas
        </button>
      </div>
    </div>
  `
})
export class DeudasComponent implements OnInit {
  tipoCobro = 'INDIVIDUAL';
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
    this.api.getConceptos().subscribe(res => this.conceptos = res);
  }

  generar() {
    if (this.puestoIdsStr.trim()) {
      this.data.puestoIds = this.puestoIdsStr.split(',').map(id => parseInt(id.trim())).filter(id => !isNaN(id));
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
        this.puestoIdsStr = '';
        this.data.monto = 0;
        this.data.montoTotal = 0;
      },
      error: (err) => alert('Error: ' + (err.error?.message || 'No se pudo generar las deudas'))
    });
  }
}
