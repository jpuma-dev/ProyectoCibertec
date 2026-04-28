package com.cibertec.proyecto.services;

import com.cibertec.proyecto.enums.EstadoDeuda;
import com.cibertec.proyecto.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SocioRepository socioRepository;
    private final DeudaRepository deudaRepository;
    private final PagoRepository pagoRepository;

    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSocios", socioRepository.count());
        stats.put("cantidadDeudasPendientes", deudaRepository.countByEstado(EstadoDeuda.PENDIENTE));

        Double montoPendiente = deudaRepository.sumMontoByEstado(EstadoDeuda.PENDIENTE);
        stats.put("montoTotalPendiente", montoPendiente != null ? montoPendiente : 0.0);

        LocalDate hoy = LocalDate.now();
        Double recaudadoHoy = pagoRepository.totalPagosEnRango(
                hoy.atStartOfDay(), hoy.plusDays(1).atStartOfDay());
        stats.put("recaudacionHoy", recaudadoHoy != null ? recaudadoHoy : 0.0);
        return stats;
    }
}
