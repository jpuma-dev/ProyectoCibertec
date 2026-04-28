package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.DeudaDTO;
import com.cibertec.proyecto.dtos.DeudaResponseDTO;
import com.cibertec.proyecto.entities.ConceptoDeuda;
import com.cibertec.proyecto.entities.Deuda;
import com.cibertec.proyecto.entities.Puesto;
import com.cibertec.proyecto.enums.EstadoDeuda;
import com.cibertec.proyecto.exceptions.ResourceNotFoundException;
import com.cibertec.proyecto.repositories.ConceptoDeudaRepository;
import com.cibertec.proyecto.repositories.DeudaRepository;
import com.cibertec.proyecto.repositories.PuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
        validarDeudaDTO(dto);
        List<Puesto> puestos = puestoRepository.findAllById(dto.getPuestoIds());
        if (puestos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron los puestos especificados");
        }
        Deuda savedDeuda = persistirDeuda(dto.getMonto(), dto.getFecha(), dto.getConceptoId(), puestos);
        return toResponseDTO(savedDeuda);
    }

    @Transactional
    public void generarDeudasMasivas(DeudaDTO dto) {
        validarDeudaDTO(dto);
        List<Puesto> puestos = (dto.getPuestoIds() == null || dto.getPuestoIds().isEmpty()) 
                ? puestoRepository.findAll().stream().filter(p -> p.getSocio() != null).collect(Collectors.toList())
                : puestoRepository.findAllById(dto.getPuestoIds());

        if (puestos.isEmpty()) {
            throw new ResourceNotFoundException("No se seleccionaron puestos válidos para generar deudas");
        }

        double montoIndividual = (dto.getMontoTotal() != null && dto.getMontoTotal() > 0)
                ? dto.getMontoTotal() / puestos.size()
                : dto.getMonto();

        for (Puesto puesto : puestos) {
            persistirDeuda(montoIndividual, dto.getFecha(), dto.getConceptoId(), List.of(puesto));
        }
    }

    private Deuda persistirDeuda(Double monto, LocalDate fecha, Long conceptoId, List<Puesto> puestos) {
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

    public List<Object[]> obtenerReporteDeudasPorSocio() {
        return deudaRepository.reporteDeudasPendientesPorSocio();
    }

    public List<Object[]> obtenerReporteMorosidad(Long socioId, Long puestoId, Long conceptoId) {
        return deudaRepository.reporteMorosidadDinamico(socioId, puestoId, conceptoId);
    }

    private void validarDeudaDTO(DeudaDTO dto) {
        if ((dto.getMonto() == null || dto.getMonto() <= 0) && (dto.getMontoTotal() == null || dto.getMontoTotal() <= 0)) {
            throw new IllegalArgumentException("Debe proporcionar un monto individual o un monto total mayor a 0");
        }
    }

    public List<DeudaResponseDTO> listarTodas() {
        return deudaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private DeudaResponseDTO toResponseDTO(Deuda deuda) {
        DeudaResponseDTO dto = new DeudaResponseDTO();
        dto.setId(deuda.getId());
        dto.setMonto(deuda.getMonto());
        dto.setFecha(deuda.getFecha());
        dto.setEstado(deuda.getEstado());
        dto.setConceptoNombre(deuda.getConcepto().getNombre());
        dto.setPuestoNumeros(deuda.getPuestos().stream()
                .map(Puesto::getNumero)
                .collect(Collectors.toList()));
        return dto;
    }
}
