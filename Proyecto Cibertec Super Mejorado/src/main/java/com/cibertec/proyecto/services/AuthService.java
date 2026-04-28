package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.LoginRequest;
import com.cibertec.proyecto.dtos.LoginResponse;
import com.cibertec.proyecto.dtos.RegisterRequest;
import com.cibertec.proyecto.entities.Usuario;
import com.cibertec.proyecto.exceptions.ConflictException;
import com.cibertec.proyecto.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Servicio de autenticación.
 *
 * login()    → verifica credenciales con AuthenticationManager.
 * register() → guarda nuevo usuario con password cifrado por BCryptPasswordEncoder.
 *
 * El cifrado con BCrypt es un requisito explícito del proyecto.
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;   // BCryptPasswordEncoder

    /**
     * Autenticar usuario contra la base de datos.
     */
    public LoginResponse login(LoginRequest request) {
        // Spring Security verifica credenciales automáticamente con UserDetailsService
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(auth);

        Usuario usuario = (Usuario) auth.getPrincipal();
        return LoginResponse.builder()
                .username(usuario.getUsername())
                .rol(usuario.getRol())
                .mensaje("Login exitoso")
                .build();
    }

    /**
     * Registrar un nuevo usuario.
     * La contraseña se cifra con BCryptPasswordEncoder antes de guardarse.
     */
    public LoginResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("El username ya está en uso: " + request.getUsername());
        }

        Usuario nuevo = Usuario.builder()
                .username(request.getUsername())
                // BCrypt cifra la contraseña con salt aleatorio
                .password(passwordEncoder.encode(request.getPassword()))
                .rol(request.getRol() != null ? request.getRol() : "ROLE_USER")
                .activo(true)
                .build();

        usuarioRepository.save(nuevo);

        return LoginResponse.builder()
                .username(nuevo.getUsername())
                .rol(nuevo.getRol())
                .mensaje("Usuario registrado correctamente")
                .build();
    }
}
