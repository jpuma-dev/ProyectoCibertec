🔧 1. Módulo: Deudas (Individual / Masivo)
📁 Estructura recomendada (Frontend Angular)

Crea una carpeta:

src/app/deudas/
Archivos:
deudas.component.ts
deudas.component.html
deudas.service.ts
🧠 Lógica (deudas.component.ts)
import { Component } from '@angular/core';

@Component({
  selector: 'app-deudas',
  templateUrl: './deudas.component.html'
})
export class DeudasComponent {
  modo: 'individual' | 'masivo' = 'individual';

  cambiarModo(tipo: string) {
    this.modo = tipo as any;
  }
}
🎨 Vista (deudas.component.html)
<h2>Gestión de Deudas</h2>

<button (click)="cambiarModo('individual')">Individual</button>
<button (click)="cambiarModo('masivo')">Masivo</button>

<div *ngIf="modo === 'individual'">
  <h3>Registro Individual</h3>
  <input placeholder="Cliente">
  <input placeholder="Monto">
  <button>Guardar</button>
</div>

<div *ngIf="modo === 'masivo'">
  <h3>Carga Masiva</h3>
  <input type="file">
  <button>Subir Archivo</button>
</div>
🔌 Servicio (deudas.service.ts)

(Si tienes backend en Java, aquí conectas API)

import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class DeudasService {

  guardarIndividual(data: any) {
    console.log("Guardando deuda individual", data);
  }

  cargaMasiva(file: File) {
    console.log("Procesando archivo masivo", file);
  }
}
