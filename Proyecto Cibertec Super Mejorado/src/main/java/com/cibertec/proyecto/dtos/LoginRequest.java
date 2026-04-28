package com.cibertec.proyecto.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO para la petición de login.
 * Recibe username y password en texto plano (viajan por HTTPS en producción).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "El username es obligatorio")
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
