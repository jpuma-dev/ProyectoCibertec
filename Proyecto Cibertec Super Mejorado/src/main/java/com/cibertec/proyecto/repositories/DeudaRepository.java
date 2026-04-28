package com.cibertec.proyecto.repositories;

import com.cibertec.proyecto.entities.Deuda;
import com.cibertec.proyecto.enums.EstadoDeuda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeudaRepository extends JpaRepository<Deuda, Long> {

    long countByEstado(EstadoDeuda estado);

    @Query("SELECT SUM(d.monto) FROM Deuda d WHERE d.estado = :estado")
    Double sumMontoByEstado(@Param("estado") EstadoDeuda estado);

    // JPQL: reporte de deudas pendientes agrupadas por socio
    @Query("SELECT s.nombre, s.dni, SUM(d.monto) " +
           "FROM Deuda d JOIN d.puestos p JOIN p.socio s " +
           "WHERE d.estado = 'PENDIENTE' " +
           "GROUP BY s.nombre, s.dni")
    List<Object[]> reporteDeudasPendientesPorSocio();

    // JPQL: reporte de morosidad con filtros dinámicos
    @Query("SELECT s.nombre, s.dni, p.numero, c.nombre, d.monto, d.fecha " +
           "FROM Deuda d JOIN d.puestos p JOIN p.socio s JOIN d.concepto c " +
           "WHERE d.estado = 'PENDIENTE' " +
           "AND (:socioId   IS NULL OR s.id = :socioId) " +
           "AND (:puestoId  IS NULL OR p.id = :puestoId) " +
           "AND (:conceptoId IS NULL OR c.id = :conceptoId)")
    List<Object[]> reporteMorosidadDinamico(
            @Param("socioId")    Long socioId,
            @Param("puestoId")   Long puestoId,
            @Param("conceptoId") Long conceptoId);
}
