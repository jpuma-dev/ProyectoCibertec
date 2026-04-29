package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.services.DeudaService;
import com.cibertec.proyecto.services.ExcelReportService;
import com.cibertec.proyecto.services.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final PagoService pagoService;
    private final DeudaService deudaService;
    private final ExcelReportService excelReportService;

    @GetMapping("/flujo-caja")
    public ResponseEntity<ApiResponse<Map<String, Object>>> flujoCaja(
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate startDate,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate endDate) {
        
        java.time.LocalDate start = (startDate != null) ? startDate : java.time.LocalDate.now();
        java.time.LocalDate end = (endDate != null) ? endDate : java.time.LocalDate.now();
        
        return ResponseEntity.ok(ApiResponse.success(pagoService.resumenFlujoCaja(start, end), "Resumen de flujo de caja obtenido"));
    }

    @GetMapping("/deudas/socio")
    public ResponseEntity<ApiResponse<List<Object[]>>> deudasPorSocio() {
        return ResponseEntity.ok(ApiResponse.success(deudaService.obtenerReporteDeudasPorSocio(), "Reporte de deudas por socio obtenido"));
    }

    @GetMapping("/deudas/socio/export/excel")
    public ResponseEntity<org.springframework.core.io.InputStreamResource> exportDeudasSocioExcel() throws java.io.IOException {
        java.io.ByteArrayInputStream in = excelReportService.exportDeudasPorSocio();
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte-deudas-socios.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new org.springframework.core.io.InputStreamResource(in));
    }

    @GetMapping("/morosidad")
    public ResponseEntity<ApiResponse<List<Object[]>>> reporteMorosidad(
            @RequestParam(required = false) Long socioId,
            @RequestParam(required = false) Long puestoId,
            @RequestParam(required = false) Long conceptoId) {
        return ResponseEntity.ok(ApiResponse.success(deudaService.obtenerReporteMorosidad(socioId, puestoId, conceptoId), "Reporte de morosidad obtenido"));
    }

    @GetMapping("/deudas/export/excel")
    public ResponseEntity<org.springframework.core.io.InputStreamResource> exportDeudasExcel(
            @RequestParam(required = false) Long socioId,
            @RequestParam(required = false) Long puestoId,
            @RequestParam(required = false) Long conceptoId) throws java.io.IOException {
        java.io.ByteArrayInputStream in = excelReportService.exportMorosidadDinamica(socioId, puestoId, conceptoId);
        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte-morosidad.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new org.springframework.core.io.InputStreamResource(in));
    }
}
