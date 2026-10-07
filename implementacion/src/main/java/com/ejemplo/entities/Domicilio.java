package com.ejemplo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Domicilio")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class Domicilio extends EntityId {

    private String nombreCalle;
    private String numeroCalle;
}
