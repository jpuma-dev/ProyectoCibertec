package com.cibertec.proyecto.repository;

import com.cibertec.proyecto.entity.Socio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SocioRepository extends JpaRepository<Socio, Long> {

    Optional<Socio> findByDni(String dni);

    boolean existsByDni(String dni);

    void deleteByDni(String dni);
}
