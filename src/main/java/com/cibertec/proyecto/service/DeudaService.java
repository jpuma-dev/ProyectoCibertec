package com.cibertec.proyecto.service;

import com.cibertec.proyecto.dto.DeudaDTO;
import com.cibertec.proyecto.entity.ConceptoDeuda;
import com.cibertec.proyecto.entity.Deuda;
import com.cibertec.proyecto.entity.Puesto;
import com.cibertec.proyecto.enums.EstadoDeuda;
import com.cibertec.proyecto.repository.ConceptoDeudaRepository;
import com.cibertec.proyecto.repository.DeudaRepository;
import com.cibertec.proyecto.repository.PuestoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeudaService {

    private final DeudaRepository deudaRepository;
    private final PuestoRepository puestoRepository;
    private final ConceptoDeudaRepository conceptoRepository;

    @Transactional
    public Deuda crearDeuda(DeudaDTO dto) {

        if (dto.getMonto() == null || dto.getMonto() <= 0) {
            throw new RuntimeException("El monto debe ser mayor a 0");
        }

        if (dto.getPuestoIds() == null || dto.getPuestoIds().isEmpty()) {
            throw new RuntimeException("Debe seleccionar al menos un puesto");
        }

        ConceptoDeuda concepto = conceptoRepository.findById(dto.getConceptoId())
                .orElseThrow(() -> new RuntimeException("Concepto no encontrado"));

        List<Puesto> puestos = puestoRepository.findAllById(dto.getPuestoIds());

        if (puestos.size() != dto.getPuestoIds().size()) {
            throw new RuntimeException("Uno o más puestos no fueron encontrados");
        }

        Deuda deuda = new Deuda();
        deuda.setMonto(dto.getMonto());
        deuda.setFecha(dto.getFecha());
        deuda.setEstado(EstadoDeuda.PENDIENTE);
        deuda.setConcepto(concepto);
        deuda.setPuestos(puestos);

        return deudaRepository.save(deuda);
    }
}