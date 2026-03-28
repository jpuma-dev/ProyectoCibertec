package com.cibertec.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PuestoDTO {

    @NotBlank(message = "El número de puesto es obligatorio")
    private String numero;

    private Long socioId; // opcional
}
