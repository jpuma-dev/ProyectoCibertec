import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { ConceptoDeuda } from '../../models/models';

@Component({
  selector: 'app-conceptos',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container-fluid">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="m-0 fw-bold"><i class="fas fa-tags text-primary me-2"></i>Conceptos de Deuda</h2>
        <button class="btn btn-primary" (click)="openModal()"><i class="fas fa-plus me-2"></i>Nuevo Concepto</button>
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
                <th>Nombre</th>
                <th>Descripción</th>
                <th>Recurrente</th>
                <th>Monto Sugerido (S/)</th>
                <th class="text-end">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngIf="isLoading">
                <td colspan="6" class="text-center py-4">
                  <div class="spinner-border text-primary" role="status"></div>
                </td>
              </tr>
              <tr *ngIf="!isLoading && conceptos.length === 0">
                <td colspan="6" class="text-center py-4 text-muted">No hay conceptos registrados.</td>
              </tr>
              <tr *ngFor="let concepto of conceptos">
                <td>#{{ concepto.id }}</td>
                <td class="fw-medium">{{ concepto.nombre }}</td>
                <td>{{ concepto.descripcion }}</td>
                <td>
                  <span class="badge" [ngClass]="concepto.recurrente ? 'bg-success' : 'bg-secondary'">
                    {{ concepto.recurrente ? 'Sí' : 'No' }}
                  </span>
                </td>
                <td>{{ concepto.montoSugerido | number:'1.2-2' }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary me-2" (click)="editConcepto(concepto)"><i class="fas fa-edit"></i></button>
                  <button class="btn btn-sm btn-outline-danger" (click)="deleteConcepto(concepto.id!)"><i class="fas fa-trash"></i></button>
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
              <h5 class="modal-title text-white m-0">{{ isEditMode ? 'Editar Concepto' : 'Nuevo Concepto' }}</h5>
            </div>
            <div class="modal-body p-4">
              <form [formGroup]="conceptoForm" (ngSubmit)="saveConcepto()">
                <div class="mb-3">
                  <label class="form-label fw-medium">Nombre</label>
                  <input type="text" class="form-control" formControlName="nombre" [ngClass]="{'is-invalid': isInvalid('nombre')}">
                  <div class="invalid-feedback">Requerido (mín. 3 chars).</div>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-medium">Descripción</label>
                  <textarea class="form-control" formControlName="descripcion" rows="3"></textarea>
                </div>
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label fw-medium">Monto Sugerido (S/)</label>
                    <input type="number" class="form-control" formControlName="montoSugerido" [ngClass]="{'is-invalid': isInvalid('montoSugerido')}">
                    <div class="invalid-feedback">Debe ser mayor a 0.</div>
                  </div>
                  <div class="col-md-6 mb-3 d-flex align-items-end">
                    <div class="form-check form-switch mb-2">
                      <input class="form-check-input" type="checkbox" role="switch" formControlName="recurrente" id="recurrenteSwitch">
                      <label class="form-check-label ms-2" for="recurrenteSwitch">Es recurrente</label>
                    </div>
                  </div>
                </div>
                
                <div class="d-flex justify-content-end gap-2">
                  <button type="button" class="btn btn-light border" (click)="closeModal()">Cancelar</button>
                  <button type="submit" class="btn btn-primary" [disabled]="conceptoForm.invalid || isSaving">
                    <span *ngIf="isSaving" class="spinner-border spinner-border-sm me-2" role="status"></span>
                    Guardar
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
export class ConceptosComponent implements OnInit {
  conceptos: ConceptoDeuda[] = [];
  isLoading = false;
  isSaving = false;
  showModal = false;
  isEditMode = false;
  currentId: number | null = null;
  successMessage = '';
  errorMessage = '';

  conceptoForm: FormGroup;

  constructor(private apiService: ApiService, private fb: FormBuilder) {
    this.conceptoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      descripcion: [''],
      recurrente: [false],
      montoSugerido: [0, [Validators.required, Validators.min(0.1)]]
    });
  }

  ngOnInit(): void {
    this.loadConceptos();
  }

  loadConceptos(): void {
    this.isLoading = true;
    this.apiService.getConceptos().subscribe({
      next: (res) => {
        this.conceptos = res.data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar conceptos: ' + err.message;
        this.isLoading = false;
      }
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.currentId = null;
    this.conceptoForm.reset({ recurrente: false, montoSugerido: 0 });
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  editConcepto(concepto: ConceptoDeuda): void {
    this.isEditMode = true;
    this.currentId = concepto.id!;
    this.conceptoForm.patchValue({
      nombre: concepto.nombre,
      descripcion: concepto.descripcion,
      recurrente: concepto.recurrente,
      montoSugerido: concepto.montoSugerido
    });
    this.showModal = true;
  }

  deleteConcepto(id: number): void {
    if (confirm('¿Está seguro?')) {
      this.apiService.deleteConcepto(id).subscribe({
        next: () => {
          this.successMessage = 'Eliminado correctamente';
          this.loadConceptos();
        },
        error: (err) => this.errorMessage = 'Error: ' + err.message
      });
    }
  }

  saveConcepto(): void {
    if (this.conceptoForm.invalid) return;
    this.isSaving = true;
    const data: ConceptoDeuda = this.conceptoForm.value;

    if (this.isEditMode && this.currentId) {
      this.apiService.updateConcepto(this.currentId, data).subscribe({
        next: () => {
          this.successMessage = 'Actualizado correctamente';
          this.closeModal();
          this.loadConceptos();
          this.isSaving = false;
        },
        error: (err) => {
          this.errorMessage = 'Error: ' + err.message;
          this.isSaving = false;
        }
      });
    } else {
      this.apiService.createConcepto(data).subscribe({
        next: () => {
          this.successMessage = 'Creado correctamente';
          this.closeModal();
          this.loadConceptos();
          this.isSaving = false;
        },
        error: (err) => {
          this.errorMessage = 'Error: ' + err.message;
          this.isSaving = false;
        }
      });
    }
  }

  isInvalid(field: string): boolean {
    const control = this.conceptoForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
