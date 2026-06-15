package com.cibertec.proyecto.repositories;

import com.cibertec.proyecto.entities.Deuda;
import com.cibertec.proyecto.enums.EstadoDeuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DeudaRepository extends JpaRepository<Deuda, Long> {

    @Query("SELECT s.nombre, s.dni, SUM(d.monto) FROM Deuda d JOIN d.puestos p JOIN p.socio s WHERE d.estado = 'PENDIENTE' GROUP BY s.nombre, s.dni")
    List<Object[]> reporteDeudasPendientesPorSocio();

    @Query("SELECT s.nombre, s.dni, p.numero, c.nombre, d.monto, d.fecha " +
           "FROM Deuda d JOIN d.puestos p JOIN p.socio s JOIN d.concepto c " +
           "WHERE d.estado = 'PENDIENTE' " +
           "AND (:socioId IS NULL OR s.id = :socioId) " +
           "AND (:puestoId IS NULL OR p.id = :puestoId) " +
           "AND (:conceptoId IS NULL OR c.id = :conceptoId)")
    List<Object[]> reporteMorosidadDinamico(Long socioId, Long puestoId, Long conceptoId);

    long countByEstado(EstadoDeuda estado);

    @Query("SELECT SUM(d.monto) FROM Deuda d WHERE d.estado = :estado")
    Double sumMontoByEstado(EstadoDeuda estado);

    List<Deuda> findByEstado(EstadoDeuda estado);

    long countByEstadoAndFechaBefore(EstadoDeuda estado, LocalDate fecha);

    @Query("SELECT COALESCE(SUM(d.monto), 0) FROM Deuda d WHERE d.estado = :estado AND d.fecha < :fecha")
    Double sumMontoVencido(@Param("estado") EstadoDeuda estado, @Param("fecha") LocalDate fecha);

    @Query("""
            SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END
            FROM Deuda d JOIN d.puestos p
            WHERE d.concepto.id = :conceptoId
              AND p.id = :puestoId
              AND d.fecha = :fecha
              AND d.estado = :estado
            """)
    boolean existsByConceptoPuestoFechaAndEstado(@Param("conceptoId") Long conceptoId,
                                                 @Param("puestoId") Long puestoId,
                                                 @Param("fecha") LocalDate fecha,
                                                 @Param("estado") EstadoDeuda estado);
}
