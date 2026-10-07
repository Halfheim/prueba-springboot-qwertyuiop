package com.ejemplo.controller;

import com.ejemplo.dto.FacturaReporteDTO;
import com.ejemplo.service.FacturaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
public class FacturaRestController {

    private final FacturaService facturaService;

    public FacturaRestController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    /**
     * Endpoint GET principal que retorna la lista de FacturaReporteDTO en formato JSON con filtros opcionales.
     * Consigna Parte 3 (Items 3, 4 y 5)
     */
    @GetMapping
    public ResponseEntity<List<FacturaReporteDTO>> obtenerFacturas(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double montoMinimo
    ) {
        List<FacturaReporteDTO> facturas = facturaService.buscarFacturasFiltradas(fechaDesde, fechaHasta, estado, montoMinimo);
        return ResponseEntity.ok(facturas);
    }

    /**
     * Endpoint para exportar y descargar el reporte en formato PDF aplicando los mismos filtros.
     * Consigna Parte 2 (Página 5)
     */
    @GetMapping("/exportar/pdf")
    public ResponseEntity<byte[]> exportarPdf(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double montoMinimo
    ) {
        try {
            List<FacturaReporteDTO> facturas = facturaService.buscarFacturasFiltradas(fechaDesde, fechaHasta, estado, montoMinimo);
            byte[] pdfBytes = facturaService.generarReportePdf(facturas);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte_facturas.pdf\"")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Endpoint para exportar y descargar el reporte en formato Excel (texto tabulado TSV).
     * Consigna Parte 2 (Página 5)
     */
    @GetMapping("/exportar/excel")
    public ResponseEntity<byte[]> exportarExcel(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaDesde,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaHasta,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Double montoMinimo
    ) {
        List<FacturaReporteDTO> facturas = facturaService.buscarFacturasFiltradas(fechaDesde, fechaHasta, estado, montoMinimo);
        byte[] tsvBytes = facturaService.generarReporteExcelTsv(facturas);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"reporte_facturas.tsv\"")
                .contentType(MediaType.parseMediaType("text/tab-separated-values; charset=UTF-8"))
                .body(tsvBytes);
    }
}
