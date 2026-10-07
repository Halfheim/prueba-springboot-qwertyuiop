package com.ejemplo.entities;

import jakarta.persistence.*;
import lombok.*;

// Superclase base refactorizada con Lombok
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class EntityId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;
}
