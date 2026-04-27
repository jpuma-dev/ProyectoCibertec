package com.cibertec.proyecto.services;

import com.cibertec.proyecto.repositories.DeudaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportService {

    private final DeudaRepository deudaRepository;

    public ByteArrayInputStream exportDeudasPorSocio() throws IOException {
        String[] columns = {"Socio", "DNI", "Total Pendiente (S/)"};
        List<Object[]> data = deudaRepository.reporteDeudasPendientesPorSocio();
        return generarExcel("Deudas por Socio", columns, data);
    }

    public ByteArrayInputStream exportMorosidadDinamica(Long socioId, Long puestoId, Long conceptoId) throws IOException {
        String[] columns = {"Socio", "DNI", "Puesto", "Concepto", "Monto", "Fecha"};
        List<Object[]> data = deudaRepository.reporteMorosidadDinamico(socioId, puestoId, conceptoId);
        return generarExcel("Reporte de Morosidad", columns, data);
    }

    private ByteArrayInputStream generarExcel(String sheetName, String[] columns, List<Object[]> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(sheetName);
            CellStyle headerCellStyle = crearEstiloEncabezado(workbook);

            // Crear encabezado
            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerCellStyle);
            }

            // Llenar datos
            int rowIdx = 1;
            for (Object[] rowData : data) {
                Row row = sheet.createRow(rowIdx++);
                for (int col = 0; col < rowData.length; col++) {
                    Object value = rowData[col];
                    if (value instanceof Double) {
                        row.createCell(col).setCellValue((Double) value);
                    } else {
                        row.createCell(col).setCellValue(String.valueOf(value != null ? value : ""));
                    }
                }
            }

            // Auto ajustar
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private CellStyle crearEstiloEncabezado(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}
