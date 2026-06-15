package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PuestoDTO {

    // Formato (Letras y Números) ej: A01, B02, 101
    @NotBlank(message = "El número de puesto es obligatorio")
    @Pattern(regexp = "^[A-Z0-9]{2,5}$", message = "Use de 2 a 5 letras o números (ejm: A01, B02)")
    private String numero;

    private String descripcion;

    private Long socioId; // opcional
}
