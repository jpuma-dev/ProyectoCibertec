package com.cibertec.proyecto.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PagoResponseDTO {
    private Long id;
    private Double monto;
    private LocalDateTime fecha;
    private String metodoPago;
    private Long deudaId;
    private String conceptoDeuda;
}
