import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from '../../environments/environment';
import { Socio, ConceptoDeuda, Deuda, Pago, Puesto, ApiResponse } from '../models/models';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private baseUrl = environment.apiUrl + '/api';

  constructor(private http: HttpClient) { }

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'Error desconocido';
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      if (error.error && error.error.message) {
         errorMessage = error.error.message;
      } else {
         errorMessage = `Código de error: ${error.status}\nMensaje: ${error.message}`;
      }
    }
    console.error(errorMessage);
    return throwError(() => new Error(errorMessage));
  }

  // SOCIOS
  getSocios(): Observable<ApiResponse<Socio[]>> {
    return this.http.get<ApiResponse<Socio[]>>(`${this.baseUrl}/socios`)
      .pipe(catchError(this.handleError));
  }
  getSocio(id: number): Observable<ApiResponse<Socio>> {
    return this.http.get<ApiResponse<Socio>>(`${this.baseUrl}/socios/${id}`)
      .pipe(catchError(this.handleError));
  }
  createSocio(data: Socio): Observable<ApiResponse<Socio>> {
    return this.http.post<ApiResponse<Socio>>(`${this.baseUrl}/socios`, data)
      .pipe(catchError(this.handleError));
  }
  updateSocio(id: number, data: Socio): Observable<ApiResponse<Socio>> {
    return this.http.put<ApiResponse<Socio>>(`${this.baseUrl}/socios/${id}`, data)
      .pipe(catchError(this.handleError));
  }
  deleteSocio(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/socios/${id}`)
      .pipe(catchError(this.handleError));
  }

  // CONCEPTOS DE DEUDA
  getConceptos(): Observable<ApiResponse<ConceptoDeuda[]>> {
    return this.http.get<ApiResponse<ConceptoDeuda[]>>(`${this.baseUrl}/conceptos`)
      .pipe(catchError(this.handleError));
  }
  createConcepto(data: ConceptoDeuda): Observable<ApiResponse<ConceptoDeuda>> {
    return this.http.post<ApiResponse<ConceptoDeuda>>(`${this.baseUrl}/conceptos`, data)
      .pipe(catchError(this.handleError));
  }
  updateConcepto(id: number, data: ConceptoDeuda): Observable<ApiResponse<ConceptoDeuda>> {
    return this.http.put<ApiResponse<ConceptoDeuda>>(`${this.baseUrl}/conceptos/${id}`, data)
      .pipe(catchError(this.handleError));
  }
  deleteConcepto(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/conceptos/${id}`)
      .pipe(catchError(this.handleError));
  }

  // DEUDAS
  getDeudas(): Observable<ApiResponse<Deuda[]>> {
    return this.http.get<ApiResponse<Deuda[]>>(`${this.baseUrl}/deudas`)
      .pipe(catchError(this.handleError));
  }
  createDeuda(data: Deuda): Observable<ApiResponse<Deuda>> {
    return this.http.post<ApiResponse<Deuda>>(`${this.baseUrl}/deudas/individual`, data)
      .pipe(catchError(this.handleError));
  }
  deleteDeuda(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/deudas/${id}`)
      .pipe(catchError(this.handleError));
  }

  // PAGOS
  getPagos(): Observable<ApiResponse<Pago[]>> {
    return this.http.get<ApiResponse<Pago[]>>(`${this.baseUrl}/pagos`)
      .pipe(catchError(this.handleError));
  }
  createPago(data: Pago): Observable<ApiResponse<Pago>> {
    return this.http.post<ApiResponse<Pago>>(`${this.baseUrl}/pagos`, data)
      .pipe(catchError(this.handleError));
  }

  // PUESTOS
  getPuestos(): Observable<ApiResponse<Puesto[]>> {
    return this.http.get<ApiResponse<Puesto[]>>(`${this.baseUrl}/puestos`)
      .pipe(catchError(this.handleError));
  }
  createPuesto(data: Puesto): Observable<ApiResponse<Puesto>> {
    return this.http.post<ApiResponse<Puesto>>(`${this.baseUrl}/puestos`, data)
      .pipe(catchError(this.handleError));
  }
  updatePuesto(id: number, data: Puesto): Observable<ApiResponse<Puesto>> {
    return this.http.put<ApiResponse<Puesto>>(`${this.baseUrl}/puestos/${id}`, data)
      .pipe(catchError(this.handleError));
  }
  deletePuesto(id: number): Observable<ApiResponse<void>> {
    return this.http.delete<ApiResponse<void>>(`${this.baseUrl}/puestos/${id}`)
      .pipe(catchError(this.handleError));
  }
}
