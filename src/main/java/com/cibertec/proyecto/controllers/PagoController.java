package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.dtos.PagoDTO;
import com.cibertec.proyecto.dtos.PagoResponseDTO;
import com.cibertec.proyecto.services.PagoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/pagos")
@RequiredArgsConstructor
public class PagoController {

    private final PagoService pagoService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<PagoResponseDTO>>> listar() {
        return ResponseEntity.ok(ApiResponse.success(pagoService.listarTodos(), "Lista de pagos obtenida"));
    }

    @GetMapping("/deuda/{deudaId}")
    public ResponseEntity<ApiResponse<List<PagoResponseDTO>>> listarPorDeuda(@PathVariable Long deudaId) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.listarPorDeuda(deudaId), "Pagos de la deuda obtenidos"));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<PagoResponseDTO>> crear(@Valid @RequestBody PagoDTO dto) {
        PagoResponseDTO response = pagoService.registrarPago(dto);
        return ResponseEntity.ok(ApiResponse.success(response, "Pago registrado exitosamente"));
    }

    @GetMapping("/flujo-caja-diario")
    public ResponseEntity<ApiResponse<Map<String, Object>>> flujoCajaDiario() {
        LocalDate hoy = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.success(pagoService.resumenFlujoCaja(hoy, hoy), "Resumen de flujo de caja obtenido"));
    }

    @GetMapping("/flujo-caja")
    public ResponseEntity<ApiResponse<Map<String, Object>>> flujoCaja(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return ResponseEntity.ok(ApiResponse.success(pagoService.resumenFlujoCaja(startDate, endDate), "Resumen de flujo de caja obtenido"));
    }
}
