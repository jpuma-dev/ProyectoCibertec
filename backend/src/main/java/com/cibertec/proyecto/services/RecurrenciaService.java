package com.cibertec.proyecto.services;

import com.cibertec.proyecto.entities.ConceptoDeuda;
import com.cibertec.proyecto.entities.Puesto;
import com.cibertec.proyecto.repositories.ConceptoDeudaRepository;
import com.cibertec.proyecto.repositories.PuestoRepository;
import com.cibertec.proyecto.dtos.DeudaDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecurrenciaService {

    private final ConceptoDeudaRepository conceptoRepository;
    private final PuestoRepository puestoRepository;
    private final DeudaService deudaService;

    @Scheduled(cron = "0 0 0 1 * ?")
    public void generarCargosRecurrentesMensuales() {
        log.info("Iniciando generación de cargos recurrentes del mes: {}", LocalDate.now().getMonth());
        
        List<ConceptoDeuda> conceptosRecurrentes = conceptoRepository.findAll().stream()
                .filter(ConceptoDeuda::isRecurrente)
                .collect(Collectors.toList());

        List<Puesto> puestosConSocio = puestoRepository.findAll().stream()
                .filter(p -> p.getSocio() != null)
                .collect(Collectors.toList());

        if (puestosConSocio.isEmpty()) {
            log.warn("No hay puestos con socios asignados para generar cargos recurrentes.");
            return;
        }

        for (ConceptoDeuda concepto : conceptosRecurrentes) {
            log.info("Generando cargos para el concepto: {}", concepto.getNombre());
            
            DeudaDTO dto = new DeudaDTO();
            dto.setConceptoId(concepto.getId());
            dto.setMonto(concepto.getMontoSugerido() != null ? concepto.getMontoSugerido() : 0.0);
            dto.setFecha(LocalDate.now());
            dto.setPuestoIds(puestosConSocio.stream().map(Puesto::getId).collect(Collectors.toList()));

            try {
                deudaService.generarDeudasMasivas(dto);
                log.info("Cargos generados exitosamente para concepto: {}", concepto.getNombre());
            } catch (Exception e) {
                log.error("Error al generar cargos recurrentes para concepto {}: {}", concepto.getNombre(), e.getMessage());
            }
        }
    }
}
