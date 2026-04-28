import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-cobranza',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>💰 Módulo de Cobranza</h2>
    
    <div class="form-group" *ngIf="deudasPendientes.length > 0">
      <label>Seleccionar Deuda a Pagar:</label>
      <select [(ngModel)]="pago.deudaId">
        <option *ngFor="let d of deudasPendientes" [value]="d.id">
          Puesto: {{ d.puestoNumeros.join(', ') }} - {{ d.conceptoNombre }} - S/ {{ d.monto }}
        </option>
      </select>

      <label>Método de Pago:</label>
      <select [(ngModel)]="pago.metodoPago">
        <option value="EFECTIVO">Efectivo</option>
        <option value="YAPE">Yape / Plin</option>
        <option value="TRANSFERENCIA">Transferencia</option>
        <option value="TARJETA">Tarjeta</option>
      </select>

      <button class="btn btn-success" (click)="pagar()">Registrar Pago</button>
    </div>

    <div *ngIf="deudasPendientes.length === 0">
      <p>✅ No hay deudas pendientes por cobrar.</p>
    </div>

    <hr>
    <h3>Historial de Deudas</h3>
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
        <tr *ngFor="let d of allDeudas">
          <td>{{ d.puestoNumeros.join(', ') }}</td>
          <td>{{ d.conceptoNombre }}</td>
          <td>S/ {{ d.monto }}</td>
          <td>
            <span [class.text-success]="d.estado === 'PAGADO'" [class.text-danger]="d.estado === 'PENDIENTE'">
              {{ d.estado }}
            </span>
          </td>
        </tr>
      </tbody>
    </table>
  `
})
export class CobranzaComponent implements OnInit {
  allDeudas: any[] = [];
  deudasPendientes: any[] = [];
  pago = { deudaId: null, metodoPago: 'EFECTIVO', monto: 0 };

  constructor(private api: ApiService) {}

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.api.getDeudas().subscribe(res => {
      this.allDeudas = res;
      this.deudasPendientes = res.filter(d => d.estado === 'PENDIENTE');
      if (this.deudasPendientes.length > 0) {
        this.pago.deudaId = this.deudasPendientes[0].id;
      }
    });
  }

  pagar() {
    const deuda = this.deudasPendientes.find(d => d.id == this.pago.deudaId);
    if (!deuda) return;

    this.pago.monto = deuda.monto;
    this.api.registrarPago(this.pago).subscribe({
      next: () => {
        alert('Pago registrado con éxito');
        this.cargar();
      },
      error: (err) => alert('Error: ' + (err.error?.message || 'No se pudo procesar el pago'))
    });
  }
}
