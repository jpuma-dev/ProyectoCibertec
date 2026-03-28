package com.cibertec.proyecto.controller;

import com.cibertec.proyecto.dto.PagoDTO;
import com.cibertec.proyecto.entity.Pago;
import com.cibertec.proyecto.service.PagoService;
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
    public ResponseEntity<Pago> crear(@RequestBody PagoDTO dto) {
        return ResponseEntity.ok(pagoService.registrarPago(dto));
    }

    @GetMapping("/flujo-caja-diario")
    public ResponseEntity<Map<String, Object>> flujoCajaDiario() {
        return ResponseEntity.ok(pagoService.resumenFlujoCajaDelDia());
    }
}
