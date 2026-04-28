package com.cibertec.proyecto.dtos;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoResponseDTO {
    private Long id;
    private Double monto;
    private LocalDateTime fecha;
    private String metodoPago;
    private Long deudaId;
    private String conceptoDeuda;
}
