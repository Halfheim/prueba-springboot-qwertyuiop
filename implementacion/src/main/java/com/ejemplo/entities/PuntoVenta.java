package com.ejemplo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "PuntoVenta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class PuntoVenta extends AuditoriaApp {

    @Column(nullable = false)
    private int numero;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private String tipoEmision;

    @Column(nullable = false)
    private String domicilioComercial;
}
