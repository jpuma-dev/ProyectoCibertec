package com.cibertec.proyecto.service;

import com.cibertec.proyecto.dto.ConceptoDeudaDTO;
import com.cibertec.proyecto.entity.ConceptoDeuda;
import com.cibertec.proyecto.repository.ConceptoDeudaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConceptoDeudaService {

    private final ConceptoDeudaRepository conceptoRepository;

    public ConceptoDeuda crearConcepto(ConceptoDeudaDTO dto) {

        ConceptoDeuda concepto = ConceptoDeuda.builder()
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .build();

        return conceptoRepository.save(concepto);
    }
}
