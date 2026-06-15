package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.dtos.PuestoDTO;
import com.cibertec.proyecto.dtos.PuestoResponseDTO;
import com.cibertec.proyecto.services.PuestoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PuestoResponseDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(puestoService.buscarPorId(id), "Puesto encontrado"));
    }

    @GetMapping("/disponibles")
    public ResponseEntity<ApiResponse<List<PuestoResponseDTO>>> listarDisponibles() {
        return ResponseEntity.ok(ApiResponse.success(puestoService.listarDisponibles(), "Puestos disponibles obtenidos"));
    }

    @GetMapping("/socio/{socioId}")
    public ResponseEntity<ApiResponse<List<PuestoResponseDTO>>> listarPorSocio(@PathVariable Long socioId) {
        return ResponseEntity.ok(ApiResponse.success(puestoService.listarPorSocio(socioId), "Puestos del socio obtenidos"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        puestoService.eliminarPuesto(id);
        return ResponseEntity.ok(ApiResponse.success(null, "Puesto eliminado exitosamente"));
    }
}
