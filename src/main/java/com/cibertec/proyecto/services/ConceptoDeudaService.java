package com.cibertec.proyecto.services;

import com.cibertec.proyecto.dtos.ConceptoDeudaDTO;
import com.cibertec.proyecto.entities.ConceptoDeuda;
import com.cibertec.proyecto.repositories.ConceptoDeudaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConceptoDeudaService {

    private final ConceptoDeudaRepository conceptoRepository;

    @jakarta.annotation.PostConstruct
    public void init() {
        System.out.println(">> Verificando conceptos iniciales...");
        crearSiNoExiste("Cuota de Mantenimiento", "Cobro mensual por mantenimiento", true, 50.0);
        crearSiNoExiste("Servicio de Agua", "Consumo de agua mensual", true, 20.0);
        crearSiNoExiste("Servicio de Luz", "Consumo de energía eléctrica", true, 30.0);
        crearSiNoExiste("Vigilancia", "Servicio de seguridad", true, 15.0);
        crearSiNoExiste("Alquiler", "Pago por derecho de uso de puesto", true, 100.0);
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

    public java.util.List<ConceptoDeuda> listarTodos() {
        return conceptoRepository.findAll();
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
}
