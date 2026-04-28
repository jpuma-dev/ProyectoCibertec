package com.cibertec.proyecto.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Configuración de Spring Security.
 *
 * - El endpoint POST /api/auth/** es público (login y registro).
 * - El resto de endpoints requiere autenticación.
 * - Se usa BCryptPasswordEncoder para cifrar contraseñas (requisito del proyecto).
 * - Sesión stateless: el cliente envía credenciales en cada petición (Basic Auth).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /**
     * BCryptPasswordEncoder: cifra passwords con salt aleatorio y factor de costo 10.
     * Este es el encoder que exige el proyecto (BCryptPasswordEncoder).
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // Desactivar CSRF (API REST stateless no lo necesita)
            .csrf(AbstractHttpConfigurer::disable)

            // Configurar CORS para Angular en localhost:4200
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // Reglas de autorización
            .authorizeHttpRequests(auth -> auth
                // Endpoints públicos: login y registro
                .requestMatchers("/api/auth/**").permitAll()
                // Todo lo demás requiere autenticación
                .anyRequest().authenticated()
            )

            // Usar HTTP Basic Auth (simple, sin JWT, suficiente para el proyecto)
            .httpBasic(basic -> {})

            // Sesión stateless: no se guarda estado en el servidor
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }

    /**
     * CORS: permite que Angular (puerto 4200) consuma la API.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:4200"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
