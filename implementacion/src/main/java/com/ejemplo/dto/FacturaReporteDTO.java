package com.ejemplo.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

// DTO para reportes ejecutivos de ventas proyectado directamente por JPQL (Consigna Parte 2)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FacturaReporteDTO {

    private Long numeroFactura;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "America/Argentina/Buenos_Aires")
    private Date fechaEmision;

    private String clienteDenominacion;
    private String condicionIva;
    private String puntoVentaDescripcion;
    private double importeTotal;
    private long cantidadItems;
}
