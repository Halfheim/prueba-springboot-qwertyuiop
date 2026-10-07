package com.ejemplo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CondicionIva")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class CondicionIva extends AuditoriaApp {

    @Column(nullable = false)
    private int codigoAfip;

    @Column(nullable = false)
    private String denominacion;
}
