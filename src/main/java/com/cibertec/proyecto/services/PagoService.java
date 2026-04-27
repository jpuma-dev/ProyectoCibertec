package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.PagoDTO;
import com.cibertec.proyecto.dtos.PagoResponseDTO;
import com.cibertec.proyecto.entities.Deuda;
import com.cibertec.proyecto.entities.Pago;
import com.cibertec.proyecto.enums.EstadoDeuda;
import com.cibertec.proyecto.exceptions.ConflictException;
import com.cibertec.proyecto.exceptions.ResourceNotFoundException;
import com.cibertec.proyecto.repositories.DeudaRepository;
import com.cibertec.proyecto.repositories.PagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PagoService {

    private final PagoRepository pagoRepository;
    private final DeudaRepository deudaRepository;

    @Transactional
    public PagoResponseDTO registrarPago(PagoDTO dto) {
        Deuda deuda = deudaRepository.findById(dto.getDeudaId())
                .orElseThrow(() -> new ResourceNotFoundException("Deuda no encontrada con ID: " + dto.getDeudaId()));

        if (deuda.getEstado() == EstadoDeuda.PAGADO) {
            throw new ConflictException("La deuda ya se encuentra pagada");
        }
        if (!dto.getMonto().equals(deuda.getMonto())) {
            throw new IllegalArgumentException("El pago debe ser por el total de la deuda: S/ " + deuda.getMonto());
        }

        Pago pago = new Pago();
        pago.setMonto(dto.getMonto());
        pago.setFecha(LocalDateTime.now());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setDeuda(deuda);
        Pago saved = pagoRepository.save(pago);

        deuda.setEstado(EstadoDeuda.PAGADO);
        deudaRepository.save(deuda);

        return toDTO(saved);
    }

    public Map<String, Object> resumenFlujoCaja(LocalDate from, LocalDate to) {
        LocalDateTime inicio = from.atStartOfDay();
        LocalDateTime fin = to.plusDays(1).atStartOfDay();

        List<Pago> pagos = pagoRepository.findByFechaBetween(inicio, fin);
        Double total = pagoRepository.totalPagosEnRango(inicio, fin);

        Map<String, Object> res = new LinkedHashMap<>();
        res.put("desde", from);
        res.put("hasta", to);
        res.put("cantidadPagos", pagos.size());
        res.put("totalRecaudado", redondear(total != null ? total : 0.0));
        res.put("pagos", pagos.stream().map(this::toDTO).toList());
        return res;
    }

    private PagoResponseDTO toDTO(Pago p) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(p.getId());
        dto.setMonto(p.getMonto());
        dto.setFecha(p.getFecha());
        dto.setMetodoPago(p.getMetodoPago());
        dto.setDeudaId(p.getDeuda().getId());
        dto.setConceptoDeuda(p.getDeuda().getConcepto().getNombre());
        return dto;
    }

    private double redondear(double v) {
        return BigDecimal.valueOf(v).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
