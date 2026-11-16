package tp.Controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tp.DTO.ArticuloOpcionDTO;
import tp.Entidad.Articulo;
import tp.Entidad.Cliente;
import tp.Entidad.CondicionIva;
import tp.Entidad.ListaPrecioArticulo;
import tp.Entidad.PuntoVenta;
import tp.Entidad.TipoMoneda;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Listados para los selectores del frontend. Solo lectura; ignora registros
 * dados de baja.
 */
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/api")
public class CatalogoRestController {

        public record OpcionDTO(Long id, String etiqueta) {
        }

        @PersistenceContext
        private EntityManager em;

        @GetMapping("/puntos-venta")
        public List<OpcionDTO> puntosVenta() {
                return em
                                .createQuery("select p from PuntoVenta p where p.fechaBaja is null order by p.numero",
                                                PuntoVenta.class)
                                .getResultList().stream()
                                .map(p -> new OpcionDTO(p.getId(), p.getNumero() + " - " + p.getDescripcion()))
                                .toList();
        }

        @GetMapping("/condiciones-iva")
        public List<OpcionDTO> condicionesIva() {
                return em
                                .createQuery("select c from CondicionIva c where c.fechaBaja is null order by c.denominacion",
                                                CondicionIva.class)
                                .getResultList().stream()
                                .map(c -> new OpcionDTO(c.getId(), c.getDenominacion()))
                                .toList();
        }

        @GetMapping("/clientes")
        public List<OpcionDTO> clientes() {
                return em.createQuery("select c from Cliente c where c.fechaBaja is null order by c.denominacion",
                                Cliente.class)
                                .getResultList().stream()
                                .map(c -> new OpcionDTO(c.getId(), c.getDenominacion() + " (" + c.getCuitCuil() + ")"))
                                .toList();
        }

        @GetMapping("/articulos")
        public List<ArticuloOpcionDTO> articulos() {
                List<ListaPrecioArticulo> lista = em.createQuery(
                                "select lpa from ListaPrecioArticulo lpa join fetch lpa.articulo a left join fetch a.marca "
                                                + "where a.fechaBaja is null order by a.denominacion, lpa.id",
                                ListaPrecioArticulo.class).getResultList();

                Map<Long, ArticuloOpcionDTO> porArticulo = new LinkedHashMap<>();
                for (ListaPrecioArticulo lpa : lista) {
                        Articulo a = lpa.getArticulo();
                        if (porArticulo.containsKey(a.getId()))
                                continue;
                        String marca = a.getMarca() != null ? " - " + a.getMarca().getDenominacion() : "";
                        porArticulo.put(a.getId(), new ArticuloOpcionDTO(
                                        a.getId(),
                                        a.getCodigo() + " - " + a.getDenominacion() + marca,
                                        a.getDenominacion(),
                                        lpa.getPrecioVenta()));
                }
                return new ArrayList<>(porArticulo.values());
        }

        @GetMapping("/tipos-moneda")
        public List<OpcionDTO> tiposMoneda() {
                return em
                                .createQuery("select m from TipoMoneda m where m.fechaBaja is null order by m.denominacion",
                                                TipoMoneda.class)
                                .getResultList().stream()
                                .map(m -> new OpcionDTO(m.getId(), m.getDenominacion() + " (" + m.getSimbolo() + ")"))
                                .toList();
        }
}