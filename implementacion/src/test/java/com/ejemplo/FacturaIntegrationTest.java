package com.ejemplo;

import com.ejemplo.dto.FacturaReporteDTO;
import com.ejemplo.service.FacturaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class FacturaIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FacturaService facturaService;

    @Test
    void contextLoads() {
        assertThat(facturaService).isNotNull();
    }

    @Test
    void testBuscarFacturasSinFiltros() {
        List<FacturaReporteDTO> facturas = facturaService.buscarFacturasFiltradas(null, null, null, null);
        assertThat(facturas).isNotEmpty();

        // Verificar que exista una factura con "Consumidor Final" o con "Empresa Tech S.A."
        boolean tieneConsumidorFinal = facturas.stream()
                .anyMatch(f -> "Consumidor Final".equals(f.getClienteDenominacion()));
        assertThat(tieneConsumidorFinal).isTrue();
    }

    @Test
    void testEndpointGetFacturasJson() throws Exception {
        mockMvc.perform(get("/api/facturas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].numeroFactura").exists())
                .andExpect(jsonPath("$[0].importeTotal").exists());
    }

    @Test
    void testEndpointFiltroEstado() throws Exception {
        mockMvc.perform(get("/api/facturas")
                .param("estado", "EMITIDA")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void testEndpointFiltroMontoMinimo() throws Exception {
        mockMvc.perform(get("/api/facturas")
                .param("montoMinimo", "50000.0")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].importeTotal").value(org.hamcrest.Matchers.greaterThanOrEqualTo(50000.0)));
    }

    @Test
    void testEndpointExportarPdf() throws Exception {
        mockMvc.perform(get("/api/facturas/exportar/pdf"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"reporte_facturas.pdf\""))
                .andExpect(content().contentType(MediaType.APPLICATION_PDF));
    }

    @Test
    void testEndpointExportarExcelTsv() throws Exception {
        mockMvc.perform(get("/api/facturas/exportar/excel"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"reporte_facturas.tsv\""))
                .andExpect(content().contentTypeCompatibleWith("text/tab-separated-values"));
    }

    @Test
    void testArchivosGeneradosEnDisco() {
        File pdfFile = new File("reportes/reporte_facturas.pdf");
        File tsvFile = new File("reportes/reporte_facturas.tsv");

        assertThat(pdfFile.exists()).isTrue();
        assertThat(pdfFile.length()).isGreaterThan(0);

        assertThat(tsvFile.exists()).isTrue();
        assertThat(tsvFile.length()).isGreaterThan(0);
    }
}
