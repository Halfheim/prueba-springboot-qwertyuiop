package com.ejemplo.service;

import com.ejemplo.dto.FacturaReporteDTO;
import com.ejemplo.repository.FacturaRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public FacturaService(FacturaRepository facturaRepository) {
        this.facturaRepository = facturaRepository;
    }

    public List<FacturaReporteDTO> buscarFacturasFiltradas(Date fechaDesde, Date fechaHasta, String estado, Double montoMinimo) {
        return facturaRepository.buscarFacturasFiltradas(fechaDesde, fechaHasta, estado, montoMinimo);
    }

    public byte[] generarReportePdf(List<FacturaReporteDTO> facturas) throws DocumentException {
        Document document = new Document(PageSize.A4.rotate()); // Horizontal para mejor visualización
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // Título del Reporte
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, Color.DARK_GRAY);
        Paragraph title = new Paragraph("Reporte Ejecutivo de Ventas (Facturas)", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Tabla con 7 columnas según FacturaReporteDTO
        PdfPTable table = new PdfPTable(7);
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1.5f, 2.0f, 3.5f, 3.0f, 2.5f, 2.0f, 1.5f});

        // Encabezados
        String[] headers = {
            "N° Factura", "Fecha Emisión", "Cliente", "Condición IVA", "Punto Venta", "Importe Total", "Cant. Ítems"
        };

        Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setBackgroundColor(new Color(41, 128, 185));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setPadding(6);
            table.addCell(cell);
        }

        // Filas de datos
        Font rowFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Color.BLACK);
        boolean alternate = false;
        Color altColor = new Color(245, 247, 250);

        for (FacturaReporteDTO f : facturas) {
            Color rowBg = alternate ? altColor : Color.WHITE;

            addCell(table, String.valueOf(f.getNumeroFactura()), rowFont, Element.ALIGN_CENTER, rowBg);
            addCell(table, f.getFechaEmision() != null ? dateFormat.format(f.getFechaEmision()) : "", rowFont, Element.ALIGN_CENTER, rowBg);
            addCell(table, f.getClienteDenominacion() != null ? f.getClienteDenominacion() : "Consumidor Final", rowFont, Element.ALIGN_LEFT, rowBg);
            addCell(table, f.getCondicionIva() != null ? f.getCondicionIva() : "", rowFont, Element.ALIGN_LEFT, rowBg);
            addCell(table, f.getPuntoVentaDescripcion() != null ? f.getPuntoVentaDescripcion() : "", rowFont, Element.ALIGN_LEFT, rowBg);
            addCell(table, String.format("$ %.2f", f.getImporteTotal()), rowFont, Element.ALIGN_RIGHT, rowBg);
            addCell(table, String.valueOf(f.getCantidadItems()), rowFont, Element.ALIGN_CENTER, rowBg);

            alternate = !alternate;
        }

        document.add(table);
        document.close();

        return out.toByteArray();
    }

    private void addCell(PdfPTable table, String text, Font font, int alignment, Color bgColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(5);
        cell.setBackgroundColor(bgColor);
        table.addCell(cell);
    }

    public byte[] generarReporteExcelTsv(List<FacturaReporteDTO> facturas) {
        StringBuilder tsv = new StringBuilder();
        // BOM UTF-8 para apertura correcta en Excel
        tsv.append("\uFEFF");
        tsv.append("Nro Factura\tFecha Emision\tCliente\tCondicion IVA\tPunto de Venta\tImporte Total\tCantidad Items\r\n");

        for (FacturaReporteDTO f : facturas) {
            tsv.append(f.getNumeroFactura() != null ? f.getNumeroFactura() : "").append("\t");
            tsv.append(f.getFechaEmision() != null ? dateFormat.format(f.getFechaEmision()) : "").append("\t");
            tsv.append(f.getClienteDenominacion() != null ? f.getClienteDenominacion().replace("\t", " ") : "Consumidor Final").append("\t");
            tsv.append(f.getCondicionIva() != null ? f.getCondicionIva().replace("\t", " ") : "").append("\t");
            tsv.append(f.getPuntoVentaDescripcion() != null ? f.getPuntoVentaDescripcion().replace("\t", " ") : "").append("\t");
            tsv.append(f.getImporteTotal()).append("\t");
            tsv.append(f.getCantidadItems()).append("\r\n");
        }

        return tsv.toString().getBytes(StandardCharsets.UTF_8);
    }

    public void guardarReportesEnDisco(List<FacturaReporteDTO> facturas) {
        try {
            File dir = new File("reportes");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            byte[] pdfBytes = generarReportePdf(facturas);
            try (FileOutputStream fos = new FileOutputStream(new File(dir, "reporte_facturas.pdf"))) {
                fos.write(pdfBytes);
            }

            byte[] tsvBytes = generarReporteExcelTsv(facturas);
            try (FileOutputStream fos = new FileOutputStream(new File(dir, "reporte_facturas.tsv"))) {
                fos.write(tsvBytes);
            }

            System.out.println(">> Reportes guardados exitosamente en la carpeta 'reportes/'.");
        } catch (Exception e) {
            System.err.println("Error al guardar reportes en disco: " + e.getMessage());
        }
    }
}
