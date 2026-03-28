package com.cibertec.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SocioDTO {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank
    @Size(min = 8, max = 8, message = "El DNI debe tener 8 dígitos")
    private String dni;

    private String telefono;
}
