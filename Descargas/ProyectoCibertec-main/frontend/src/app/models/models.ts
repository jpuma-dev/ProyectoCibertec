export interface Socio {
  id?: number;
  nombre: string;
  apellido: string;
  dni: string;
  telefono: string;
  email: string;
}

export interface ConceptoDeuda {
  id?: number;
  nombre: string;
  descripcion: string;
  recurrente: boolean;
  montoSugerido: number;
}

export interface Deuda {
  id?: number;
  conceptoId: number;
  conceptoNombre?: string;
  monto: number;
  fecha: string; // ISO date string
  estado?: string;
  puestoIds?: number[];
  puestoNumeros?: string[];
}

export interface Pago {
  id?: number;
  deudaId: number;
  monto: number;
  fecha?: string; // ISO date string
  metodoPago: string;
  conceptoDeuda?: string;
}

export interface Puesto {
  id?: number;
  numero: string;
  descripcion: string;
  socioId?: number;
  socioNombreCompleto?: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: string;
}
