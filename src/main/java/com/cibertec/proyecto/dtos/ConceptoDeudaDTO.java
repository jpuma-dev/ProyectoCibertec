package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ConceptoDeudaDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @Size(max = 255)
    private String descripcion;

    private boolean recurrente;

    @Positive(message = "El monto sugerido debe ser positivo")
    private Double montoSugerido;
}
