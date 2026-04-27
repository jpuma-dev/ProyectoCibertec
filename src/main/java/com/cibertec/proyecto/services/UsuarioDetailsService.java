package com.cibertec.proyecto.services;

import com.cibertec.proyecto.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

/**
 * Servicio que Spring Security usa para cargar usuarios desde la BD.
 * Implementa UserDetailsService tal como lo indica el manual del curso.
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {
        return usuarioRepository.findByUsername(username)
                .orElseThrow(() ->
                    new UsernameNotFoundException(
                        "Usuario no encontrado: " + username));
    }
}
