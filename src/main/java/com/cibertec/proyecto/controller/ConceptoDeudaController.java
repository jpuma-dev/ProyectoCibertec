package com.cibertec.proyecto.controller;

import com.cibertec.proyecto.dto.ConceptoDeudaDTO;
import com.cibertec.proyecto.entity.ConceptoDeuda;
import com.cibertec.proyecto.service.ConceptoDeudaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/conceptos-deuda")
@RequiredArgsConstructor
public class ConceptoDeudaController {

    private final ConceptoDeudaService conceptoDeudaService;

    @PostMapping
    public ResponseEntity<ConceptoDeuda> crear(@RequestBody ConceptoDeudaDTO dto) {
        return ResponseEntity.ok(conceptoDeudaService.crearConcepto(dto));
    }
}
