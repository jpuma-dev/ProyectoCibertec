package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.*;
import com.cibertec.proyecto.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * ─────────────────────────────────────────────────────────────
 * REST Controller de Autenticación
 *
 * REQUISITO DEL PROYECTO:
 *   "Un servicio web Rest de login, donde el usuario y password
 *    deben estar en una Base de Datos, el campo password debe
 *    estar cifrado con BCryptPasswordEncoder."
 *
 * Endpoints:
 *   POST /api/auth/login    → valida credenciales contra la BD
 *   POST /api/auth/register → crea usuario con password cifrado
 * ─────────────────────────────────────────────────────────────
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Login: verifica username y password (cifrado con BCrypt) en la BD.
     * Devuelve datos del usuario autenticado.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Autenticación exitosa"));
    }

    /**
     * Registro: guarda nuevo usuario con contraseña cifrada con BCrypt.
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<LoginResponse>> register(
            @Valid @RequestBody RegisterRequest request) {
        LoginResponse response = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Usuario registrado correctamente"));
    }
}
