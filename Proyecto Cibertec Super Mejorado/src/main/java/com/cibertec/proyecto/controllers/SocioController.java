package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.*;
import com.cibertec.proyecto.services.ISocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/socios")
@RequiredArgsConstructor
public class SocioController {

    private final ISocioService socioService;

    /** POST /api/socios → Crear socio */
    @PostMapping
    public ResponseEntity<ApiResponse<SocioResponseDTO>> crear(
            @Valid @RequestBody SocioDTO dto) {
        return ResponseEntity.ok(
                ApiResponse.success(socioService.crearSocio(dto), "Socio creado exitosamente"));
    }

    /** GET /api/socios → Listar todos */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SocioResponseDTO>>> listar() {
        return ResponseEntity.ok(
                ApiResponse.success(socioService.listarSocios(), "Lista de socios obtenida"));
    }

    /** GET /api/socios/paginado → Listar paginado */
    @GetMapping("/paginado")
    public ResponseEntity<ApiResponse<Page<SocioResponseDTO>>> listarPaginado(
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success(socioService.listarSociosPaginado(pageable), "Página de socios obtenida"));
    }

    /** GET /api/socios/buscar?term=xxx → Buscar por nombre o DNI */
    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<Page<SocioResponseDTO>>> buscar(
            @RequestParam String term,
            @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success(socioService.buscarSocios(term, pageable), "Resultados de búsqueda"));
    }

    /** GET /api/socios/{dni} → Obtener por DNI */
    @GetMapping("/{dni}")
    public ResponseEntity<ApiResponse<SocioResponseDTO>> obtenerPorDni(
            @PathVariable String dni) {
        return ResponseEntity.ok(
                ApiResponse.success(socioService.obtenerPorDni(dni), "Socio encontrado"));
    }

    /** PUT /api/socios/{dni} → Actualizar */
    @PutMapping("/{dni}")
    public ResponseEntity<ApiResponse<SocioResponseDTO>> actualizar(
            @PathVariable String dni,
            @Valid @RequestBody SocioDTO dto) {
        return ResponseEntity.ok(
                ApiResponse.success(socioService.actualizarSocio(dni, dto), "Socio actualizado exitosamente"));
    }

    /** DELETE /api/socios/{dni} → Eliminar */
    @DeleteMapping("/{dni}")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable String dni) {
        socioService.eliminarSocioPorDni(dni);
        return ResponseEntity.ok(
                ApiResponse.success(null, "Socio eliminado exitosamente"));
    }
}
