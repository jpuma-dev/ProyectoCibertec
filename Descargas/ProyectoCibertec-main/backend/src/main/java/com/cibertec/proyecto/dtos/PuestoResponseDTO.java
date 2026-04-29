package com.cibertec.proyecto.dtos;

import lombok.Data;

@Data
public class PuestoResponseDTO {
    private Long id;
    private String numero;
    private String descripcion;
    private String socioNombreCompleto;
}
