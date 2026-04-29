package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record SocioDTO(
    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    String nombre,

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 3, max = 100, message = "El apellido debe tener entre 3 y 100 caracteres")
    String apellido,

    @NotBlank(message = "El DNI es obligatorio")
    @Pattern(regexp = "\\d{8}", message = "El DNI debe tener exactamente 8 digitos")
    String dni,

    @NotBlank(message = "El telefono es obligatorio")
    @Pattern(regexp = "9\\d{8}", message = "El telefono debe empezar con 9 y tener 9 digitos")
    String telefono,

    @Email(message = "El formato del email no es valido")
    String email
) {}
