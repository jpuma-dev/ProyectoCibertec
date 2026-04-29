import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Pago, Deuda } from '../../models/models';

@Component({
  selector: 'app-pagos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container-fluid">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="m-0 fw-bold"><i class="fas fa-money-bill-wave text-success me-2"></i>Gestión de Pagos</h2>
        <button class="btn btn-success" (click)="openModal()"><i class="fas fa-plus me-2"></i>Registrar Pago</button>
      </div>

      <div *ngIf="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
        {{ successMessage }}
        <button type="button" class="btn-close" (click)="successMessage = ''"></button>
      </div>
      <div *ngIf="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
        {{ errorMessage }}
        <button type="button" class="btn-close" (click)="errorMessage = ''"></button>
      </div>

      <div class="premium-card p-4 border-top border-success border-4">
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead class="table-light">
              <tr>
                <th>ID</th>
                <th>Concepto de Deuda</th>
                <th>Monto (S/)</th>
                <th>Fecha y Hora</th>
                <th>Método de Pago</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngIf="isLoading">
                <td colspan="5" class="text-center py-4"><div class="spinner-border text-success" role="status"></div></td>
              </tr>
              <tr *ngIf="!isLoading && pagos.length === 0">
                <td colspan="5" class="text-center py-4 text-muted">No hay pagos registrados.</td>
              </tr>
              <tr *ngFor="let pago of pagos">
                <td>#{{ pago.id }}</td>
                <td class="fw-medium">{{ pago.conceptoDeuda || 'Deuda #' + pago.deudaId }}</td>
                <td class="fw-bold text-success">+ S/ {{ pago.monto | number:'1.2-2' }}</td>
                <td>{{ pago.fecha | date:'medium' }}</td>
                <td><span class="badge bg-secondary">{{ pago.metodoPago }}</span></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal overlay -->
      <div class="modal fade show" tabindex="-1" [style.display]="showModal ? 'block' : 'none'" style="background: rgba(0,0,0,0.5);">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content border-0 shadow-lg">
            <div class="card-header bg-success text-white">
              <h5 class="modal-title m-0">Registrar Nuevo Pago</h5>
            </div>
            <div class="modal-body p-4">
              <form [formGroup]="pagoForm" (ngSubmit)="savePago()">
                <div class="mb-3">
                  <label class="form-label fw-medium">Deuda a Pagar</label>
                  <select class="form-select" formControlName="deudaId" [ngClass]="{'is-invalid': isInvalid('deudaId')}">
                    <option value="">Seleccione una deuda pendiente...</option>
                    <option *ngFor="let d of deudasPendientes" [value]="d.id">#{{ d.id }} - {{ d.conceptoNombre }} (S/ {{ d.monto }})</option>
                  </select>
                </div>
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label fw-medium">Monto a Pagar (S/)</label>
                    <input type="number" class="form-control" formControlName="monto" [ngClass]="{'is-invalid': isInvalid('monto')}">
                  </div>
                  <div class="col-md-6 mb-4">
                    <label class="form-label fw-medium">Método de Pago</label>
                    <select class="form-select" formControlName="metodoPago" [ngClass]="{'is-invalid': isInvalid('metodoPago')}">
                      <option value="EFECTIVO">Efectivo</option>
                      <option value="TRANSFERENCIA">Transferencia</option>
                      <option value="TARJETA">Tarjeta</option>
                      <option value="YAPE">Yape / Plin</option>
                    </select>
                  </div>
                </div>
                
                <div class="d-flex justify-content-end gap-2">
                  <button type="button" class="btn btn-light border" (click)="closeModal()">Cancelar</button>
                  <button type="submit" class="btn btn-success" [disabled]="pagoForm.invalid || isSaving">
                    <span *ngIf="isSaving" class="spinner-border spinner-border-sm me-2" role="status"></span>
                    Registrar Pago
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </div>
  `
})
export class PagosComponent implements OnInit {
  pagos: Pago[] = [];
  deudasPendientes: Deuda[] = [];
  
  isLoading = false;
  isSaving = false;
  showModal = false;
  successMessage = '';
  errorMessage = '';

  pagoForm: FormGroup;

  constructor(private apiService: ApiService, private fb: FormBuilder) {
    this.pagoForm = this.fb.group({
      deudaId: ['', Validators.required],
      monto: [0, [Validators.required, Validators.min(0.1)]],
      metodoPago: ['EFECTIVO', Validators.required]
    });
    
    // Auto-fill amount based on selected debt
    this.pagoForm.get('deudaId')?.valueChanges.subscribe(deudaId => {
      if (deudaId) {
        const deuda = this.deudasPendientes.find(d => d.id == deudaId);
        if (deuda) {
          this.pagoForm.patchValue({ monto: deuda.monto });
        }
      }
    });
  }

  ngOnInit(): void {
    this.loadPagos();
    this.loadDeudasPendientes();
  }

  loadPagos(): void {
    this.isLoading = true;
    this.apiService.getPagos().subscribe({
      next: (res) => { this.pagos = res.data; this.isLoading = false; },
      error: (err) => { this.errorMessage = 'Error: ' + err.message; this.isLoading = false; }
    });
  }

  loadDeudasPendientes(): void {
    this.apiService.getDeudas().subscribe(res => {
      this.deudasPendientes = res.data.filter(d => d.estado === 'PENDIENTE');
    });
  }

  openModal(): void {
    this.pagoForm.reset({ metodoPago: 'EFECTIVO', monto: 0 });
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  savePago(): void {
    if (this.pagoForm.invalid) return;
    this.isSaving = true;
    
    const data: Pago = {
      deudaId: parseInt(this.pagoForm.value.deudaId),
      monto: this.pagoForm.value.monto,
      metodoPago: this.pagoForm.value.metodoPago
    };

    this.apiService.createPago(data).subscribe({
      next: () => {
        this.successMessage = 'Pago registrado correctamente';
        this.closeModal();
        this.loadPagos();
        this.loadDeudasPendientes();
        this.isSaving = false;
      },
      error: (err) => {
        this.errorMessage = 'Error: ' + err.message;
        this.isSaving = false;
      }
    });
  }

  isInvalid(field: string): boolean {
    const c = this.pagoForm.get(field);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }
}
