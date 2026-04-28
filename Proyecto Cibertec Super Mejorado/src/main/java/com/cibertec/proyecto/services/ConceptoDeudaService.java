package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.ConceptoDeudaDTO;
import com.cibertec.proyecto.entities.ConceptoDeuda;
import com.cibertec.proyecto.repositories.ConceptoDeudaRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConceptoDeudaService {

    private final ConceptoDeudaRepository conceptoRepository;

    /** Inserta conceptos base si no existen al iniciar la app */
    @PostConstruct
    public void init() {
        log.info("Verificando conceptos de deuda iniciales...");
        crearSiNoExiste("Cuota de Mantenimiento", "Cobro mensual por mantenimiento",  true,  50.0);
        crearSiNoExiste("Servicio de Agua",        "Consumo de agua mensual",          true,  20.0);
        crearSiNoExiste("Servicio de Luz",         "Consumo de energía eléctrica",     true,  30.0);
        crearSiNoExiste("Vigilancia",              "Servicio de seguridad",            true,  15.0);
        crearSiNoExiste("Alquiler",                "Pago por derecho de uso de puesto",true, 100.0);
    }

    public ConceptoDeuda crearConcepto(ConceptoDeudaDTO dto) {
        ConceptoDeuda concepto = ConceptoDeuda.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .recurrente(dto.isRecurrente())
                .montoSugerido(dto.getMontoSugerido())
                .build();
        return conceptoRepository.save(concepto);
    }

    public List<ConceptoDeuda> listarTodos() {
        return conceptoRepository.findAll();
    }

    private void crearSiNoExiste(String nombre, String desc, boolean recurrente, Double monto) {
        if (!conceptoRepository.existsByNombre(nombre)) {
            conceptoRepository.save(ConceptoDeuda.builder()
                    .nombre(nombre).descripcion(desc)
                    .recurrente(recurrente).montoSugerido(monto)
                    .build());
        }
    }
}
