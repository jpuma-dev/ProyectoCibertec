package com.cibertec.proyecto.repositories;

import com.cibertec.proyecto.entities.Socio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {

    Optional<Socio> findByDni(String dni);

    boolean existsByDni(String dni);

    void deleteByDni(String dni);

    Page<Socio> findByNombreContainingIgnoreCaseOrDniContaining(String nombre, String dni, Pageable pageable);
}
