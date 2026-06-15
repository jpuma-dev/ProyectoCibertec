package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.ConceptoDeudaDTO;
import com.cibertec.proyecto.entities.ConceptoDeuda;
import com.cibertec.proyecto.exceptions.BadRequestException;
import com.cibertec.proyecto.exceptions.ConflictException;
import com.cibertec.proyecto.exceptions.ResourceNotFoundException;
import com.cibertec.proyecto.repositories.ConceptoDeudaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConceptoDeudaService {

    private final ConceptoDeudaRepository conceptoRepository;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("Verificando conceptos iniciales");

        crearSiNoExiste("Cuota de Mantenimiento", "Cobro mensual por mantenimiento", true, 50.0);
        crearSiNoExiste("Servicio de Agua", "Consumo de agua mensual", true, 20.0);
        crearSiNoExiste("Servicio de Luz", "Consumo de energia electrica", true, 30.0);
        crearSiNoExiste("Vigilancia", "Servicio de seguridad", true, 15.0);
        crearSiNoExiste("Alquiler", "Pago por derecho de uso de puesto", true, 100.0);
        crearSiNoExiste("Cuota Extraordinaria", "Cobro eventual por gastos especiales", false, 80.0);
        crearSiNoExiste("Multa por Atraso", "Penalidad por pago fuera de fecha", false, 30.0);
    }

    @Transactional(readOnly = true)
    public List<ConceptoDeuda> listarTodos() {
        return conceptoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public ConceptoDeuda buscarPorId(Long id) {
        return conceptoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el concepto de deuda con ID: " + id));
    }

    @Transactional
    public ConceptoDeuda crearConcepto(ConceptoDeudaDTO dto) {
        validarDatos(dto);
        String nombre = normalizarNombre(dto.getNombre());

        if (conceptoRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictException("Ya existe un concepto de deuda con el nombre: " + nombre);
        }

        ConceptoDeuda concepto = ConceptoDeuda.builder()
                .nombre(nombre)
                .descripcion(normalizarTextoOpcional(dto.getDescripcion()))
                .recurrente(dto.isRecurrente())
                .montoSugerido(dto.getMontoSugerido())
                .build();

        return conceptoRepository.save(concepto);
    }

    @Transactional
    public ConceptoDeuda actualizarConcepto(Long id, ConceptoDeudaDTO dto) {
        validarDatos(dto);

        ConceptoDeuda concepto = buscarPorId(id);
        String nuevoNombre = normalizarNombre(dto.getNombre());

        if (!concepto.getNombre().equalsIgnoreCase(nuevoNombre)
                && conceptoRepository.existsByNombreIgnoreCase(nuevoNombre)) {
            throw new ConflictException("Ya existe otro concepto de deuda con el nombre: " + nuevoNombre);
        }

        concepto.setNombre(nuevoNombre);
        concepto.setDescripcion(normalizarTextoOpcional(dto.getDescripcion()));
        concepto.setRecurrente(dto.isRecurrente());
        concepto.setMontoSugerido(dto.getMontoSugerido());

        return conceptoRepository.save(concepto);
    }

    @Transactional
    public void eliminarConcepto(Long id) {
        ConceptoDeuda concepto = buscarPorId(id);

        if (concepto.getDeudas() != null && !concepto.getDeudas().isEmpty()) {
            throw new ConflictException("No se puede eliminar un concepto con deudas registradas");
        }

        conceptoRepository.delete(concepto);
    }

    private void crearSiNoExiste(String nombre, String descripcion, boolean recurrente, Double montoSugerido) {
        if (!conceptoRepository.existsByNombreIgnoreCase(nombre)) {
            conceptoRepository.save(ConceptoDeuda.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .recurrente(recurrente)
                    .montoSugerido(montoSugerido)
                    .build());
        }
    }

    private void validarDatos(ConceptoDeudaDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Los datos del concepto son obligatorios");
        }

        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new BadRequestException("El nombre del concepto es obligatorio");
        }

        if (dto.getMontoSugerido() == null || dto.getMontoSugerido() < 0) {
            throw new BadRequestException("El monto sugerido no puede ser negativo");
        }
    }

    private String normalizarNombre(String valor) {
        return valor.trim().replaceAll("\\s+", " ");
    }

    private String normalizarTextoOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim().replaceAll("\\s+", " ");
    }
}
