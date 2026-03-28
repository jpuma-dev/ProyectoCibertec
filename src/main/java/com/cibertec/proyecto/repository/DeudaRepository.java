package com.cibertec.proyecto.repository;

import com.cibertec.proyecto.entity.Deuda;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.stereotype.Repository;

@Repository
public interface DeudaRepository extends JpaRepository<Deuda, Long> {

}
