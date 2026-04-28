package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.*;
import com.cibertec.proyecto.services.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponseDTO>> registrar(@Valid @RequestBody PagoDTO dto) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.registrarPago(dto), "Pago registrado exitosamente"));
    }

    @GetMapping("/flujo-caja-diario")
    public ResponseEntity<ApiResponse<Map<String, Object>>> flujoCajaDiario() {
        LocalDate hoy = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.success(pagoService.resumenFlujoCaja(hoy, hoy), "Flujo de caja diario"));
    }
}
