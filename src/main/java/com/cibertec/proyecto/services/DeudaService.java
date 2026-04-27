package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.DeudaDTO;
import com.cibertec.proyecto.dtos.DeudaResponseDTO;
import com.cibertec.proyecto.entities.*;
import com.cibertec.proyecto.enums.EstadoDeuda;
import com.cibertec.proyecto.exceptions.ResourceNotFoundException;
import com.cibertec.proyecto.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DeudaService {

    private final DeudaRepository deudaRepository;
    private final PuestoRepository puestoRepository;
    private final ConceptoDeudaRepository conceptoRepository;

    @Transactional
    public DeudaResponseDTO crearDeuda(DeudaDTO dto) {
        validar(dto);
        List<Puesto> puestos = puestoRepository.findAllById(dto.getPuestoIds());
        if (puestos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron los puestos especificados");
        }
        return toDTO(persistir(dto.getMonto(), dto.getFecha(), dto.getConceptoId(), puestos));
    }

    @Transactional
    public void generarDeudasMasivas(DeudaDTO dto) {
        validar(dto);
        List<Puesto> puestos = (dto.getPuestoIds() == null || dto.getPuestoIds().isEmpty())
                ? puestoRepository.findAll().stream()
                        .filter(p -> p.getSocio() != null)
                        .collect(Collectors.toList())
                : puestoRepository.findAllById(dto.getPuestoIds());

        if (puestos.isEmpty()) {
            throw new ResourceNotFoundException("No hay puestos con socios asignados");
        }

        double montoIndividual = (dto.getMontoTotal() != null && dto.getMontoTotal() > 0)
                ? dto.getMontoTotal() / puestos.size()
                : dto.getMonto();

        for (Puesto p : puestos) {
            persistir(montoIndividual, dto.getFecha(), dto.getConceptoId(), List.of(p));
        }
    }

    public List<DeudaResponseDTO> listarTodas() {
        return deudaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<Object[]> obtenerReporteDeudasPorSocio() {
        return deudaRepository.reporteDeudasPendientesPorSocio();
    }

    public List<Object[]> obtenerReporteMorosidad(Long socioId, Long puestoId, Long conceptoId) {
        return deudaRepository.reporteMorosidadDinamico(socioId, puestoId, conceptoId);
    }

    // ── Helpers ──────────────────────────────────────────
    private Deuda persistir(Double monto, java.time.LocalDate fecha, Long conceptoId, List<Puesto> puestos) {
        ConceptoDeuda concepto = conceptoRepository.findById(conceptoId)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado con ID: " + conceptoId));
        Deuda deuda = new Deuda();
        deuda.setMonto(monto);
        deuda.setFecha(fecha);
        deuda.setEstado(EstadoDeuda.PENDIENTE);
        deuda.setConcepto(concepto);
        deuda.setPuestos(puestos);
        return deudaRepository.save(deuda);
    }

    private void validar(DeudaDTO dto) {
        boolean sinMonto = (dto.getMonto() == null || dto.getMonto() <= 0);
        boolean sinTotal = (dto.getMontoTotal() == null || dto.getMontoTotal() <= 0);
        if (sinMonto && sinTotal) {
            throw new IllegalArgumentException("Debe proporcionar un monto individual o un monto total mayor a 0");
        }
    }

    private DeudaResponseDTO toDTO(Deuda d) {
        DeudaResponseDTO dto = new DeudaResponseDTO();
        dto.setId(d.getId());
        dto.setMonto(d.getMonto());
        dto.setFecha(d.getFecha());
        dto.setEstado(d.getEstado());
        dto.setConceptoNombre(d.getConcepto().getNombre());
        dto.setPuestoNumeros(d.getPuestos().stream().map(Puesto::getNumero).collect(Collectors.toList()));
        return dto;
    }
}
