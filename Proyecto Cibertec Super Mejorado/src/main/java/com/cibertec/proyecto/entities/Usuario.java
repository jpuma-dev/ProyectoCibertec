package com.cibertec.proyecto.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * Entidad Usuario para autenticación con Spring Security.
 * El campo 'password' se almacena cifrado con BCryptPasswordEncoder.
 * Implementa UserDetails para integrarse con Spring Security.
 * (Requisito del proyecto: usuario y password en BD con BCrypt)
 */
@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    /**
     * Contraseña cifrada con BCryptPasswordEncoder.
     * NUNCA se guarda en texto plano.
     */
    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 20)
    @Builder.Default
    private String rol = "ROLE_USER";

    @Column(nullable = false)
    @Builder.Default
    private boolean activo = true;

    // ── UserDetails methods ──────────────────────────────────

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(this.rol));
    }

    @Override
    public boolean isAccountNonExpired()  { return true; }

    @Override
    public boolean isAccountNonLocked()   { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return this.activo; }
}
