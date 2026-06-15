package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConceptoDeudaDTO {

    @NotBlank(message = "El nombre del concepto es obligatorio")
    private String nombre;

    private String descripcion;

    private boolean recurrente;

    @DecimalMin(value = "0.0", message = "El monto sugerido no puede ser negativo")
    private Double montoSugerido;
}