package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.ConceptoDeudaDTO;
import com.cibertec.proyecto.entities.ConceptoDeuda;
import com.cibertec.proyecto.repositories.ConceptoDeudaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConceptoDeudaService {

    private final ConceptoDeudaRepository conceptoRepository;

    @PostConstruct
    public void init() {
        System.out.println(">> Verificando conceptos iniciales...");

        crearSiNoExiste("Cuota de Mantenimiento", "Cobro mensual por mantenimiento", true, 50.0);
        crearSiNoExiste("Servicio de Agua", "Consumo de agua mensual", true, 20.0);
        crearSiNoExiste("Servicio de Luz", "Consumo de energía eléctrica", true, 30.0);
        crearSiNoExiste("Vigilancia", "Servicio de seguridad", true, 15.0);
        crearSiNoExiste("Alquiler", "Pago por derecho de uso de puesto", true, 100.0);
        crearSiNoExiste("Cuota Extraordinaria", "Cobro eventual por gastos especiales", false, 80.0);
        crearSiNoExiste("Multa por Atraso", "Penalidad por pago fuera de fecha", false, 30.0);
    }

    public List<ConceptoDeuda> listarTodos() {
        return conceptoRepository.findAll();
    }

    public ConceptoDeuda buscarPorId(Long id) {
        return conceptoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("No existe el concepto de deuda con ID: " + id));
    }

    public ConceptoDeuda crearConcepto(ConceptoDeudaDTO dto) {
        validarDatos(dto);

        if (conceptoRepository.existsByNombre(dto.getNombre().trim())) {
            throw new RuntimeException("Ya existe un concepto de deuda con el nombre: " + dto.getNombre());
        }

        ConceptoDeuda concepto = ConceptoDeuda.builder()
                .nombre(dto.getNombre().trim())
                .descripcion(dto.getDescripcion())
                .recurrente(dto.isRecurrente())
                .montoSugerido(dto.getMontoSugerido())
                .build();

        return conceptoRepository.save(concepto);
    }

    public ConceptoDeuda actualizarConcepto(Long id, ConceptoDeudaDTO dto) {
        validarDatos(dto);

        ConceptoDeuda concepto = buscarPorId(id);
        String nuevoNombre = dto.getNombre().trim();

        if (!concepto.getNombre().equalsIgnoreCase(nuevoNombre)
                && conceptoRepository.existsByNombre(nuevoNombre)) {
            throw new RuntimeException("Ya existe otro concepto de deuda con el nombre: " + nuevoNombre);
        }

        concepto.setNombre(nuevoNombre);
        concepto.setDescripcion(dto.getDescripcion());
        concepto.setRecurrente(dto.isRecurrente());
        concepto.setMontoSugerido(dto.getMontoSugerido());

        return conceptoRepository.save(concepto);
    }

    public void eliminarConcepto(Long id) {
        ConceptoDeuda concepto = buscarPorId(id);

        /*
         * Si tu entidad ConceptoDeuda tiene una lista de deudas, se puede validar aquí.
         * Si no la tiene, deja solo delete.
         */
        conceptoRepository.delete(concepto);
    }

    private void crearSiNoExiste(String nombre, String descripcion, boolean recurrente, Double montoSugerido) {
        if (!conceptoRepository.existsByNombre(nombre)) {
            conceptoRepository.save(ConceptoDeuda.builder()
                    .nombre(nombre)
                    .descripcion(descripcion)
                    .recurrente(recurrente)
                    .montoSugerido(montoSugerido)
                    .build());
        }
    }

    private void validarDatos(ConceptoDeudaDTO dto) {
        if (dto.getNombre() == null || dto.getNombre().trim().isEmpty()) {
            throw new RuntimeException("El nombre del concepto es obligatorio");
        }

        if (dto.getMontoSugerido() == null || dto.getMontoSugerido() < 0) {
            throw new RuntimeException("El monto sugerido no puede ser negativo");
        }
    }
}