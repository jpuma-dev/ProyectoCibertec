package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PuestoDTO {

    @NotBlank(message = "El número de puesto es obligatorio")
    @Size(min = 2, max = 10, message = "El número debe tener entre 2 y 10 caracteres")
    private String numero;

    @Size(max = 200)
    private String descripcion;

    private Long socioId;
}
