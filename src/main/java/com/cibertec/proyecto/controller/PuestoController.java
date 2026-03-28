package com.cibertec.proyecto.controller;

import com.cibertec.proyecto.dto.PuestoDTO;
import com.cibertec.proyecto.entity.Puesto;
import com.cibertec.proyecto.service.PuestoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/puestos")
@RequiredArgsConstructor
public class PuestoController {

    private final PuestoService puestoService;

    @PostMapping
    public ResponseEntity<Puesto> crear(@RequestBody PuestoDTO dto) {
        return ResponseEntity.ok(puestoService.crearPuesto(dto));
    }
}
