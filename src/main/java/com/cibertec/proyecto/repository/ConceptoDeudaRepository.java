package com.cibertec.proyecto.repository;

import com.cibertec.proyecto.entity.ConceptoDeuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConceptoDeudaRepository extends JpaRepository<ConceptoDeuda, Long> {
}
