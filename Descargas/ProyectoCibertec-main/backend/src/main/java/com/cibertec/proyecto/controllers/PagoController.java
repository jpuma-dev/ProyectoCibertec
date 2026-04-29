package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.dtos.PagoDTO;
import com.cibertec.proyecto.dtos.PagoResponseDTO;
import com.cibertec.proyecto.services.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponseDTO>> crear(@Valid @RequestBody PagoDTO dto) {
        PagoResponseDTO response = pagoService.registrarPago(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Pago registrado exitosamente"));
    }

    @GetMapping("/flujo-caja-diario")
    public ResponseEntity<ApiResponse<Map<String, Object>>> flujoCajaDiario() {
        java.time.LocalDate hoy = java.time.LocalDate.now();
        return ResponseEntity.ok(ApiResponse.success(pagoService.resumenFlujoCaja(hoy, hoy), "Resumen de flujo de caja obtenido"));
    }
}
