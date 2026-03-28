package com.cibertec.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConceptoDeudaDTO {

    @NotBlank
    private String nombre;

    private String descripcion;
}
