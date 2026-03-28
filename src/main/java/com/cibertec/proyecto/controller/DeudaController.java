package com.cibertec.proyecto.controller;

import com.cibertec.proyecto.dto.DeudaDTO;
import com.cibertec.proyecto.entity.Deuda;
import com.cibertec.proyecto.service.DeudaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deudas")
@RequiredArgsConstructor
public class DeudaController {

    private final DeudaService deudaService;

    @PostMapping
    public ResponseEntity<Deuda> crear(@RequestBody DeudaDTO dto) {
        return ResponseEntity.ok(deudaService.crearDeuda(dto));
    }
}