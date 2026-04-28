import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, map, tap } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private url = 'http://localhost:8080/api';

  // Credenciales guardadas para HTTP Basic Auth
  private credenciales: string = '';

  constructor(private http: HttpClient) {}

  // ── AUTH ────────────────────────────────────────────────

  /**
   * Login: envía credenciales al backend.
   * El backend verifica username y password (cifrado con BCrypt) en la BD.
   * Guarda las credenciales en Base64 para HTTP Basic Auth.
   */
  login(username: string, password: string): Observable<any> {
    const body = { username, password };
    return this.http.post<any>(`${this.url}/auth/login`, body).pipe(
      tap(() => {
        // Guardar credenciales para las siguientes peticiones (HTTP Basic)
        this.credenciales = btoa(`${username}:${password}`);
        localStorage.setItem('auth', this.credenciales);
        localStorage.setItem('username', username);
      }),
      map(res => res.data)
    );
  }

  register(username: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.url}/auth/register`, { username, password })
      .pipe(map(res => res.data));
  }

  logout(): void {
    this.credenciales = '';
    localStorage.removeItem('auth');
    localStorage.removeItem('username');
  }

  estaAutenticado(): boolean {
    return !!localStorage.getItem('auth');
  }

  getUsername(): string {
    return localStorage.getItem('username') || '';
  }

  // Cabeceras con autenticación Basic
  private getHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth') || this.credenciales;
    return new HttpHeaders({ Authorization: `Basic ${token}` });
  }

  // ── SOCIOS ──────────────────────────────────────────────

  getSocios(): Observable<any[]> {
    return this.http.get<any>(`${this.url}/socios`, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  crearSocio(socio: any): Observable<any> {
    return this.http.post<any>(`${this.url}/socios`, socio, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  updateSocio(dni: string, socio: any): Observable<any> {
    return this.http.put<any>(`${this.url}/socios/${dni}`, socio, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  deleteSocio(dni: string): Observable<any> {
    return this.http.delete<any>(`${this.url}/socios/${dni}`, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  // ── PUESTOS ─────────────────────────────────────────────

  getPuestos(): Observable<any[]> {
    return this.http.get<any>(`${this.url}/puestos`, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  crearPuesto(puesto: any): Observable<any> {
    return this.http.post<any>(`${this.url}/puestos`, puesto, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  updatePuesto(id: number, puesto: any): Observable<any> {
    return this.http.put<any>(`${this.url}/puestos/${id}`, puesto, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  deletePuesto(id: number): Observable<any> {
    return this.http.delete<any>(`${this.url}/puestos/${id}`, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  // ── CONCEPTOS ───────────────────────────────────────────

  getConceptos(): Observable<any[]> {
    return this.http.get<any>(`${this.url}/conceptos-deuda`, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  // ── DEUDAS ──────────────────────────────────────────────

  getDeudas(): Observable<any[]> {
    return this.http.get<any>(`${this.url}/deudas`, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  generarDeudaMasiva(data: any): Observable<any> {
    return this.http.post<any>(`${this.url}/deudas/generar-masiva`, data, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  // ── PAGOS ───────────────────────────────────────────────

  registrarPago(pago: any): Observable<any> {
    return this.http.post<any>(`${this.url}/pagos`, pago, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  // ── REPORTES ────────────────────────────────────────────

  getReporteCaja(): Observable<any> {
    return this.http.get<any>(`${this.url}/pagos/flujo-caja-diario`, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  getReporteDeudasSocio(): Observable<any[]> {
    return this.http.get<any>(`${this.url}/reportes/deudas/socio`, { headers: this.getHeaders() })
      .pipe(map(res => res.data));
  }

  exportarExcel(): void {
    const token = localStorage.getItem('auth') || '';
    window.open(
      `${this.url}/reportes/deudas/socio/export/excel`,
      '_blank'
    );
  }
}
