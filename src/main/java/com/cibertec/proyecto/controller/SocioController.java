package com.cibertec.proyecto.controller;

import com.cibertec.proyecto.dto.SocioDTO;
import com.cibertec.proyecto.dto.SocioResponseDTO;
import com.cibertec.proyecto.entity.Socio;
import com.cibertec.proyecto.service.SocioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/socios")
@RequiredArgsConstructor
public class SocioController {

    private final SocioService socioService;

    @PostMapping
    public ResponseEntity<Socio> crear(@RequestBody SocioDTO dto) {
        return ResponseEntity.ok(socioService.crearSocio(dto));
    }

    @GetMapping
    public ResponseEntity<List<SocioResponseDTO>> listar() {
        return ResponseEntity.ok(socioService.listarSocios());
    }

    @DeleteMapping("/{dni}")
    public ResponseEntity<Void> eliminarPorDni(@PathVariable String dni) {
        socioService.eliminarSocioPorDni(dni);
        return ResponseEntity.noContent().build();
    }
}
