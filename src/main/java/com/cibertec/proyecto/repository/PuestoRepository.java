package com.cibertec.proyecto.repository;

import com.cibertec.proyecto.entity.Puesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PuestoRepository extends JpaRepository<Puesto, Long> {

    Optional<Puesto> findByNumero(String numero);

    boolean existsByNumero(String numero);

    List<Puesto> findBySocioId(Long socioId);
}
