package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConceptoDeudaDTO {

    @NotBlank
    private String nombre;

    private String descripcion;

    private boolean recurrente;

    private Double montoSugerido;
}
