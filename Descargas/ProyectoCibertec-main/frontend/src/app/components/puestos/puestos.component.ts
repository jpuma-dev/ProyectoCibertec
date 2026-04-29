import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Puesto, Socio } from '../../models/models';

@Component({
  selector: 'app-puestos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container-fluid">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="m-0 fw-bold"><i class="fas fa-store-alt text-primary me-2"></i>Gestión de Puestos</h2>
        <button class="btn btn-primary" (click)="openModal()"><i class="fas fa-plus me-2"></i>Nuevo Puesto</button>
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
        <div class="row mb-4">
          <div class="col-md-4" *ngFor="let puesto of puestos">
            <div class="card shadow-sm h-100 border-0" style="border-radius: 12px; background: #fff;">
              <div class="card-body">
                <div class="d-flex justify-content-between align-items-center mb-3">
                  <h4 class="card-title fw-bold m-0 text-primary">Puesto {{ puesto.numero }}</h4>
                  <span class="badge" [ngClass]="puesto.socioNombreCompleto ? 'bg-success' : 'bg-warning text-dark'">
                    {{ puesto.socioNombreCompleto ? 'Ocupado' : 'Disponible' }}
                  </span>
                </div>
                <p class="card-text text-muted mb-3">{{ puesto.descripcion || 'Sin descripción' }}</p>
                <div class="d-flex align-items-center bg-light p-2 rounded mb-3">
                  <i class="fas fa-user-circle fs-4 text-secondary me-2"></i>
                  <span class="fw-medium text-dark">{{ puesto.socioNombreCompleto || 'Ningún socio asignado' }}</span>
                </div>
                <div class="d-flex justify-content-end gap-2 border-top pt-3">
                  <button class="btn btn-sm btn-outline-primary" (click)="editPuesto(puesto)">Editar</button>
                  <button class="btn btn-sm btn-outline-danger" (click)="deletePuesto(puesto.id!)">Eliminar</button>
                </div>
              </div>
            </div>
          </div>
          <div class="col-12" *ngIf="!isLoading && puestos.length === 0">
            <div class="text-center py-5 text-muted">No hay puestos registrados.</div>
          </div>
          <div class="col-12" *ngIf="isLoading">
            <div class="text-center py-5"><div class="spinner-border text-primary" role="status"></div></div>
          </div>
        </div>
      </div>

      <!-- Modal overlay -->
      <div class="modal fade show" tabindex="-1" [style.display]="showModal ? 'block' : 'none'" style="background: rgba(0,0,0,0.5);">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content border-0 shadow-lg">
            <div class="card-header-premium">
              <h5 class="modal-title text-white m-0">{{ isEditMode ? 'Editar Puesto' : 'Nuevo Puesto' }}</h5>
            </div>
            <div class="modal-body p-4">
              <form [formGroup]="puestoForm" (ngSubmit)="savePuesto()">
                <div class="mb-3">
                  <label class="form-label fw-medium">Número de Puesto</label>
                  <input type="text" class="form-control" formControlName="numero" [ngClass]="{'is-invalid': isInvalid('numero')}">
                  <div class="invalid-feedback">El número es requerido.</div>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-medium">Descripción</label>
                  <textarea class="form-control" formControlName="descripcion" rows="3"></textarea>
                </div>
                <div class="mb-4">
                  <label class="form-label fw-medium">Socio Asignado (Opcional)</label>
                  <select class="form-select" formControlName="socioId">
                    <option [ngValue]="null">-- Sin asignar --</option>
                    <option *ngFor="let s of socios" [ngValue]="s.id">{{ s.nombre }} {{ s.apellido }} - {{ s.dni }}</option>
                  </select>
                </div>
                
                <div class="d-flex justify-content-end gap-2">
                  <button type="button" class="btn btn-light border" (click)="closeModal()">Cancelar</button>
                  <button type="submit" class="btn btn-primary" [disabled]="puestoForm.invalid || isSaving">
                    <span *ngIf="isSaving" class="spinner-border spinner-border-sm me-2" role="status"></span>
                    Guardar Puesto
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
export class PuestosComponent implements OnInit {
  puestos: Puesto[] = [];
  socios: Socio[] = [];
  
  isLoading = false;
  isSaving = false;
  showModal = false;
  isEditMode = false;
  currentId: number | null = null;
  successMessage = '';
  errorMessage = '';

  puestoForm: FormGroup;

  constructor(private apiService: ApiService, private fb: FormBuilder) {
    this.puestoForm = this.fb.group({
      numero: ['', Validators.required],
      descripcion: [''],
      socioId: [null]
    });
  }

  ngOnInit(): void {
    this.loadPuestos();
    this.apiService.getSocios().subscribe(res => this.socios = res.data);
  }

  loadPuestos(): void {
    this.isLoading = true;
    this.apiService.getPuestos().subscribe({
      next: (res) => { this.puestos = res.data; this.isLoading = false; },
      error: (err) => { this.errorMessage = 'Error: ' + err.message; this.isLoading = false; }
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.currentId = null;
    this.puestoForm.reset({ socioId: null });
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  editPuesto(puesto: Puesto): void {
    this.isEditMode = true;
    this.currentId = puesto.id!;
    this.puestoForm.patchValue({
      numero: puesto.numero,
      descripcion: puesto.descripcion,
      socioId: puesto.socioId || null
    });
    this.showModal = true;
  }

  deletePuesto(id: number): void {
    if (confirm('¿Eliminar este puesto?')) {
      this.apiService.deletePuesto(id).subscribe({
        next: () => { this.successMessage = 'Eliminado correctamente'; this.loadPuestos(); },
        error: (err) => this.errorMessage = 'Error: ' + err.message
      });
    }
  }

  savePuesto(): void {
    if (this.puestoForm.invalid) return;
    this.isSaving = true;
    const data: Puesto = this.puestoForm.value;

    if (this.isEditMode && this.currentId) {
      this.apiService.updatePuesto(this.currentId, data).subscribe({
        next: () => {
          this.successMessage = 'Actualizado correctamente';
          this.closeModal();
          this.loadPuestos();
          this.isSaving = false;
        },
        error: (err) => { this.errorMessage = 'Error: ' + err.message; this.isSaving = false; }
      });
    } else {
      this.apiService.createPuesto(data).subscribe({
        next: () => {
          this.successMessage = 'Creado correctamente';
          this.closeModal();
          this.loadPuestos();
          this.isSaving = false;
        },
        error: (err) => { this.errorMessage = 'Error: ' + err.message; this.isSaving = false; }
      });
    }
  }

  isInvalid(field: string): boolean {
    const c = this.puestoForm.get(field);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }
}
