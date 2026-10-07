package com.ejemplo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "FacturaVentaDetalle")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true, exclude = {"factura"})
@ToString(exclude = {"factura"})
public class FacturaVentaDetalle extends EntityId {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private FacturaVenta factura;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ListaPrecioArticulo listaPrecioArticulo;

    private String descripcion;

    @Column(nullable = false)
    private double cantidad;

    @Column(nullable = false)
    private double precioUnitario;

    private double porcentajeBonificacion;
    private double importeNeto;
    private double importeIva;

    @Column(nullable = false)
    private double importeSubtotal;
}
