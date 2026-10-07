package com.ejemplo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class Cliente extends AuditoriaApp {

    @Column(nullable = false)
    private String cuitCuil;

    @Column(nullable = false)
    private String denominacion;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Contacto contacto;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private Domicilio domicilio;
}
