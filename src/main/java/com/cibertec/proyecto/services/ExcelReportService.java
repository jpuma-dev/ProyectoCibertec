package com.cibertec.proyecto.services;

import com.cibertec.proyecto.repositories.DeudaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportService {

    private final DeudaRepository deudaRepository;

    public ByteArrayInputStream exportDeudasPorSocio() throws IOException {
        return generarExcel("Deudas por Socio",
                new String[]{"Socio", "DNI", "Total Pendiente (S/)"},
                deudaRepository.reporteDeudasPendientesPorSocio());
    }

    public ByteArrayInputStream exportMorosidadDinamica(Long socioId, Long puestoId, Long conceptoId) throws IOException {
        return generarExcel("Reporte de Morosidad",
                new String[]{"Socio", "DNI", "Puesto", "Concepto", "Monto", "Fecha"},
                deudaRepository.reporteMorosidadDinamico(socioId, puestoId, conceptoId));
    }

    private ByteArrayInputStream generarExcel(String sheetName, String[] cols, List<Object[]> data) throws IOException {
        try (Workbook wb = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = wb.createSheet(sheetName);
            CellStyle headerStyle = wb.createCellStyle();
            Font font = wb.createFont();
            font.setBold(true);
            headerStyle.setFont(font);

            Row header = sheet.createRow(0);
            for (int i = 0; i < cols.length; i++) {
                Cell c = header.createCell(i);
                c.setCellValue(cols[i]);
                c.setCellStyle(headerStyle);
            }

            int rowIdx = 1;
            for (Object[] row : data) {
                Row r = sheet.createRow(rowIdx++);
                for (int i = 0; i < row.length; i++) {
                    Object v = row[i];
                    if (v instanceof Double d) r.createCell(i).setCellValue(d);
                    else r.createCell(i).setCellValue(v != null ? v.toString() : "");
                }
            }
            for (int i = 0; i < cols.length; i++) sheet.autoSizeColumn(i);
            wb.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
