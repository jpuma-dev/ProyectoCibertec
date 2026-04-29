import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ApiService } from './services/api.service';

@Component({
  selector: 'app-puestos',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <h2>Gestión de Puestos</h2>
    
    <div class="form-group">
      <!-- * REQUISITO: Mostrar instrucción clara de cómo registrar el puesto -->
      <p style="color: #666; font-size: 0.9rem;">Formato requerido: Letras y Números de 2 a 5 caracteres (Ejm: A01, B02, 101)</p>
      
      <input [(ngModel)]="nuevoPuesto.numero" placeholder="Nro de Puesto (ejm: A01)" (keyup.enter)="guardar()">
      <input [(ngModel)]="nuevoPuesto.descripcion" placeholder="Descripción (opcional)">
      
      <select [(ngModel)]="nuevoPuesto.socioId">
        <option [value]="null">Dueño: Asociación</option>
        <option *ngFor="let s of socios" [value]="s.id">{{ s.nombre }} {{ s.apellido }}</option>
      </select>
      
      <button class="btn btn-primary" (click)="guardar()">Registrar Puesto</button>
    </div>

    <table>
      <thead>
        <tr>
          <th>Número</th>
          <th>Descripción</th>
          <th>Socio / Dueño</th>
          <th>Acciones</th>
        </tr>
      </thead>
      <tbody>
        <tr *ngFor="let p of puestos">
          <td>{{ p.numero }}</td>
          <td>{{ p.descripcion || '-' }}</td>
          <td>{{ p.socioNombreCompleto || 'Asociación' }}</td>
          <td>
            <button class="btn btn-primary" style="padding: 2px 8px; margin-right: 5px;" (click)="editar(p)">Editar</button>
            <button class="btn btn-primary" style="padding: 2px 8px; background: #ef4444;" (click)="eliminar(p.id)">Eliminar</button>
          </td>
        </tr>
      </tbody>
    </table>
  `
})
export class PuestosComponent implements OnInit {
  puestos: any[] = [];
  socios: any[] = [];
  nuevoPuesto: any = { numero: '', descripcion: '', socioId: null };
  editando = false;

  constructor(private api: ApiService) {}

  ngOnInit() { 
    this.cargar();
    this.api.getSocios().subscribe((res: any[]) => this.socios = res);
  }

  cargar() { this.api.getPuestos().subscribe((res: any[]) => this.puestos = res); }

  editar(p: any) {
    this.nuevoPuesto = { ...p };
    // Mapear socioNombreCompleto de vuelta al ID si es necesario
    const socio = this.socios.find(s => (s.nombre + ' ' + s.apellido) === p.socioNombreCompleto);
    this.nuevoPuesto.socioId = socio ? socio.id : null;
    this.editando = true;
  }

  eliminar(id: number) {
    if (confirm('¿Desea eliminar este puesto?')) {
      this.api.deletePuesto(id).subscribe({
        next: () => {
          alert('Puesto eliminado');
          this.cargar();
        },
        error: (err) => alert('Error: ' + err.error?.message)
      });
    }
  }

  guardar() {
    if (this.nuevoPuesto.numero) {
      this.nuevoPuesto.numero = this.nuevoPuesto.numero.trim().toUpperCase();
    }

    const operacion = this.editando 
      ? this.api.updatePuesto(this.nuevoPuesto.id, this.nuevoPuesto)
      : this.api.crearPuesto(this.nuevoPuesto);

    operacion.subscribe({
      next: () => {
        alert(this.editando ? 'Puesto actualizado' : 'Puesto registrado');
        this.nuevoPuesto = { numero: '', descripcion: '', socioId: null };
        this.editando = false;
        this.cargar();
      },
      error: (err: any) => alert('Error: ' + (err.error?.message || 'Error en operación'))
    });
  }
}
