package com.cibertec.proyecto.dtos;

import lombok.*;

/**
 * DTO de respuesta exitosa al hacer login.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String username;
    private String rol;
    private String mensaje;
}
