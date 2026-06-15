package com.cibertec.proyecto.controllers;

import com.cibertec.proyecto.dtos.ApiResponse;
import com.cibertec.proyecto.services.DeudaService;
import com.cibertec.proyecto.services.ExcelReportService;
import com.cibertec.proyecto.services.PagoService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final PagoService pagoService;
    private final DeudaService deudaService;
    private final ExcelReportService excelReportService;

    @GetMapping("/flujo-caja-diario")
    public ResponseEntity<ApiResponse<Map<String, Object>>> flujoCajaDiario() {
        LocalDate hoy = LocalDate.now();
        return ResponseEntity.ok(ApiResponse.success(pagoService.resumenFlujoCaja(hoy, hoy), "Resumen de flujo de caja diario obtenido"));
    }

    @GetMapping("/flujo-caja")
    public ResponseEntity<ApiResponse<Map<String, Object>>> flujoCaja(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        LocalDate start = startDate != null ? startDate : LocalDate.now();
        LocalDate end = endDate != null ? endDate : LocalDate.now();

        return ResponseEntity.ok(ApiResponse.success(pagoService.resumenFlujoCaja(start, end), "Resumen de flujo de caja obtenido"));
    }

    @GetMapping("/deudas/socio")
    public ResponseEntity<ApiResponse<List<Object[]>>> deudasPorSocio() {
        return ResponseEntity.ok(ApiResponse.success(deudaService.obtenerReporteDeudasPorSocio(), "Reporte de deudas por socio obtenido"));
    }

    @GetMapping("/deudas/socio/export/excel")
    public ResponseEntity<InputStreamResource> exportDeudasSocioExcel() throws IOException {
        return crearRespuestaExcel(excelReportService.exportDeudasPorSocio(), "reporte-deudas-socios.xlsx");
    }

    @GetMapping("/morosidad")
    public ResponseEntity<ApiResponse<List<Object[]>>> reporteMorosidad(
            @RequestParam(required = false) Long socioId,
            @RequestParam(required = false) Long puestoId,
            @RequestParam(required = false) Long conceptoId) {
        return ResponseEntity.ok(ApiResponse.success(deudaService.obtenerReporteMorosidad(socioId, puestoId, conceptoId), "Reporte de morosidad obtenido"));
    }

    @GetMapping("/deudas/export/excel")
    public ResponseEntity<InputStreamResource> exportDeudasExcel(
            @RequestParam(required = false) Long socioId,
            @RequestParam(required = false) Long puestoId,
            @RequestParam(required = false) Long conceptoId) throws IOException {
        return crearRespuestaExcel(excelReportService.exportMorosidadDinamica(socioId, puestoId, conceptoId), "reporte-morosidad.xlsx");
    }

    private ResponseEntity<InputStreamResource> crearRespuestaExcel(ByteArrayInputStream in, String fileName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName);

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
