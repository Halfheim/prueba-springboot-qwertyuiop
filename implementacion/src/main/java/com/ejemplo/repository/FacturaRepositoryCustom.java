package com.ejemplo.repository;

import com.ejemplo.dto.FacturaReporteDTO;
import java.util.Date;
import java.util.List;

public interface FacturaRepositoryCustom {

    List<FacturaReporteDTO> buscarFacturasFiltradas(Date fechaDesde, Date fechaHasta, String estado, Double montoMinimo);
}
