package com.cibertec.proyecto.repositories;

import com.cibertec.proyecto.entities.ConceptoDeuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptoDeudaRepository extends JpaRepository<ConceptoDeuda, Long> {
    boolean existsByNombre(String nombre);
}
