package tp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @PersistenceContext
    private EntityManager em;

    public List<FacturaReporteDTO> buscarFacturasFiltradas(Date fechaDesde, Date fechaHasta, String estado, Double montoMinimo) {
        return facturaRepository.buscarFacturasFiltradas(fechaDesde, fechaHasta, estado, montoMinimo);
    }

    @Transactional
    public FacturaReporteDTO crear(FacturaRequestDTO dto) {
        PuntoVenta puntoVenta = em.find(PuntoVenta.class, dto.getPuntoVentaId());
        if (puntoVenta == null) {
            throw new RecursoNoEncontradoException("No existe el punto de venta con id " + dto.getPuntoVentaId());
        }

        CondicionIva condicionIva = em.find(CondicionIva.class, dto.getCondicionIvaId());
        if (condicionIva == null) {
            throw new RecursoNoEncontradoException("No existe la condicion de IVA con id " + dto.getCondicionIvaId());
        }

        Cliente cliente = null;
        if (dto.getClienteId() != null) {
            cliente = em.find(Cliente.class, dto.getClienteId());
            if (cliente == null) {
                throw new RecursoNoEncontradoException("No existe el cliente con id " + dto.getClienteId());
            }
        }

        TipoMoneda tipoMoneda = null;
        if (dto.getTipoMonedaId() != null) {
            tipoMoneda = em.find(TipoMoneda.class, dto.getTipoMonedaId());
            if (tipoMoneda == null) {
                throw new RecursoNoEncontradoException("No existe el tipo de moneda con id " + dto.getTipoMonedaId());
            }
        }

        Usuario usuario = em.find(Usuario.class, 1L);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("No se encontro el usuario con id 1 para auditoria");
        }

        FacturaVenta factura = new FacturaVenta();
        factura.setNumero(dto.getNumero());
        factura.setFechaEmision(dto.getFechaEmision() != null ? dto.getFechaEmision() : new Date());
        factura.setPuntoVenta(puntoVenta);
        factura.setCondicionIva(condicionIva);
        factura.setCliente(cliente);
        factura.setTipoMoneda(tipoMoneda);
        factura.setEstado(dto.getEstado() != null && !dto.getEstado().trim().isEmpty() ? dto.getEstado() : "EMITIDA");
        factura.setObservaciones(dto.getObservaciones());
        factura.setUsuarioCarga(usuario);
        factura.setUsuarioModificacion(usuario);
        factura.setDetalles(new ArrayList<>());

        double totalFactura = 0.0;

        if (dto.getDetalles() != null) {
            for (FacturaDetalleRequestDTO detDto : dto.getDetalles()) {
                ListaPrecioArticulo lpa = facturaRepository.buscarListaPrecioPorArticuloId(detDto.getArticuloId())
                        .orElseThrow(() -> new RecursoNoEncontradoException("No existe el articulo con id " + detDto.getArticuloId()));

                FacturaVentaDetalle detalle = new FacturaVentaDetalle();
                detalle.setListaPrecioArticulo(lpa);
                detalle.setDescripcion(detDto.getDescripcion());
                detalle.setCantidad(detDto.getCantidad());
                detalle.setPrecioUnitario(detDto.getPrecioUnitario());
                detalle.setPorcentajeBonificacion(detDto.getPorcentajeBonificacion());

                // Neto bonificado + IVA 21%
                double bruto = detDto.getCantidad() * detDto.getPrecioUnitario();
                double neto = bruto * (1.0 - (detDto.getPorcentajeBonificacion() / 100.0));
                double iva = neto * 0.21;
                double subtotal = neto + iva;

                detalle.setImporteNeto(neto);
                detalle.setImporteIva(iva);
                detalle.setImporteSubtotal(subtotal);

                detalle.setFactura(factura);
                factura.getDetalles().add(detalle);

                totalFactura += subtotal;
            }
        }

        factura.setImporteTotal(totalFactura);
        factura.setImporteSaldo(totalFactura);

        FacturaVenta guardada = facturaRepository.guardar(factura);
        return mapearAReporteDTO(guardada);
    }

    private FacturaReporteDTO mapearAReporteDTO(FacturaVenta factura) {
        String clienteDenominacion = factura.getCliente() != null
                ? factura.getCliente().getDenominacion()
                : "Consumidor Final";
        String condicionIvaDenominacion = factura.getCondicionIva() != null
                ? factura.getCondicionIva().getDenominacion()
                : "";
        String puntoVentaDescripcion = factura.getPuntoVenta() != null
                ? factura.getPuntoVenta().getDescripcion()
                : "";
        long cantidadItems = factura.getDetalles() != null ? factura.getDetalles().size() : 0;

        return new FacturaReporteDTO(
                factura.getId(),
                factura.getNumero(),
                factura.getFechaEmision(),
                clienteDenominacion,
                condicionIvaDenominacion,
                puntoVentaDescripcion,
                factura.getImporteTotal(),
                cantidadItems,
                factura.getEstado()
        );
    }
}
