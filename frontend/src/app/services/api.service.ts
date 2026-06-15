import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private url = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  // =========================
  // SOCIOS
  // =========================

  getSocios(): Observable<any[]> {
    return this.http.get<any>(`${this.url}/socios`)
        .pipe(map(res => res.data));
  }

  crearSocio(socio: any): Observable<any> {
    return this.http.post<any>(`${this.url}/socios`, socio)
        .pipe(map(res => res.data));
  }

  updateSocio(dni: string, socio: any): Observable<any> {
    return this.http.put<any>(`${this.url}/socios/${dni}`, socio)
        .pipe(map(res => res.data));
  }

  deleteSocio(dni: string): Observable<any> {
    return this.http.delete<any>(`${this.url}/socios/${dni}`)
        .pipe(map(res => res.data));
  }

  // =========================
  // PUESTOS
  // =========================

  getPuestos(): Observable<any[]> {
    return this.http.get<any>(`${this.url}/puestos`)
        .pipe(map(res => res.data));
  }

  crearPuesto(puesto: any): Observable<any> {
    return this.http.post<any>(`${this.url}/puestos`, puesto)
        .pipe(map(res => res.data));
  }

  updatePuesto(id: number, puesto: any): Observable<any> {
    return this.http.put<any>(`${this.url}/puestos/${id}`, puesto)
        .pipe(map(res => res.data));
  }

  deletePuesto(id: number): Observable<any> {
    return this.http.delete<any>(`${this.url}/puestos/${id}`)
        .pipe(map(res => res.data));
  }

  // =========================
  // CONCEPTOS DE DEUDA / COBRO
  // Ruta backend usada: /api/conceptos-deuda
  // =========================

  getConceptos(): Observable<any[]> {
    return this.http.get<any>(`${this.url}/conceptos-deuda`)
        .pipe(map(res => res.data));
  }

  crearConcepto(concepto: any): Observable<any> {
    return this.http.post<any>(`${this.url}/conceptos-deuda`, concepto)
        .pipe(map(res => res.data));
  }

  updateConcepto(id: number, concepto: any): Observable<any> {
    return this.http.put<any>(`${this.url}/conceptos-deuda/${id}`, concepto)
        .pipe(map(res => res.data));
  }

  deleteConcepto(id: number): Observable<any> {
    return this.http.delete<any>(`${this.url}/conceptos-deuda/${id}`)
        .pipe(map(res => res.data));
  }

  // =========================
  // DEUDAS
  // =========================

  getDeudas(): Observable<any[]> {
    return this.http.get<any>(`${this.url}/deudas`)
        .pipe(map(res => res.data));
  }

  generarDeudaMasiva(data: any): Observable<any> {
    return this.http.post<any>(`${this.url}/deudas/generar-masiva`, data)
        .pipe(map(res => res.data));
  }

  crearDeuda(data: any): Observable<any> {
    return this.http.post<any>(`${this.url}/deudas`, data)
        .pipe(map(res => res.data));
  }

  // =========================
  // PAGOS / COBRANZA
  // =========================

  registrarPago(pago: any): Observable<any> {
    return this.http.post<any>(`${this.url}/pagos`, pago)
        .pipe(map(res => res.data));
  }

  // =========================
  // REPORTES
  // =========================

  getReporteCaja(): Observable<any> {
    return this.http.get<any>(`${this.url}/reportes/flujo-caja-diario`)
        .pipe(map(res => res.data));
  }

  getReporteDeudasSocio(): Observable<any[]> {
    return this.http.get<any>(`${this.url}/reportes/deudas/socio`)
        .pipe(map(res => res.data));
  }
}