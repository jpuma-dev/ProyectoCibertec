package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.dtos.SocioDTO;
import com.cibertec.proyecto.dtos.SocioResponseDTO;
import com.cibertec.proyecto.services.ISocioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/socios")
@RequiredArgsConstructor
public class SocioController {

    private final ISocioService socioService;

    @PostMapping
    public ResponseEntity<ApiResponse<SocioResponseDTO>> crear(@Valid @RequestBody SocioDTO dto) {
        SocioResponseDTO response = socioService.crearSocio(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Socio creado exitosamente"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SocioResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(socioService.listarSocios(), "Lista de socios obtenida"));
    }

    @GetMapping("/paginado")
    public ResponseEntity<ApiResponse<Page<SocioResponseDTO>>> listarPaginado(@PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(socioService.listarSociosPaginado(pageable), "Pagina de socios obtenida"));
    }

    @GetMapping("/buscar")
    public ResponseEntity<ApiResponse<Page<SocioResponseDTO>>> buscar(@RequestParam String term, @PageableDefault(size = 10) Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(socioService.buscarSocios(term, pageable), "Resultados de busqueda obtenidos"));
    }

    @GetMapping("/{dni}")
    public ResponseEntity<ApiResponse<SocioResponseDTO>> obtenerPorDni(@PathVariable String dni) {
        return ResponseEntity.ok(ApiResponse.success(socioService.obtenerPorDni(dni), "Socio encontrado"));
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<ApiResponse<Void>> eliminarPorDni(@PathVariable String dni) {
        socioService.eliminarSocioPorDni(dni);
        return ResponseEntity.ok(ApiResponse.success(null, "Socio eliminado exitosamente"));
    }

    @PutMapping("/{dni}")
    public ResponseEntity<ApiResponse<SocioResponseDTO>> actualizar(@PathVariable String dni, @Valid @RequestBody SocioDTO dto) {
        SocioResponseDTO response = socioService.actualizarSocio(dni, dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Socio actualizado exitosamente"));
    }
}
