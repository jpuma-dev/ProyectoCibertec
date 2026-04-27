package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.DeudaDTO;
import com.cibertec.proyecto.entities.*;
import com.cibertec.proyecto.repositories.*;
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

    /** Genera cargos recurrentes el día 1 de cada mes a medianoche */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void generarCargosRecurrentesMensuales() {
        log.info("Generando cargos recurrentes del mes: {}", LocalDate.now().getMonth());

        List<ConceptoDeuda> recurrentes = conceptoRepository.findAll().stream()
                .filter(ConceptoDeuda::isRecurrente)
                .collect(Collectors.toList());

        List<Puesto> puestosConSocio = puestoRepository.findAll().stream()
                .filter(p -> p.getSocio() != null)
                .collect(Collectors.toList());

        if (puestosConSocio.isEmpty()) {
            log.warn("No hay puestos con socios para generar cargos.");
            return;
        }

        for (ConceptoDeuda concepto : recurrentes) {
            try {
                DeudaDTO dto = new DeudaDTO();
                dto.setConceptoId(concepto.getId());
                dto.setMonto(concepto.getMontoSugerido() != null ? concepto.getMontoSugerido() : 0.0);
                dto.setFecha(LocalDate.now());
                dto.setPuestoIds(puestosConSocio.stream().map(Puesto::getId).collect(Collectors.toList()));
                deudaService.generarDeudasMasivas(dto);
                log.info("Cargos generados para: {}", concepto.getNombre());
            } catch (Exception e) {
                log.error("Error generando cargo para {}: {}", concepto.getNombre(), e.getMessage());
            }
        }
    }
}
