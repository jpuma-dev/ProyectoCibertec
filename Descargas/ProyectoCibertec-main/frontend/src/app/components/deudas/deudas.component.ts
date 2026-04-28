import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Deuda, ConceptoDeuda, Puesto } from '../../models/models';

@Component({
  selector: 'app-deudas',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container-fluid">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="m-0 fw-bold"><i class="fas fa-file-invoice-dollar text-primary me-2"></i>Gestión de Deudas</h2>
        <button class="btn btn-primary" (click)="openModal()"><i class="fas fa-plus me-2"></i>Nueva Deuda</button>
      </div>

      <div *ngIf="successMessage" class="alert alert-success alert-dismissible fade show" role="alert">
        {{ successMessage }}
        <button type="button" class="btn-close" (click)="successMessage = ''"></button>
      </div>
      <div *ngIf="errorMessage" class="alert alert-danger alert-dismissible fade show" role="alert">
        {{ errorMessage }}
        <button type="button" class="btn-close" (click)="errorMessage = ''"></button>
      </div>

      <div class="premium-card p-4">
        <div class="table-responsive">
          <table class="table table-hover align-middle">
            <thead class="table-light">
              <tr>
                <th>ID</th>
                <th>Concepto</th>
                <th>Monto</th>
                <th>Fecha</th>
                <th>Estado</th>
                <th>Puestos</th>
                <th class="text-end">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngIf="isLoading">
                <td colspan="7" class="text-center py-4"><div class="spinner-border text-primary" role="status"></div></td>
              </tr>
              <tr *ngIf="!isLoading && deudas.length === 0">
                <td colspan="7" class="text-center py-4 text-muted">No hay deudas registradas.</td>
              </tr>
              <tr *ngFor="let deuda of deudas">
                <td>#{{ deuda.id }}</td>
                <td class="fw-medium">{{ deuda.conceptoNombre }}</td>
                <td class="fw-bold text-danger">S/ {{ deuda.monto | number:'1.2-2' }}</td>
                <td>{{ deuda.fecha | date:'mediumDate' }}</td>
                <td>
                  <span class="badge" [ngClass]="deuda.estado === 'PENDIENTE' ? 'bg-warning text-dark' : 'bg-success'">
                    {{ deuda.estado }}
                  </span>
                </td>
                <td>{{ deuda.puestoNumeros?.join(', ') || 'N/A' }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-danger" (click)="deleteDeuda(deuda.id!)" [disabled]="deuda.estado !== 'PENDIENTE'"><i class="fas fa-trash"></i></button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal overlay -->
      <div class="modal fade show" tabindex="-1" [style.display]="showModal ? 'block' : 'none'" style="background: rgba(0,0,0,0.5);">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content border-0 shadow-lg">
            <div class="card-header-premium">
              <h5 class="modal-title text-white m-0">Nueva Deuda Individual</h5>
            </div>
            <div class="modal-body p-4">
              <form [formGroup]="deudaForm" (ngSubmit)="saveDeuda()">
                <div class="mb-3">
                  <label class="form-label fw-medium">Concepto</label>
                  <select class="form-select" formControlName="conceptoId" [ngClass]="{'is-invalid': isInvalid('conceptoId')}">
                    <option value="">Seleccione un concepto...</option>
                    <option *ngFor="let c of conceptos" [value]="c.id">{{ c.nombre }} (S/ {{c.montoSugerido}})</option>
                  </select>
                </div>
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label fw-medium">Monto (S/)</label>
                    <input type="number" class="form-control" formControlName="monto" [ngClass]="{'is-invalid': isInvalid('monto')}">
                  </div>
                  <div class="col-md-6 mb-3">
                    <label class="form-label fw-medium">Fecha Vencimiento</label>
                    <input type="date" class="form-control" formControlName="fecha" [ngClass]="{'is-invalid': isInvalid('fecha')}">
                  </div>
                </div>
                <div class="mb-4">
                  <label class="form-label fw-medium">Puesto</label>
                  <select class="form-select" formControlName="puestoId" [ngClass]="{'is-invalid': isInvalid('puestoId')}">
                    <option value="">Seleccione un puesto...</option>
                    <option *ngFor="let p of puestos" [value]="p.id">Puesto {{ p.numero }} - {{ p.socioNombreCompleto || 'Sin socio' }}</option>
                  </select>
                </div>
                
                <div class="d-flex justify-content-end gap-2">
                  <button type="button" class="btn btn-light border" (click)="closeModal()">Cancelar</button>
                  <button type="submit" class="btn btn-primary" [disabled]="deudaForm.invalid || isSaving">
                    <span *ngIf="isSaving" class="spinner-border spinner-border-sm me-2" role="status"></span>
                    Crear Deuda
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
export class DeudasComponent implements OnInit {
  deudas: Deuda[] = [];
  conceptos: ConceptoDeuda[] = [];
  puestos: Puesto[] = [];
  
  isLoading = false;
  isSaving = false;
  showModal = false;
  successMessage = '';
  errorMessage = '';

  deudaForm: FormGroup;

  constructor(private apiService: ApiService, private fb: FormBuilder) {
    this.deudaForm = this.fb.group({
      conceptoId: ['', Validators.required],
      monto: [0, [Validators.required, Validators.min(0.1)]],
      fecha: ['', Validators.required],
      puestoId: ['', Validators.required]
    });
    
    // Auto-fill amount based on concept
    this.deudaForm.get('conceptoId')?.valueChanges.subscribe(conceptId => {
      if (conceptId) {
        const concept = this.conceptos.find(c => c.id == conceptId);
        if (concept && concept.montoSugerido) {
          this.deudaForm.patchValue({ monto: concept.montoSugerido });
        }
      }
    });
  }

  ngOnInit(): void {
    this.loadDeudas();
    this.loadExtras();
  }

  loadDeudas(): void {
    this.isLoading = true;
    this.apiService.getDeudas().subscribe({
      next: (res) => { this.deudas = res.data; this.isLoading = false; },
      error: (err) => { this.errorMessage = 'Error: ' + err.message; this.isLoading = false; }
    });
  }

  loadExtras(): void {
    this.apiService.getConceptos().subscribe(res => this.conceptos = res.data);
    this.apiService.getPuestos().subscribe(res => this.puestos = res.data);
  }

  openModal(): void {
    this.deudaForm.reset({ monto: 0 });
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  deleteDeuda(id: number): void {
    if (confirm('¿Eliminar deuda?')) {
      this.apiService.deleteDeuda(id).subscribe({
        next: () => { this.successMessage = 'Eliminada'; this.loadDeudas(); },
        error: (err) => this.errorMessage = 'Error: ' + err.message
      });
    }
  }

  saveDeuda(): void {
    if (this.deudaForm.invalid) return;
    this.isSaving = true;
    
    const formVal = this.deudaForm.value;
    const data: Deuda = {
      conceptoId: parseInt(formVal.conceptoId),
      monto: formVal.monto,
      fecha: formVal.fecha,
      puestoIds: [parseInt(formVal.puestoId)]
    };

    this.apiService.createDeuda(data).subscribe({
      next: () => {
        this.successMessage = 'Deuda creada correctamente';
        this.closeModal();
        this.loadDeudas();
        this.isSaving = false;
      },
      error: (err) => {
        this.errorMessage = 'Error: ' + err.message;
        this.isSaving = false;
      }
    });
  }

  isInvalid(field: string): boolean {
    const c = this.deudaForm.get(field);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }
}
