package com.ejemplo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ListaPrecioArticulo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class ListaPrecioArticulo extends AuditoriaApp {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private ListaPrecio listaPrecio;

    @Column(nullable = false)
    private double precioVenta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Articulo articulo;
}
