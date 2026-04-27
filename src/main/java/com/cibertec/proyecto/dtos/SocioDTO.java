package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SocioDTO {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100)
    private String apellido;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 8, max = 8, message = "El DNI debe tener exactamente 8 dígitos")
    @Pattern(regexp = "\\d{8}", message = "El DNI solo debe contener números")
    private String dni;

    @Size(min = 9, max = 9, message = "El teléfono debe tener 9 dígitos")
    @Pattern(regexp = "9\\d{8}", message = "El teléfono debe iniciar con 9")
    private String telefono;

    @Email(message = "El email no es válido")
    private String email;
}
