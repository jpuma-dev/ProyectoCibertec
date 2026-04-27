package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.dtos.DeudaDTO;
import com.cibertec.proyecto.dtos.DeudaResponseDTO;
import com.cibertec.proyecto.services.DeudaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deudas")
@RequiredArgsConstructor
public class DeudaController {

    private final DeudaService deudaService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<DeudaResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(deudaService.listarTodas(), "Lista de deudas obtenida"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<DeudaResponseDTO>> crear(@Valid @RequestBody DeudaDTO dto) {
        DeudaResponseDTO response = deudaService.crearDeuda(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Deuda creada exitosamente"));
    }

    @PostMapping("/generar-masiva")
    public ResponseEntity<ApiResponse<Void>> generarMasiva(@Valid @RequestBody DeudaDTO dto) {
        deudaService.generarDeudasMasivas(dto);
        return ResponseEntity.ok(ApiResponse.success(null, "Deudas generadas masivamente con éxito"));
    }
}
