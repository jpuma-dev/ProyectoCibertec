package com.cibertec.proyecto.repositories;

import com.cibertec.proyecto.entities.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    List<Pago> findByFechaBetween(LocalDateTime inicio,
                                  LocalDateTime fin);

    @Query("""
                SELECT SUM(p.monto) FROM Pago p
                WHERE p.fecha BETWEEN :inicio AND :fin
            """)
    Double totalPagosEnRango(@Param("inicio") LocalDateTime inicio,
                             @Param("fin") LocalDateTime fin);
}
