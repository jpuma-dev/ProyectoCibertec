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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
            throw new IllegalArgumentException("El pago debe ser por el total de la deuda: " + deuda.getMonto());
        }

        Pago pago = new Pago();
        pago.setMonto(dto.getMonto());
        pago.setFecha(LocalDateTime.now());
        pago.setMetodoPago(dto.getMetodoPago());
        pago.setDeuda(deuda);

        Pago savedPago = pagoRepository.save(pago);

        deuda.setEstado(EstadoDeuda.PAGADO);
        deudaRepository.save(deuda);

        return toResponseDTO(savedPago);
    }

    public Map<String, Object> resumenFlujoCaja(LocalDate startDate, LocalDate endDate) {
        LocalDateTime inicio = startDate.atStartOfDay();
        LocalDateTime fin = endDate.plusDays(1).atStartOfDay();

        List<Pago> pagos = pagoRepository.findByFechaBetween(inicio, fin);
        Double total = pagoRepository.totalPagosEnRango(inicio, fin);

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("desde", startDate);
        resumen.put("hasta", endDate);
        resumen.put("cantidadPagos", pagos.size());
        resumen.put("totalRecaudado", redondear(total != null ? total : 0.0));
        resumen.put("pagos", pagos.stream().map(this::toResponseDTO).toList());

        return resumen;
    }

    private PagoResponseDTO toResponseDTO(Pago pago) {
        PagoResponseDTO dto = new PagoResponseDTO();
        dto.setId(pago.getId());
        dto.setMonto(pago.getMonto());
        dto.setFecha(pago.getFecha());
        dto.setMetodoPago(pago.getMetodoPago());
        dto.setDeudaId(pago.getDeuda().getId());
        dto.setConceptoDeuda(pago.getDeuda().getConcepto().getNombre());
        return dto;
    }

    private double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
