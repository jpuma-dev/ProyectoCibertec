import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-socios',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>Gestión de Socios</h2>
    
    <div class="form-group">
      <input [(ngModel)]="nuevoSocio.nombre" placeholder="Nombre">
      <input [(ngModel)]="nuevoSocio.apellido" placeholder="Apellido">
      <!-- * REQUISITO: Validar exactamente 8 dígitos para DNI -->
      <input [(ngModel)]="nuevoSocio.dni" placeholder="DNI (8 dígitos)" maxlength="8">
      <!-- * REQUISITO: Validar exactamente 9 dígitos para teléfono -->
      <input [(ngModel)]="nuevoSocio.telefono" placeholder="Teléfono (9 dígitos)" maxlength="9">
      <input [(ngModel)]="nuevoSocio.email" placeholder="Email (opcional)">
      <button class="btn btn-primary" (click)="guardar()">Registrar Socio</button>
    </div>

    <table>
      <thead>
        <tr>
          <th>Nombre Completo</th>
          <th>DNI</th>
          <th>Teléfono</th>
          <th>Email</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let s of socios">
          <td>{{ s.nombre }} {{ s.apellido }}</td>
          <td>{{ s.dni }}</td>
          <td>{{ s.telefono }}</td>
          <td>{{ s.email || '-' }}</td>
          <td>
            <button class="btn btn-primary" style="padding: 2px 8px; margin-right: 5px;" (click)="editar(s)">Editar</button>
            <button class="btn btn-primary" style="padding: 2px 8px; background: #ef4444;" (click)="eliminar(s.dni)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
  `
})
export class SociosComponent implements OnInit {
  socios: any[] = [];
  nuevoSocio = { nombre: '', apellido: '', dni: '', telefono: '', email: '' };
  editando = false;

  constructor(private api: ApiService) {}

  ngOnInit() { this.cargar(); }

  cargar() { this.api.getSocios().subscribe((res: any[]) => this.socios = res); }

  editar(s: any) {
    this.nuevoSocio = { ...s };
    this.editando = true;
  }

  eliminar(dni: string) {
    if (confirm('¿Estás seguro de eliminar este socio?')) {
      this.api.deleteSocio(dni).subscribe(() => {
        alert('Socio eliminado');
        this.cargar();
      });
    }
  }

  guardar() {
    if (!this.nuevoSocio.dni || this.nuevoSocio.dni.length !== 8) {
      alert('DNI inválido');
      return;
    }

    const operacion = this.editando 
      ? this.api.updateSocio(this.nuevoSocio.dni, this.nuevoSocio)
      : this.api.crearSocio(this.nuevoSocio);

    operacion.subscribe({
      next: () => {
        alert(this.editando ? 'Socio actualizado' : 'Socio registrado');
        this.nuevoSocio = { nombre: '', apellido: '', dni: '', telefono: '', email: '' };
        this.editando = false;
        this.cargar();
      },
      error: (err) => alert('Error: ' + err.error?.message)
    });
  }
}
