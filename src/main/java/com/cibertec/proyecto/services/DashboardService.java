package com.cibertec.proyecto.services;

import com.cibertec.proyecto.enums.EstadoDeuda;
import com.cibertec.proyecto.repositories.DeudaRepository;
import com.cibertec.proyecto.repositories.PagoRepository;
import com.cibertec.proyecto.repositories.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SocioRepository socioRepository;
    private final DeudaRepository deudaRepository;
    private final PagoRepository pagoRepository;

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();

        // Total Socios
        stats.put("totalSocios", socioRepository.count());

        // Deudas Pendientes
        stats.put("cantidadDeudasPendientes", deudaRepository.countByEstado(EstadoDeuda.PENDIENTE));
        Double montoPendiente = deudaRepository.sumMontoByEstado(EstadoDeuda.PENDIENTE);
        stats.put("montoTotalPendiente", montoPendiente != null ? montoPendiente : 0.0);

        // Recaudación del día
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();
        Double totalHoy = pagoRepository.totalPagosEnRango(inicio, fin);
        stats.put("recaudacionHoy", totalHoy != null ? totalHoy : 0.0);

        return stats;
    }
}
