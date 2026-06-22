package com.cibertec.proyecto.services;

import com.cibertec.proyecto.repositories.DeudaRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExcelReportService {

    private static final DateTimeFormatter REPORT_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final DeudaRepository deudaRepository;

    public ByteArrayInputStream exportDeudasPorSocio() throws IOException {
        List<Object[]> data = deudaRepository.reporteDeudasPendientesPorSocio();

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Styles styles = crearEstilos(workbook);
            ReportMetrics metrics = calcularMetricas(data);

            Sheet resumen = workbook.createSheet("Resumen Ejecutivo");
            Sheet detalle = workbook.createSheet("Detalle de Deudas");

            crearHojaResumen(resumen, styles, data, metrics);
            crearHojaDetalle(detalle, styles, data, metrics.totalPendiente);

            workbook.setActiveSheet(0);
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    public ByteArrayInputStream exportMorosidadDinamica(Long socioId, Long puestoId, Long conceptoId) throws IOException {
        String[] columns = {"Socio", "DNI", "Puesto", "Concepto", "Monto", "Fecha"};
        List<Object[]> data = deudaRepository.reporteMorosidadDinamico(socioId, puestoId, conceptoId);
        return generarExcelGenerico("Reporte de Morosidad", columns, data);
    }

    private void crearHojaResumen(Sheet sheet, Styles styles, List<Object[]> data, ReportMetrics metrics) {
        configurarHoja(sheet);
        sheet.setColumnWidth(0, 2400);
        sheet.setColumnWidth(1, 7600);
        sheet.setColumnWidth(2, 3600);
        sheet.setColumnWidth(3, 4600);
        sheet.setColumnWidth(4, 4100);
        sheet.setColumnWidth(5, 5200);
        sheet.setColumnWidth(6, 5200);
        sheet.setColumnWidth(7, 3600);

        Row brandRow = sheet.createRow(0);
        brandRow.setHeightInPoints(18);
        crearCeldaTexto(brandRow, 0, "MERCADO - GESTION DE PAGOS", styles.brand);
        combinar(sheet, 0, 0, 0, 7);

        Row titleRow = sheet.createRow(1);
        titleRow.setHeightInPoints(34);
        crearCeldaTexto(titleRow, 0, "Reporte ejecutivo de deudas por socio", styles.title);
        combinar(sheet, 1, 1, 0, 7);

        Row metaRow = sheet.createRow(2);
        metaRow.setHeightInPoints(22);
        crearCeldaTexto(metaRow, 0, "Generado: " + LocalDateTime.now().format(REPORT_DATE), styles.meta);
        combinar(sheet, 2, 2, 0, 2);
        crearCeldaTexto(metaRow, 3, "Fuente: Sistema Mercado", styles.metaRight);
        combinar(sheet, 2, 2, 3, 7);

        crearTarjetaResumen(sheet, styles, 4, 0, "Registros", metrics.registros, null, styles.kpiBlue);
        crearTarjetaResumen(sheet, styles, 4, 2, "Total pendiente", metrics.totalPendiente, "money", styles.kpiRed);
        crearTarjetaResumen(sheet, styles, 4, 4, "Promedio por socio", metrics.promedio, "money", styles.kpiAmber);
        crearTarjetaResumen(sheet, styles, 4, 6, "Mayor deuda", metrics.mayorDeuda, "money", styles.kpiGreen);

        Row sectionRow = sheet.createRow(8);
        sectionRow.setHeightInPoints(24);
        crearCeldaTexto(sectionRow, 0, "Distribucion por prioridad", styles.section);
        combinar(sheet, 8, 8, 0, 3);
        crearCeldaTexto(sectionRow, 4, "Acciones recomendadas", styles.section);
        combinar(sheet, 8, 8, 4, 6);

        Row priorityHeader = sheet.createRow(9);
        priorityHeader.setHeightInPoints(24);
        crearCeldaTexto(priorityHeader, 0, "Prioridad", styles.tableHeader);
        crearCeldaTexto(priorityHeader, 1, "Casos", styles.tableHeader);
        crearCeldaTexto(priorityHeader, 2, "Monto", styles.tableHeader);
        crearCeldaTexto(priorityHeader, 3, "% cartera", styles.tableHeader);
        crearCeldaTexto(priorityHeader, 4, "Plan de accion", styles.tableHeader);
        combinar(sheet, 9, 9, 4, 6);

        crearPrioridadResumen(sheet, styles, 10, "Alta", metrics.altaCantidad, metrics.altaMonto, styles.priorityHigh);
        crearPrioridadResumen(sheet, styles, 11, "Media", metrics.mediaCantidad, metrics.mediaMonto, styles.priorityMedium);
        crearPrioridadResumen(sheet, styles, 12, "Baja", metrics.bajaCantidad, metrics.bajaMonto, styles.priorityLow);

        crearCeldaTexto(getOrCreateRow(sheet, 10), 4, "1. Contactar primero a socios con prioridad alta.", styles.note);
        combinar(sheet, 10, 10, 4, 6);
        crearCeldaTexto(getOrCreateRow(sheet, 11), 4, "2. Revisar compromisos de pago y registrar abonos.", styles.note);
        combinar(sheet, 11, 11, 4, 6);
        crearCeldaTexto(getOrCreateRow(sheet, 12), 4, "3. Exportar este reporte al cierre de cada jornada.", styles.note);
        combinar(sheet, 12, 12, 4, 6);

        Row topHeader = sheet.createRow(15);
        topHeader.setHeightInPoints(24);
        crearCeldaTexto(topHeader, 0, "Top de socios con mayor deuda", styles.section);
        combinar(sheet, 15, 15, 0, 6);

        String[] topColumns = {"Nro.", "Socio", "DNI", "Monto", "% cartera", "Prioridad", "Gestion"};
        crearCabeceraTabla(sheet, styles, 17, topColumns);

        List<Object[]> topData = data.stream()
                .sorted(Comparator.comparingDouble((Object[] row) -> numero(row, 2)).reversed())
                .limit(8)
                .toList();

        int rowIndex = 18;
        for (int i = 0; i < topData.size(); i++) {
            Object[] rowData = topData.get(i);
            double monto = numero(rowData, 2);
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(26);

            crearCeldaNumero(row, 0, i + 1, styles.center);
            crearCeldaTexto(row, 1, texto(rowData, 0), styles.text);
            crearCeldaTexto(row, 2, texto(rowData, 1), styles.center);
            crearCeldaNumero(row, 3, monto, styles.money);
            crearCeldaNumero(row, 4, porcentaje(monto, metrics.totalPendiente), styles.percent);
            crearCeldaTexto(row, 5, prioridad(monto), estiloPrioridad(styles, monto));
            crearCeldaTexto(row, 6, accion(monto), styles.textMuted);
        }

        sheet.createFreezePane(0, 17);
        configurarImpresion(sheet);
    }

    private void crearHojaDetalle(Sheet sheet, Styles styles, List<Object[]> data, double totalPendiente) {
        configurarHoja(sheet);

        sheet.setColumnWidth(0, 2200);
        sheet.setColumnWidth(1, 7800);
        sheet.setColumnWidth(2, 4200);
        sheet.setColumnWidth(3, 5200);
        sheet.setColumnWidth(4, 4200);
        sheet.setColumnWidth(5, 4400);
        sheet.setColumnWidth(6, 7600);

        Row brandRow = sheet.createRow(0);
        brandRow.setHeightInPoints(18);
        crearCeldaTexto(brandRow, 0, "MERCADO - GESTION DE PAGOS", styles.brand);
        combinar(sheet, 0, 0, 0, 6);

        Row titleRow = sheet.createRow(1);
        titleRow.setHeightInPoints(32);
        crearCeldaTexto(titleRow, 0, "Detalle de deudas pendientes por socio", styles.title);
        combinar(sheet, 1, 1, 0, 6);

        Row metaRow = sheet.createRow(2);
        metaRow.setHeightInPoints(22);
        crearCeldaTexto(metaRow, 0, "Generado: " + LocalDateTime.now().format(REPORT_DATE), styles.meta);
        combinar(sheet, 2, 2, 0, 2);
        crearCeldaTexto(metaRow, 3, "Reporte para seguimiento administrativo", styles.metaRight);
        combinar(sheet, 2, 2, 3, 6);

        int headerRowIndex = 5;
        String[] columns = {"Nro.", "Socio", "DNI", "Total pendiente", "% cartera", "Prioridad", "Accion sugerida"};
        crearCabeceraTabla(sheet, styles, headerRowIndex, columns);

        int rowIndex = headerRowIndex + 1;
        List<Object[]> orderedData = data.stream()
                .sorted(Comparator.comparingDouble((Object[] row) -> numero(row, 2)).reversed())
                .toList();

        for (int i = 0; i < orderedData.size(); i++) {
            Object[] rowData = orderedData.get(i);
            double monto = numero(rowData, 2);
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(27);

            crearCeldaNumero(row, 0, i + 1, styles.center);
            crearCeldaTexto(row, 1, texto(rowData, 0), styles.text);
            crearCeldaTexto(row, 2, texto(rowData, 1), styles.center);
            crearCeldaNumero(row, 3, monto, styles.money);
            crearCeldaNumero(row, 4, porcentaje(monto, totalPendiente), styles.percent);
            crearCeldaTexto(row, 5, prioridad(monto), estiloPrioridad(styles, monto));
            crearCeldaTexto(row, 6, accion(monto), styles.textMuted);
        }

        Row totalRow = sheet.createRow(rowIndex);
        totalRow.setHeightInPoints(30);
        crearCeldaTexto(totalRow, 0, "TOTAL GENERAL", styles.totalLabel);
        combinar(sheet, rowIndex, rowIndex, 0, 2);
        crearCeldaNumero(totalRow, 3, totalPendiente, styles.totalMoney);
        crearCeldaNumero(totalRow, 4, 1, styles.totalPercent);
        crearCeldaTexto(totalRow, 5, "", styles.totalLabel);
        crearCeldaTexto(totalRow, 6, data.size() + " registro(s)", styles.totalLabel);

        aplicarAutoFiltro(sheet, headerRowIndex, rowIndex, columns.length - 1);
        sheet.createFreezePane(0, headerRowIndex + 1);
        configurarImpresion(sheet);
    }

    private ByteArrayInputStream generarExcelGenerico(String title, String[] columns, List<Object[]> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet(title);
            Styles styles = crearEstilos(workbook);

            configurarHoja(sheet);
            crearTituloGenerico(sheet, styles, title);

            int headerRowIndex = 5;
            crearCabeceraTabla(sheet, styles, headerRowIndex, columns);

            int rowIndex = headerRowIndex + 1;
            for (Object[] rowData : data) {
                Row row = sheet.createRow(rowIndex++);
                row.setHeightInPoints(25);

                for (int col = 0; col < columns.length; col++) {
                    Object value = col < rowData.length ? rowData[col] : "";
                    if (value instanceof Number) {
                        crearCeldaNumero(row, col, ((Number) value).doubleValue(), col == 4 ? styles.money : styles.number);
                    } else {
                        crearCeldaTexto(row, col, value != null ? String.valueOf(value) : "", styles.text);
                    }
                }
            }

            aplicarAutoFiltro(sheet, headerRowIndex, Math.max(headerRowIndex + 1, rowIndex - 1), columns.length - 1);
            sheet.createFreezePane(0, headerRowIndex + 1);
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, Math.min(Math.max(sheet.getColumnWidth(i) + 900, 3600), 9000));
            }
            configurarImpresion(sheet);

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }

    private void crearTituloGenerico(Sheet sheet, Styles styles, String title) {
        Row brandRow = sheet.createRow(0);
        brandRow.setHeightInPoints(18);
        crearCeldaTexto(brandRow, 0, "MERCADO - GESTION DE PAGOS", styles.brand);
        combinar(sheet, 0, 0, 0, 5);

        Row titleRow = sheet.createRow(1);
        titleRow.setHeightInPoints(32);
        crearCeldaTexto(titleRow, 0, title, styles.title);
        combinar(sheet, 1, 1, 0, 5);

        Row metaRow = sheet.createRow(2);
        metaRow.setHeightInPoints(22);
        crearCeldaTexto(metaRow, 0, "Generado: " + LocalDateTime.now().format(REPORT_DATE), styles.meta);
        combinar(sheet, 2, 2, 0, 5);
    }

    private void crearTarjetaResumen(Sheet sheet, Styles styles, int rowIndex, int col, String label, double value,
                                     String type, CellStyle valueStyle) {
        Row labelRow = getOrCreateRow(sheet, rowIndex);
        Row valueRow = getOrCreateRow(sheet, rowIndex + 1);
        labelRow.setHeightInPoints(21);
        valueRow.setHeightInPoints(32);

        crearCeldaTexto(labelRow, col, label, styles.kpiLabel);
        crearCeldaTexto(labelRow, col + 1, "", styles.kpiLabel);
        combinar(sheet, rowIndex, rowIndex, col, col + 1);

        if ("money".equals(type)) {
            crearCeldaNumero(valueRow, col, value, valueStyle);
        } else {
            crearCeldaNumero(valueRow, col, value, valueStyle);
        }
        crearCeldaTexto(valueRow, col + 1, "", valueStyle);
        combinar(sheet, rowIndex + 1, rowIndex + 1, col, col + 1);
    }

    private void crearPrioridadResumen(Sheet sheet, Styles styles, int rowIndex, String label, int cantidad, double monto,
                                       CellStyle priorityStyle) {
        Row row = getOrCreateRow(sheet, rowIndex);
        row.setHeightInPoints(25);
        crearCeldaTexto(row, 0, label, priorityStyle);
        crearCeldaNumero(row, 1, cantidad, styles.center);
        crearCeldaNumero(row, 2, monto, styles.money);
        crearCeldaNumero(row, 3, cantidad > 0 ? porcentaje(monto, montoTotalResumen(sheet)) : 0, styles.percent);
    }

    private double montoTotalResumen(Sheet sheet) {
        Row row = sheet.getRow(5);
        if (row == null || row.getCell(2) == null) {
            return 0;
        }
        return row.getCell(2).getNumericCellValue();
    }

    private void crearCabeceraTabla(Sheet sheet, Styles styles, int rowIndex, String[] columns) {
        Row headerRow = sheet.createRow(rowIndex);
        headerRow.setHeightInPoints(27);

        for (int col = 0; col < columns.length; col++) {
            Cell cell = headerRow.createCell(col);
            cell.setCellValue(columns[col]);
            cell.setCellStyle(styles.tableHeader);
        }
    }

    private void configurarHoja(Sheet sheet) {
        sheet.setDisplayGridlines(false);
        sheet.setPrintGridlines(false);
        sheet.setDefaultRowHeightInPoints(22);
        sheet.setMargin(Sheet.LeftMargin, 0.25);
        sheet.setMargin(Sheet.RightMargin, 0.25);
        sheet.setMargin(Sheet.TopMargin, 0.45);
        sheet.setMargin(Sheet.BottomMargin, 0.45);
    }

    private void configurarImpresion(Sheet sheet) {
        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setFitWidth((short) 1);
        sheet.getPrintSetup().setFitHeight((short) 0);
        sheet.setFitToPage(true);
        sheet.setAutobreaks(true);
    }

    private void aplicarAutoFiltro(Sheet sheet, int firstRow, int lastRow, int lastCol) {
        if (lastRow >= firstRow) {
            sheet.setAutoFilter(new CellRangeAddress(firstRow, lastRow, 0, lastCol));
        }
    }

    private void combinar(Sheet sheet, int firstRow, int lastRow, int firstCol, int lastCol) {
        sheet.addMergedRegion(new CellRangeAddress(firstRow, lastRow, firstCol, lastCol));
    }

    private Row getOrCreateRow(Sheet sheet, int rowIndex) {
        Row row = sheet.getRow(rowIndex);
        return row != null ? row : sheet.createRow(rowIndex);
    }

    private void crearCeldaTexto(Row row, int col, String value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void crearCeldaNumero(Row row, int col, double value, CellStyle style) {
        Cell cell = row.createCell(col);
        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private String texto(Object[] rowData, int index) {
        if (index >= rowData.length || rowData[index] == null) {
            return "";
        }
        return String.valueOf(rowData[index]);
    }

    private double numero(Object[] rowData, int index) {
        if (index >= rowData.length || rowData[index] == null) {
            return 0;
        }

        Object value = rowData[index];
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }

        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    private double porcentaje(double monto, double total) {
        if (total <= 0) {
            return 0;
        }
        return monto / total;
    }

    private String prioridad(double monto) {
        if (monto >= 500) {
            return "Alta";
        }
        if (monto >= 100) {
            return "Media";
        }
        return "Baja";
    }

    private String accion(double monto) {
        if (monto >= 500) {
            return "Contacto y compromiso de pago";
        }
        if (monto >= 100) {
            return "Seguimiento regular";
        }
        return "Control mensual";
    }

    private CellStyle estiloPrioridad(Styles styles, double monto) {
        if (monto >= 500) {
            return styles.priorityHigh;
        }
        if (monto >= 100) {
            return styles.priorityMedium;
        }
        return styles.priorityLow;
    }

    private ReportMetrics calcularMetricas(List<Object[]> data) {
        ReportMetrics metrics = new ReportMetrics();
        metrics.registros = data.size();

        for (Object[] row : data) {
            double monto = numero(row, 2);
            metrics.totalPendiente += monto;
            metrics.mayorDeuda = Math.max(metrics.mayorDeuda, monto);

            if (monto >= 500) {
                metrics.altaCantidad++;
                metrics.altaMonto += monto;
            } else if (monto >= 100) {
                metrics.mediaCantidad++;
                metrics.mediaMonto += monto;
            } else {
                metrics.bajaCantidad++;
                metrics.bajaMonto += monto;
            }
        }

        metrics.promedio = metrics.registros == 0 ? 0 : metrics.totalPendiente / metrics.registros;
        return metrics;
    }

    private Styles crearEstilos(Workbook workbook) {
        Styles styles = new Styles();

        styles.brand = estilo(workbook, IndexedColors.WHITE, IndexedColors.GREY_50_PERCENT, true, 9, HorizontalAlignment.LEFT, false);
        styles.title = estilo(workbook, IndexedColors.DARK_BLUE, IndexedColors.WHITE, true, 16, HorizontalAlignment.LEFT, false);
        styles.meta = estilo(workbook, IndexedColors.WHITE, IndexedColors.GREY_50_PERCENT, false, 9, HorizontalAlignment.LEFT, false);
        styles.metaRight = estilo(workbook, IndexedColors.WHITE, IndexedColors.GREY_50_PERCENT, false, 9, HorizontalAlignment.RIGHT, false);
        styles.section = estilo(workbook, IndexedColors.BLUE_GREY, IndexedColors.WHITE, true, 11, HorizontalAlignment.LEFT, true);
        styles.note = estilo(workbook, IndexedColors.WHITE, IndexedColors.BLACK, false, 10, HorizontalAlignment.LEFT, true);

        styles.kpiLabel = estilo(workbook, IndexedColors.PALE_BLUE, IndexedColors.DARK_BLUE, true, 9, HorizontalAlignment.CENTER, true);
        styles.kpiBlue = estiloNumero(workbook, IndexedColors.WHITE, IndexedColors.DARK_BLUE, true, 17, HorizontalAlignment.CENTER, "#,##0", true);
        styles.kpiRed = estiloNumero(workbook, IndexedColors.WHITE, IndexedColors.DARK_RED, true, 17, HorizontalAlignment.CENTER, "\"S/\" #,##0.00", true);
        styles.kpiAmber = estiloNumero(workbook, IndexedColors.WHITE, IndexedColors.BROWN, true, 17, HorizontalAlignment.CENTER, "\"S/\" #,##0.00", true);
        styles.kpiGreen = estiloNumero(workbook, IndexedColors.WHITE, IndexedColors.GREEN, true, 17, HorizontalAlignment.CENTER, "\"S/\" #,##0.00", true);

        styles.tableHeader = estilo(workbook, IndexedColors.DARK_BLUE, IndexedColors.WHITE, true, 10, HorizontalAlignment.CENTER, true);
        styles.text = estilo(workbook, IndexedColors.WHITE, IndexedColors.BLACK, false, 10, HorizontalAlignment.LEFT, true);
        styles.textMuted = estilo(workbook, IndexedColors.WHITE, IndexedColors.GREY_50_PERCENT, false, 10, HorizontalAlignment.LEFT, true);
        styles.center = estilo(workbook, IndexedColors.WHITE, IndexedColors.BLACK, false, 10, HorizontalAlignment.CENTER, true);
        styles.number = estiloNumero(workbook, IndexedColors.WHITE, IndexedColors.BLACK, false, 10, HorizontalAlignment.RIGHT, "#,##0.00", true);
        styles.money = estiloNumero(workbook, IndexedColors.WHITE, IndexedColors.BLACK, false, 10, HorizontalAlignment.RIGHT, "\"S/\" #,##0.00", true);
        styles.percent = estiloNumero(workbook, IndexedColors.WHITE, IndexedColors.BLACK, false, 10, HorizontalAlignment.RIGHT, "0.00%", true);

        styles.priorityHigh = estilo(workbook, IndexedColors.ROSE, IndexedColors.DARK_RED, true, 10, HorizontalAlignment.CENTER, true);
        styles.priorityMedium = estilo(workbook, IndexedColors.LIGHT_YELLOW, IndexedColors.BROWN, true, 10, HorizontalAlignment.CENTER, true);
        styles.priorityLow = estilo(workbook, IndexedColors.LIGHT_GREEN, IndexedColors.GREEN, true, 10, HorizontalAlignment.CENTER, true);

        styles.totalLabel = estilo(workbook, IndexedColors.GREY_25_PERCENT, IndexedColors.BLACK, true, 10, HorizontalAlignment.LEFT, true);
        styles.totalMoney = estiloNumero(workbook, IndexedColors.GREY_25_PERCENT, IndexedColors.BLACK, true, 11, HorizontalAlignment.RIGHT, "\"S/\" #,##0.00", true);
        styles.totalPercent = estiloNumero(workbook, IndexedColors.GREY_25_PERCENT, IndexedColors.BLACK, true, 11, HorizontalAlignment.RIGHT, "0.00%", true);

        return styles;
    }

    private CellStyle estilo(Workbook workbook, IndexedColors fill, IndexedColors color, boolean bold, int size,
                             HorizontalAlignment alignment, boolean border) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(bold);
        font.setFontHeightInPoints((short) size);
        font.setColor(color.getIndex());
        style.setFont(font);
        style.setFillForegroundColor(fill.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(alignment);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        if (border) {
            aplicarBorde(style);
        }
        return style;
    }

    private CellStyle estiloNumero(Workbook workbook, IndexedColors fill, IndexedColors color, boolean bold, int size,
                                   HorizontalAlignment alignment, String format, boolean border) {
        CellStyle style = estilo(workbook, fill, color, bold, size, alignment, border);
        style.setDataFormat(workbook.createDataFormat().getFormat(format));
        return style;
    }

    private void aplicarBorde(CellStyle style) {
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setRightBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setBottomBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setLeftBorderColor(IndexedColors.GREY_25_PERCENT.getIndex());
    }

    private static class ReportMetrics {
        private int registros;
        private double totalPendiente;
        private double promedio;
        private double mayorDeuda;
        private int altaCantidad;
        private int mediaCantidad;
        private int bajaCantidad;
        private double altaMonto;
        private double mediaMonto;
        private double bajaMonto;
    }

    private static class Styles {
        private CellStyle brand;
        private CellStyle title;
        private CellStyle meta;
        private CellStyle metaRight;
        private CellStyle section;
        private CellStyle note;
        private CellStyle kpiLabel;
        private CellStyle kpiBlue;
        private CellStyle kpiRed;
        private CellStyle kpiAmber;
        private CellStyle kpiGreen;
        private CellStyle tableHeader;
        private CellStyle text;
        private CellStyle textMuted;
        private CellStyle center;
        private CellStyle number;
        private CellStyle money;
        private CellStyle percent;
        private CellStyle priorityHigh;
        private CellStyle priorityMedium;
        private CellStyle priorityLow;
        private CellStyle totalLabel;
        private CellStyle totalMoney;
        private CellStyle totalPercent;
    }
}
