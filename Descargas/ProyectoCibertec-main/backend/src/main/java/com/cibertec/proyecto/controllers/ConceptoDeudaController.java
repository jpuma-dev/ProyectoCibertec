package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.dtos.ConceptoDeudaDTO;
import com.cibertec.proyecto.entities.ConceptoDeuda;
import com.cibertec.proyecto.services.ConceptoDeudaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/conceptos-deuda")
@RequiredArgsConstructor
public class ConceptoDeudaController {

    private final ConceptoDeudaService conceptoDeudaService;

    @PostMapping
    public ResponseEntity<ApiResponse<ConceptoDeuda>> crear(@RequestBody ConceptoDeudaDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(conceptoDeudaService.crearConcepto(dto), "Concepto de deuda creado exitosamente"));
    }

    @GetMapping
    public ResponseEntity<?> listar() {
        java.util.List<ConceptoDeuda> lista = conceptoDeudaService.listarTodos();
        // Enviamos un objeto que tiene el campo data que busca Angular
        return ResponseEntity.ok(java.util.Map.of("data", lista));
    }
}
