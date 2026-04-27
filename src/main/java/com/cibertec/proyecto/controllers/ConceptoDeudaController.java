package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.*;
import com.cibertec.proyecto.entities.ConceptoDeuda;
import com.cibertec.proyecto.services.ConceptoDeudaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conceptos-deuda")
@RequiredArgsConstructor
public class ConceptoDeudaController {

    private final ConceptoDeudaService service;

    @PostMapping
    public ResponseEntity<ApiResponse<ConceptoDeuda>> crear(@Valid @RequestBody ConceptoDeudaDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(service.crearConcepto(dto), "Concepto creado"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConceptoDeuda>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(service.listarTodos(), "Lista de conceptos obtenida"));
    }
}
