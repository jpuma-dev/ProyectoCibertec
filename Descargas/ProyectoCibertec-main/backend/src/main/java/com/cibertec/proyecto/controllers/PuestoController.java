package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.dtos.PuestoDTO;
import com.cibertec.proyecto.dtos.PuestoResponseDTO;
import com.cibertec.proyecto.services.PuestoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/puestos")
@RequiredArgsConstructor
public class PuestoController {

    private final PuestoService puestoService;

    @PostMapping
    public ResponseEntity<ApiResponse<PuestoResponseDTO>> crear(@Valid @RequestBody PuestoDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(puestoService.crearPuesto(dto), "Puesto creado exitosamente"));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<PuestoResponseDTO>> actualizar(@PathVariable Long id, @Valid @RequestBody PuestoDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(puestoService.actualizarPuesto(id, dto), "Puesto actualizado exitosamente"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PuestoResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(puestoService.listarTodos(), "Lista de puestos obtenida"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        puestoService.eliminarPuesto(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Puesto eliminado exitosamente"));
    }
}
