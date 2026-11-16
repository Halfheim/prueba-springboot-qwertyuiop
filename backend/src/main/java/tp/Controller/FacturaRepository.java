package tp.Controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import tp.DTO.FacturaReporteDTO;
import tp.Entidad.FacturaVenta;
import tp.Entidad.ListaPrecioArticulo;

import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class FacturaRepository {

    @PersistenceContext
    private EntityManager em;

    public List<FacturaReporteDTO> buscarFacturasFiltradas(Date fechaDesde, Date fechaHasta, String estado,
            Double montoMinimo) {
        StringBuilder jpql = new StringBuilder(
                "SELECT new tp.DTO.FacturaReporteDTO(" +
                        "   f.id, f.numero, f.fechaEmision, COALESCE(c.denominacion, 'Consumidor Final'), " +
                        "   ci.denominacion, pv.descripcion, f.importeTotal, COUNT(d), f.estado" +
                        ") " +
                        "FROM FacturaVenta f " +
                        "LEFT JOIN f.cliente c " +
                        "JOIN f.condicionIva ci " +
                        "JOIN f.puntoVenta pv " +
                        "JOIN f.detalles d " +
                        "WHERE 1=1 ");

        Map<String, Object> params = new HashMap<>();

        if (fechaDesde != null) {
            jpql.append("AND f.fechaEmision >= :fechaDesde ");
            params.put("fechaDesde", fechaDesde);
        }
        if (fechaHasta != null) {
            jpql.append("AND f.fechaEmision <= :fechaHasta ");
            params.put("fechaHasta", fechaHasta);
        }
        if (estado != null && !estado.trim().isEmpty()) {
            jpql.append("AND f.estado = :estado ");
            params.put("estado", estado);
        }
        if (montoMinimo != null) {
            jpql.append("AND f.importeTotal >= :montoMinimo ");
            params.put("montoMinimo", montoMinimo);
        }

        jpql.append(
                "GROUP BY f.id, f.numero, f.fechaEmision, c.denominacion, ci.denominacion, pv.descripcion, f.importeTotal, f.estado");

        TypedQuery<FacturaReporteDTO> query = em.createQuery(jpql.toString(), FacturaReporteDTO.class);
        params.forEach(query::setParameter);

        return query.getResultList();
    }

    public Optional<FacturaVenta> buscarPorId(Long id) {
        return Optional.ofNullable(em.find(FacturaVenta.class, id));
    }

    public Optional<ListaPrecioArticulo> buscarListaPrecioPorArticuloId(Long articuloId) {
        List<ListaPrecioArticulo> resultados = em.createQuery(
                "SELECT lpa FROM ListaPrecioArticulo lpa WHERE lpa.articulo.id = :articuloId ORDER BY lpa.id",
                ListaPrecioArticulo.class).setParameter("articuloId", articuloId).getResultList();

        if (resultados.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(resultados.get(0));
    }

    public FacturaVenta guardar(FacturaVenta factura) {
        if (factura.getId() == null) {
            em.persist(factura);
            return factura;
        }
        return em.merge(factura);
    }

    public void eliminar(FacturaVenta factura) {
        em.remove(factura);
    }
}
