package com.ejemplo.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Contacto")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString
public class Contacto extends EntityId {

    private String email;
    private String telefono;
    private String celular;
}
