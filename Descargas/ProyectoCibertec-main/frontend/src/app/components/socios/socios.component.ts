import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ApiService } from '../../services/api.service';
import { Socio } from '../../models/models';

@Component({
  selector: 'app-socios',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  template: `
    <div class="container-fluid">
      <div class="d-flex justify-content-between align-items-center mb-4">
        <h2 class="m-0 fw-bold"><i class="fas fa-users text-primary me-2"></i>Gestión de Socios</h2>
        <button class="btn btn-primary" (click)="openModal()"><i class="fas fa-plus me-2"></i>Nuevo Socio</button>
      </div>

      <!-- Feedback Messages -->
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
                <th>Apellido</th>
                <th>DNI</th>
                <th>Teléfono</th>
                <th>Email</th>
                <th class="text-end">Acciones</th>
              </tr>
            </thead>
            <tbody>
              <tr *ngIf="isLoading">
                <td colspan="7" class="text-center py-4">
                  <div class="spinner-border text-primary" role="status"></div>
                  <p class="mt-2 text-muted">Cargando socios...</p>
                </td>
              </tr>
              <tr *ngIf="!isLoading && socios.length === 0">
                <td colspan="7" class="text-center py-4 text-muted">No hay socios registrados.</td>
              </tr>
              <tr *ngFor="let socio of socios">
                <td>#{{ socio.id }}</td>
                <td class="fw-medium">{{ socio.nombre }}</td>
                <td>{{ socio.apellido }}</td>
                <td><span class="badge bg-light text-dark border">{{ socio.dni }}</span></td>
                <td>{{ socio.telefono }}</td>
                <td>{{ socio.email }}</td>
                <td class="text-end">
                  <button class="btn btn-sm btn-outline-primary me-2" (click)="editSocio(socio)"><i class="fas fa-edit"></i></button>
                  <button class="btn btn-sm btn-outline-danger" (click)="deleteSocio(socio.id!)"><i class="fas fa-trash"></i></button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- Modal overlay for forms -->
      <div class="modal fade show" tabindex="-1" [style.display]="showModal ? 'block' : 'none'" style="background: rgba(0,0,0,0.5);">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content border-0 shadow-lg">
            <div class="card-header-premium">
              <h5 class="modal-title text-white m-0">{{ isEditMode ? 'Editar Socio' : 'Nuevo Socio' }}</h5>
            </div>
            <div class="modal-body p-4">
              <form [formGroup]="socioForm" (ngSubmit)="saveSocio()">
                <div class="mb-3">
                  <label class="form-label fw-medium">Nombre</label>
                  <input type="text" class="form-control" formControlName="nombre" [ngClass]="{'is-invalid': isInvalid('nombre')}">
                  <div class="invalid-feedback">El nombre es requerido (mín. 3 caracteres).</div>
                </div>
                <div class="mb-3">
                  <label class="form-label fw-medium">Apellido</label>
                  <input type="text" class="form-control" formControlName="apellido" [ngClass]="{'is-invalid': isInvalid('apellido')}">
                  <div class="invalid-feedback">El apellido es requerido (mín. 3 caracteres).</div>
                </div>
                <div class="row">
                  <div class="col-md-6 mb-3">
                    <label class="form-label fw-medium">DNI</label>
                    <input type="text" class="form-control" formControlName="dni" [ngClass]="{'is-invalid': isInvalid('dni')}">
                    <div class="invalid-feedback">DNI inválido (8 dígitos).</div>
                  </div>
                  <div class="col-md-6 mb-3">
                    <label class="form-label fw-medium">Teléfono</label>
                    <input type="text" class="form-control" formControlName="telefono" [ngClass]="{'is-invalid': isInvalid('telefono')}">
                    <div class="invalid-feedback">Teléfono inválido (9 dígitos).</div>
                  </div>
                </div>
                <div class="mb-4">
                  <label class="form-label fw-medium">Email</label>
                  <input type="email" class="form-control" formControlName="email" [ngClass]="{'is-invalid': isInvalid('email')}">
                  <div class="invalid-feedback">Email inválido.</div>
                </div>
                
                <div class="d-flex justify-content-end gap-2">
                  <button type="button" class="btn btn-light border" (click)="closeModal()">Cancelar</button>
                  <button type="submit" class="btn btn-primary" [disabled]="socioForm.invalid || isSaving">
                    <span *ngIf="isSaving" class="spinner-border spinner-border-sm me-2" role="status"></span>
                    {{ isSaving ? 'Guardando...' : 'Guardar Socio' }}
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
export class SociosComponent implements OnInit {
  socios: Socio[] = [];
  isLoading = false;
  isSaving = false;
  showModal = false;
  isEditMode = false;
  currentId: number | null = null;
  
  successMessage = '';
  errorMessage = '';

  socioForm: FormGroup;

  constructor(private apiService: ApiService, private fb: FormBuilder) {
    this.socioForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3)]],
      apellido: ['', [Validators.required, Validators.minLength(3)]],
      dni: ['', [Validators.required, Validators.pattern('^[0-9]{8}$')]],
      telefono: ['', [Validators.required, Validators.pattern('^9[0-9]{8}$')]],
      email: ['', [Validators.required, Validators.email]]
    });
  }

  ngOnInit(): void {
    this.loadSocios();
  }

  loadSocios(): void {
    this.isLoading = true;
    this.apiService.getSocios().subscribe({
      next: (res) => {
        this.socios = res.data;
        this.isLoading = false;
      },
      error: (err) => {
        this.errorMessage = 'Error al cargar socios: ' + err.message;
        this.isLoading = false;
      }
    });
  }

  openModal(): void {
    this.isEditMode = false;
    this.currentId = null;
    this.socioForm.reset();
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
  }

  editSocio(socio: Socio): void {
    this.isEditMode = true;
    this.currentId = socio.id!;
    this.socioForm.patchValue({
      nombre: socio.nombre,
      apellido: socio.apellido,
      dni: socio.dni,
      telefono: socio.telefono,
      email: socio.email
    });
    this.showModal = true;
  }

  deleteSocio(id: number): void {
    if (confirm('¿Está seguro de eliminar este socio?')) {
      this.apiService.deleteSocio(id).subscribe({
        next: () => {
          this.successMessage = 'Socio eliminado correctamente';
          this.loadSocios();
        },
        error: (err) => {
          this.errorMessage = 'Error al eliminar: ' + err.message;
        }
      });
    }
  }

  saveSocio(): void {
    if (this.socioForm.invalid) return;
    
    this.isSaving = true;
    const socioData: Socio = this.socioForm.value;

    if (this.isEditMode && this.currentId) {
      this.apiService.updateSocio(this.currentId, socioData).subscribe({
        next: () => {
          this.successMessage = 'Socio actualizado correctamente';
          this.closeModal();
          this.loadSocios();
          this.isSaving = false;
        },
        error: (err) => {
          this.errorMessage = 'Error al actualizar: ' + err.message;
          this.isSaving = false;
        }
      });
    } else {
      this.apiService.createSocio(socioData).subscribe({
        next: () => {
          this.successMessage = 'Socio creado correctamente';
          this.closeModal();
          this.loadSocios();
          this.isSaving = false;
        },
        error: (err) => {
          this.errorMessage = 'Error al crear: ' + err.message;
          this.isSaving = false;
        }
      });
    }
  }

  isInvalid(field: string): boolean {
    const control = this.socioForm.get(field);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }
}
