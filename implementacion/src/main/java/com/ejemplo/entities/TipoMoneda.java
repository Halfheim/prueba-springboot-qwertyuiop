package com.ejemplo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "TipoMoneda")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class TipoMoneda extends AuditoriaApp {

    @Column(nullable = false)
    private String codigoAfip;

    @Column(nullable = false)
    private String denominacion;

    @Column(nullable = false)
    private String simbolo;
}
