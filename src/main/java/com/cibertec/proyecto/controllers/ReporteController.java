package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.services.*;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final PagoService pagoService;
    private final DeudaService deudaService;
    private final ExcelReportService excelReportService;

    @GetMapping("/flujo-caja")
    public ResponseEntity<ApiResponse<Map<String, Object>>> flujoCaja(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDate from = startDate != null ? startDate : LocalDate.now();
        LocalDate to   = endDate   != null ? endDate   : LocalDate.now();
        return ResponseEntity.ok(ApiResponse.success(pagoService.resumenFlujoCaja(from, to), "Flujo de caja"));
    }

    @GetMapping("/deudas/socio")
    public ResponseEntity<ApiResponse<List<Object[]>>> deudasPorSocio() {
        return ResponseEntity.ok(ApiResponse.success(
                deudaService.obtenerReporteDeudasPorSocio(), "Reporte de deudas por socio"));
    }

    @GetMapping("/deudas/socio/export/excel")
    public ResponseEntity<InputStreamResource> exportDeudasSocioExcel() throws IOException {
        var in = excelReportService.exportDeudasPorSocio();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte-deudas-socios.xlsx");
        return ResponseEntity.ok().headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/morosidad")
    public ResponseEntity<ApiResponse<List<Object[]>>> morosidad(
            @RequestParam(required = false) Long socioId,
            @RequestParam(required = false) Long puestoId,
            @RequestParam(required = false) Long conceptoId) {
        return ResponseEntity.ok(ApiResponse.success(
                deudaService.obtenerReporteMorosidad(socioId, puestoId, conceptoId), "Reporte de morosidad"));
    }

    @GetMapping("/deudas/export/excel")
    public ResponseEntity<InputStreamResource> exportDeudasExcel(
            @RequestParam(required = false) Long socioId,
            @RequestParam(required = false) Long puestoId,
            @RequestParam(required = false) Long conceptoId) throws IOException {
        var in = excelReportService.exportMorosidadDinamica(socioId, puestoId, conceptoId);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte-morosidad.xlsx");
        return ResponseEntity.ok().headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
