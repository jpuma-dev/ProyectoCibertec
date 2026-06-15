package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.dtos.DeudaDTO;
import com.cibertec.proyecto.dtos.DeudaResponseDTO;
import com.cibertec.proyecto.enums.EstadoDeuda;
import com.cibertec.proyecto.services.DeudaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/deudas")
@RequiredArgsConstructor
public class DeudaController {

    private final DeudaService deudaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeudaResponseDTO>>> listar(@RequestParam(required = false) EstadoDeuda estado) {
        return ResponseEntity.ok(ApiResponse.success(deudaService.listarPorEstado(estado), "Lista de deudas obtenida"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DeudaResponseDTO>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(deudaService.buscarPorId(id), "Deuda encontrada"));
    }

    @GetMapping("/resumen")
    public ResponseEntity<ApiResponse<Map<String, Object>>> resumen() {
        return ResponseEntity.ok(ApiResponse.success(deudaService.obtenerResumen(), "Resumen de deudas obtenido"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeudaResponseDTO>> crear(@Valid @RequestBody DeudaDTO dto) {
        DeudaResponseDTO response = deudaService.crearDeuda(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Deuda creada exitosamente"));
    }

    @PostMapping("/generar-masiva")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> generarMasiva(@Valid @RequestBody DeudaDTO dto) {
        int generadas = deudaService.generarDeudasMasivas(dto);
        return ResponseEntity.ok(ApiResponse.success(Map.of("generadas", generadas), "Deudas generadas masivamente con exito"));
    }
}
