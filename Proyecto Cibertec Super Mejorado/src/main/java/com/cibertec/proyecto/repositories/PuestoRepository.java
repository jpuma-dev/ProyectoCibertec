package com.cibertec.proyecto.repositories;

import com.cibertec.proyecto.entities.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Long> {
    Optional<Puesto> findByNumero(String numero);
    boolean existsByNumero(String numero);
}
