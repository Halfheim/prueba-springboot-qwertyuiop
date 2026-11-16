package tp.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Datos de un artículo para el selector del frontend: texto a mostrar,
 * descripción y precio de lista.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ArticuloOpcionDTO {
    private Long id;
    private String etiqueta;
    private String descripcion;
    private double precio;
}
