package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.DeudaDTO;
import com.cibertec.proyecto.dtos.DeudaResponseDTO;
import com.cibertec.proyecto.entities.ConceptoDeuda;
import com.cibertec.proyecto.entities.Deuda;
import com.cibertec.proyecto.entities.Puesto;
import com.cibertec.proyecto.enums.EstadoDeuda;
import com.cibertec.proyecto.exceptions.BadRequestException;
import com.cibertec.proyecto.exceptions.ConflictException;
import com.cibertec.proyecto.exceptions.ResourceNotFoundException;
import com.cibertec.proyecto.repositories.ConceptoDeudaRepository;
import com.cibertec.proyecto.repositories.DeudaRepository;
import com.cibertec.proyecto.repositories.PuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        List<Puesto> puestos = obtenerPuestosSeleccionados(dto);

        if (puestos.isEmpty()) {
            throw new ResourceNotFoundException("No se encontraron los puestos especificados");
        }

        Deuda savedDeuda = persistirDeuda(dto.getMonto(), dto.getFecha(), dto.getConceptoId(), puestos, false);
        return toResponseDTO(savedDeuda);
    }

    @Transactional
    public int generarDeudasMasivas(DeudaDTO dto) {
        validarDeudaDTO(dto);
        ConceptoDeuda concepto = buscarConcepto(dto.getConceptoId());
        List<Puesto> puestos = obtenerPuestosParaMasivo(dto);

        if (puestos.isEmpty()) {
            throw new ResourceNotFoundException("No se seleccionaron puestos validos para generar deudas");
        }

        double montoIndividual = (dto.getMontoTotal() != null && dto.getMontoTotal() > 0)
                ? redondear(dto.getMontoTotal() / puestos.size())
                : redondear(dto.getMonto());

        int generadas = 0;
        for (Puesto puesto : puestos) {
            if (deudaRepository.existsByConceptoPuestoFechaAndEstado(
                    concepto.getId(), puesto.getId(), dto.getFecha(), EstadoDeuda.PENDIENTE)) {
                continue;
            }
            persistirDeuda(montoIndividual, dto.getFecha(), concepto.getId(), List.of(puesto), true);
            generadas++;
        }

        if (generadas == 0) {
            throw new ConflictException("Ya existen deudas pendientes para el concepto, fecha y puestos seleccionados");
        }

        return generadas;
    }

    @Transactional(readOnly = true)
    public List<DeudaResponseDTO> listarTodas() {
        return deudaRepository.findAll().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<DeudaResponseDTO> listarPorEstado(EstadoDeuda estado) {
        if (estado == null) {
            return listarTodas();
        }

        return deudaRepository.findByEstado(estado).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public DeudaResponseDTO buscarPorId(Long id) {
        return deudaRepository.findById(id)
                .map(this::toResponseDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Deuda no encontrada con ID: " + id));
    }

    @Transactional(readOnly = true)
    public Map<String, Object> obtenerResumen() {
        LocalDate hoy = LocalDate.now();
        Double montoPendiente = deudaRepository.sumMontoByEstado(EstadoDeuda.PENDIENTE);
        Double montoVencido = deudaRepository.sumMontoVencido(EstadoDeuda.PENDIENTE, hoy);

        Map<String, Object> resumen = new LinkedHashMap<>();
        resumen.put("totalDeudas", deudaRepository.count());
        resumen.put("cantidadPendientes", deudaRepository.countByEstado(EstadoDeuda.PENDIENTE));
        resumen.put("cantidadPagadas", deudaRepository.countByEstado(EstadoDeuda.PAGADO));
        resumen.put("cantidadVencidas", deudaRepository.countByEstadoAndFechaBefore(EstadoDeuda.PENDIENTE, hoy));
        resumen.put("montoPendiente", redondear(montoPendiente != null ? montoPendiente : 0.0));
        resumen.put("montoVencido", redondear(montoVencido != null ? montoVencido : 0.0));
        return resumen;
    }

    @Transactional(readOnly = true)
    public List<Object[]> obtenerReporteDeudasPorSocio() {
        return deudaRepository.reporteDeudasPendientesPorSocio();
    }

    @Transactional(readOnly = true)
    public List<Object[]> obtenerReporteMorosidad(Long socioId, Long puestoId, Long conceptoId) {
        return deudaRepository.reporteMorosidadDinamico(socioId, puestoId, conceptoId);
    }

    private Deuda persistirDeuda(Double monto, LocalDate fecha, Long conceptoId, List<Puesto> puestos, boolean masiva) {
        ConceptoDeuda concepto = buscarConcepto(conceptoId);

        if (!masiva && puestos.size() == 1 && deudaRepository.existsByConceptoPuestoFechaAndEstado(
                conceptoId, puestos.get(0).getId(), fecha, EstadoDeuda.PENDIENTE)) {
            throw new ConflictException("Ya existe una deuda pendiente para ese puesto, concepto y fecha");
        }

        Deuda deuda = new Deuda();
        deuda.setMonto(redondear(monto));
        deuda.setFecha(fecha);
        deuda.setEstado(EstadoDeuda.PENDIENTE);
        deuda.setConcepto(concepto);
        deuda.setPuestos(puestos);

        return deudaRepository.save(deuda);
    }

    private List<Puesto> obtenerPuestosSeleccionados(DeudaDTO dto) {
        if (dto.getPuestoIds() == null || dto.getPuestoIds().isEmpty()) {
            throw new BadRequestException("Debe seleccionar al menos un puesto");
        }

        List<Long> ids = dto.getPuestoIds().stream().distinct().toList();
        List<Puesto> puestos = puestoRepository.findAllById(ids);
        validarPuestosEncontrados(ids, puestos);
        validarPuestosConSocio(puestos);
        return puestos;
    }

    private List<Puesto> obtenerPuestosParaMasivo(DeudaDTO dto) {
        List<Puesto> puestos;

        if (dto.getPuestoIds() == null || dto.getPuestoIds().isEmpty()) {
            puestos = puestoRepository.findAll();
        } else {
            List<Long> ids = dto.getPuestoIds().stream().distinct().toList();
            puestos = puestoRepository.findAllById(ids);
            validarPuestosEncontrados(ids, puestos);
        }

        List<Puesto> conSocio = puestos.stream()
                .filter(puesto -> puesto.getSocio() != null)
                .toList();
        validarPuestosConSocio(conSocio);
        return conSocio;
    }

    private void validarPuestosEncontrados(List<Long> ids, List<Puesto> puestos) {
        Set<Long> encontrados = puestos.stream().map(Puesto::getId).collect(Collectors.toSet());
        List<Long> faltantes = ids.stream().filter(id -> !encontrados.contains(id)).toList();

        if (!faltantes.isEmpty()) {
            throw new ResourceNotFoundException("No existen los puestos con ID: " + faltantes);
        }
    }

    private void validarPuestosConSocio(List<Puesto> puestos) {
        if (puestos.isEmpty()) {
            throw new BadRequestException("No hay puestos con socio asignado para generar la deuda");
        }
    }

    private ConceptoDeuda buscarConcepto(Long conceptoId) {
        return conceptoRepository.findById(conceptoId)
                .orElseThrow(() -> new ResourceNotFoundException("Concepto no encontrado con ID: " + conceptoId));
    }

    private void validarDeudaDTO(DeudaDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos de la deuda son obligatorios");
        }

        if (dto.getFecha() == null) {
            throw new BadRequestException("La fecha limite de pago es obligatoria");
        }

        if (dto.getConceptoId() == null) {
            throw new BadRequestException("El concepto de deuda es obligatorio");
        }

        boolean tieneMonto = dto.getMonto() != null && dto.getMonto() > 0;
        boolean tieneMontoTotal = dto.getMontoTotal() != null && dto.getMontoTotal() > 0;

        if (!tieneMonto && !tieneMontoTotal) {
            throw new BadRequestException("Debe proporcionar un monto individual o un monto total mayor a 0");
        }

        if (tieneMonto && tieneMontoTotal) {
            throw new BadRequestException("Use solo monto individual o monto total, no ambos a la vez");
        }
    }

    private DeudaResponseDTO toResponseDTO(Deuda deuda) {
        DeudaResponseDTO dto = new DeudaResponseDTO();
        dto.setId(deuda.getId());
        dto.setMonto(deuda.getMonto());
        dto.setFecha(deuda.getFecha());
        dto.setEstado(deuda.getEstado());
        dto.setConceptoId(deuda.getConcepto().getId());
        dto.setConceptoNombre(deuda.getConcepto().getNombre());
        dto.setPuestoIds(deuda.getPuestos().stream()
                .map(Puesto::getId)
                .toList());
        dto.setPuestoNumeros(deuda.getPuestos().stream()
                .map(Puesto::getNumero)
                .toList());
        dto.setSocioNombres(deuda.getPuestos().stream()
                .map(Puesto::getSocio)
                .filter(socio -> socio != null)
                .map(socio -> socio.getNombre() + " " + socio.getApellido())
                .distinct()
                .toList());
        return dto;
    }

    private double redondear(double valor) {
        return BigDecimal.valueOf(valor)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
