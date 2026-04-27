package com.cibertec.proyecto.dtos;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PuestoResponseDTO {
    private Long id;
    private String numero;
    private String descripcion;
    private Long socioId;
    private String socioNombreCompleto;
}
