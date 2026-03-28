package com.cibertec.proyecto.service;

import com.cibertec.proyecto.dto.PagoDTO;
import com.cibertec.proyecto.entity.Deuda;
import com.cibertec.proyecto.entity.Pago;
import com.cibertec.proyecto.enums.EstadoDeuda;
import com.cibertec.proyecto.repository.DeudaRepository;
import com.cibertec.proyecto.repository.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final DeudaRepository deudaRepository;

    @Transactional
    public Pago registrarPago(PagoDTO dto) {

        Optional<Deuda> optionalDeuda = deudaRepository.findById(dto.getDeudaId());
        if (optionalDeuda.isEmpty()) {
            throw new RuntimeException("Deuda no encontrada");
        }

        Deuda deuda = optionalDeuda.get();

        if (deuda.getEstado() == EstadoDeuda.PAGADO) {
            throw new RuntimeException("La deuda ya está pagada");
        }

        double totalPagado = 0;

        if (deuda.getPagos() != null) {
            for (Pago p : deuda.getPagos()) {
                totalPagado += p.getMonto();
            }
        }

        if (totalPagado + dto.getMonto() > deuda.getMonto()) {
            throw new RuntimeException("El pago excede el monto de la deuda");
        }

        Pago pago = new Pago();
        pago.setMonto(dto.getMonto());
        pago.setFecha(LocalDateTime.now());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setDeuda(deuda);

        Pago pagoGuardado = pagoRepository.save(pago);

        if (totalPagado + dto.getMonto() == deuda.getMonto()) {
            deuda.setEstado(EstadoDeuda.PAGADO);
            deudaRepository.save(deuda);
        }

        return pagoGuardado;
    }

    public Map<String, Object> resumenFlujoCajaDelDia() {
        LocalDate hoy = LocalDate.now();
        LocalDateTime inicio = hoy.atStartOfDay();
        LocalDateTime fin = hoy.plusDays(1).atStartOfDay();

        List<Pago> pagosDelDia = pagoRepository.findByFechaBetween(inicio, fin);
        Double total = pagoRepository.totalPagosEnRango(inicio, fin);

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("fecha", hoy);
        resumen.put("cantidadPagos", pagosDelDia.size());
        resumen.put("totalRecaudado", redondear(total != null ? total : 0.0));
        resumen.put("pagos", pagosDelDia);

        return resumen;
    }

    private double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}