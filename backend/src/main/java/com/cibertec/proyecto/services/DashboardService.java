package com.cibertec.proyecto.services;

import com.cibertec.proyecto.enums.EstadoDeuda;
import com.cibertec.proyecto.repositories.ConceptoDeudaRepository;
import com.cibertec.proyecto.repositories.DeudaRepository;
import com.cibertec.proyecto.repositories.PagoRepository;
import com.cibertec.proyecto.repositories.PuestoRepository;
import com.cibertec.proyecto.repositories.SocioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SocioRepository socioRepository;
    private final PuestoRepository puestoRepository;
    private final ConceptoDeudaRepository conceptoRepository;
    private final DeudaRepository deudaRepository;
    private final PagoRepository pagoRepository;

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerEstadisticas() {
        Map<String, Object> stats = new LinkedHashMap<>();
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();

        Double montoPendiente = deudaRepository.sumMontoByEstado(EstadoDeuda.PENDIENTE);
        Double montoVencido = deudaRepository.sumMontoVencido(EstadoDeuda.PENDIENTE, hoy);
        Double totalHoy = pagoRepository.totalPagosEnRango(inicio, fin);

        stats.put("totalSocios", socioRepository.count());
        stats.put("totalPuestos", puestoRepository.count());
        stats.put("puestosOcupados", puestoRepository.countBySocioIsNotNull());
        stats.put("puestosDisponibles", puestoRepository.countBySocioIsNull());
        stats.put("totalConceptos", conceptoRepository.count());
        stats.put("cantidadDeudasPendientes", deudaRepository.countByEstado(EstadoDeuda.PENDIENTE));
        stats.put("cantidadDeudasPagadas", deudaRepository.countByEstado(EstadoDeuda.PAGADO));
        stats.put("cantidadDeudasVencidas", deudaRepository.countByEstadoAndFechaBefore(EstadoDeuda.PENDIENTE, hoy));
        stats.put("montoTotalPendiente", redondear(montoPendiente != null ? montoPendiente : 0.0));
        stats.put("montoTotalVencido", redondear(montoVencido != null ? montoVencido : 0.0));
        stats.put("recaudacionHoy", redondear(totalHoy != null ? totalHoy : 0.0));

        return stats;
    }

    private double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
