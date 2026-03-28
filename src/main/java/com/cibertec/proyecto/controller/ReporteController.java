package com.cibertec.proyecto.controller;

import com.cibertec.proyecto.service.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final PagoService pagoService;

    @GetMapping("/flujo-caja-diario")
    public ResponseEntity<Map<String, Object>> flujoCajaDiario() {
        return ResponseEntity.ok(pagoService.resumenFlujoCajaDelDia());
    }
}
