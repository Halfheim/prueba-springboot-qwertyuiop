package com.ejemplo.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.Date;

// Superclase de auditoría refactorizada con Lombok
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public abstract class AuditoriaApp extends EntityId {

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaAlta;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaBaja;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date fechaModificacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    protected Usuario usuarioCarga;

    @ManyToOne(fetch = FetchType.LAZY)
    protected Usuario usuarioBaja;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    protected Usuario usuarioModificacion;
}
