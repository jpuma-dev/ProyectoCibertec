package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.dtos.ConceptoDeudaDTO;
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

    private final ConceptoDeudaService conceptoDeudaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConceptoDeuda>>> listar() {
        List<ConceptoDeuda> lista = conceptoDeudaService.listarTodos();

        return ResponseEntity.ok(
                ApiResponse.success(lista, "Conceptos de deuda listados correctamente")
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ConceptoDeuda>> buscarPorId(@PathVariable Long id) {
        ConceptoDeuda concepto = conceptoDeudaService.buscarPorId(id);

        return ResponseEntity.ok(
                ApiResponse.success(concepto, "Concepto de deuda encontrado")
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ConceptoDeuda>> crear(@Valid @RequestBody ConceptoDeudaDTO dto) {
        ConceptoDeuda creado = conceptoDeudaService.crearConcepto(dto);

        return ResponseEntity.ok(
                ApiResponse.success(creado, "Concepto de deuda creado exitosamente")
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ConceptoDeuda>> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ConceptoDeudaDTO dto
    ) {
        ConceptoDeuda actualizado = conceptoDeudaService.actualizarConcepto(id, dto);

        return ResponseEntity.ok(
                ApiResponse.success(actualizado, "Concepto de deuda actualizado exitosamente")
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        conceptoDeudaService.eliminarConcepto(id);

        return ResponseEntity.ok(
                ApiResponse.success(null, "Concepto de deuda eliminado exitosamente")
        );
    }
}