package tp.Controller;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import tp.DTO.FacturaDetalleRequestDTO;
import tp.DTO.FacturaReporteDTO;
import tp.DTO.FacturaRequestDTO;
import tp.Entidad.Cliente;
import tp.Entidad.CondicionIva;
import tp.Entidad.FacturaVenta;
import tp.Entidad.FacturaVentaDetalle;
import tp.Entidad.ListaPrecioArticulo;
import tp.Entidad.PuntoVenta;
import tp.Entidad.TipoMoneda;
import tp.Entidad.Usuario;
import tp.Exceptions.RecursoNoEncontradoException;
import tp.ClasesArca.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private WsfeClient wsfeClient;

    // Si es false, no se llama a ARCA y la factura se guarda como antes.
    @Value("${arca.habilitado:false}")
    private boolean arcaHabilitado;

    public List<FacturaReporteDTO> buscarFacturasFiltradas(Date fechaDesde, Date fechaHasta, String estado,
            Double montoMinimo) {
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
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "No existe el articulo con id " + detDto.getArticuloId()));

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

        if (arcaHabilitado) {
            emitirEnArca(factura, cliente, condicionIva, puntoVenta);
        }

        FacturaVenta guardada = facturaRepository.guardar(factura);
        return mapearAReporteDTO(guardada);
    }

    @Transactional
    public FacturaReporteDTO actualizar(Long id, FacturaRequestDTO dto) {
        FacturaVenta factura = facturaRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la factura con id " + id));

        if (dto.getEstado() != null && !dto.getEstado().trim().isEmpty()) {
            factura.setEstado(dto.getEstado());
            if ("ANULADA".equalsIgnoreCase(dto.getEstado())) {
                factura.setFechaAnulacion(new Date());
            }
        }

        if (dto.getObservaciones() != null) {
            factura.setObservaciones(dto.getObservaciones());
        }

        if (dto.getDetalles() != null && !dto.getDetalles().isEmpty()) {
            if (dto.getPuntoVentaId() != null) {
                PuntoVenta puntoVenta = em.find(PuntoVenta.class, dto.getPuntoVentaId());
                if (puntoVenta == null) {
                    throw new RecursoNoEncontradoException(
                            "No existe el punto de venta con id " + dto.getPuntoVentaId());
                }
                factura.setPuntoVenta(puntoVenta);
            }

            if (dto.getCondicionIvaId() != null) {
                CondicionIva condicionIva = em.find(CondicionIva.class, dto.getCondicionIvaId());
                if (condicionIva == null) {
                    throw new RecursoNoEncontradoException(
                            "No existe la condicion de IVA con id " + dto.getCondicionIvaId());
                }
                factura.setCondicionIva(condicionIva);
            }

            if (dto.getClienteId() != null) {
                Cliente cliente = em.find(Cliente.class, dto.getClienteId());
                if (cliente == null) {
                    throw new RecursoNoEncontradoException("No existe el cliente con id " + dto.getClienteId());
                }
                factura.setCliente(cliente);
            }

            if (dto.getTipoMonedaId() != null) {
                TipoMoneda tipoMoneda = em.find(TipoMoneda.class, dto.getTipoMonedaId());
                if (tipoMoneda == null) {
                    throw new RecursoNoEncontradoException(
                            "No existe el tipo de moneda con id " + dto.getTipoMonedaId());
                }
                factura.setTipoMoneda(tipoMoneda);
            }

            if (dto.getFechaEmision() != null) {
                factura.setFechaEmision(dto.getFechaEmision());
            }

            if (dto.getNumero() != null) {
                factura.setNumero(dto.getNumero());
            }

            List<FacturaVentaDetalle> nuevosDetalles = new ArrayList<>();
            double totalFactura = 0.0;

            for (FacturaDetalleRequestDTO detDto : dto.getDetalles()) {
                ListaPrecioArticulo lpa = facturaRepository.buscarListaPrecioPorArticuloId(detDto.getArticuloId())
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "No existe el articulo con id " + detDto.getArticuloId()));

                FacturaVentaDetalle nuevoDetalle = new FacturaVentaDetalle();
                nuevoDetalle.setListaPrecioArticulo(lpa);
                nuevoDetalle.setDescripcion(detDto.getDescripcion());
                nuevoDetalle.setCantidad(detDto.getCantidad());
                nuevoDetalle.setPrecioUnitario(detDto.getPrecioUnitario());
                nuevoDetalle.setPorcentajeBonificacion(detDto.getPorcentajeBonificacion());

                double bruto = detDto.getCantidad() * detDto.getPrecioUnitario();
                double neto = bruto * (1.0 - (detDto.getPorcentajeBonificacion() / 100.0));
                double iva = neto * 0.21;
                double subtotal = neto + iva;

                nuevoDetalle.setImporteNeto(neto);
                nuevoDetalle.setImporteIva(iva);
                nuevoDetalle.setImporteSubtotal(subtotal);

                nuevosDetalles.add(nuevoDetalle);
                totalFactura += subtotal;
            }

            factura.getDetalles().clear();
            for (FacturaVentaDetalle nuevoDetalle : nuevosDetalles) {
                nuevoDetalle.setFactura(factura);
            }
            factura.getDetalles().addAll(nuevosDetalles);

            factura.setImporteTotal(totalFactura);
            factura.setImporteSaldo(totalFactura);
        }

        FacturaVenta actualizada = facturaRepository.guardar(factura);
        return mapearAReporteDTO(actualizada);
    }

    @Transactional
    public void eliminar(Long id) {
        FacturaVenta factura = facturaRepository.buscarPorId(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("No existe la factura con id " + id));
        facturaRepository.eliminar(factura);
    }

    /**
     * Pide el CAE a ARCA y completa numero, cae, vencimiento y resultado en la
     * factura.
     * Si ARCA no responde, lanza excepcion y @Transactional revierte: no se guarda
     * nada.
     */
    private void emitirEnArca(FacturaVenta factura, Cliente cliente, CondicionIva condicionIva, PuntoVenta puntoVenta) {
        try {
            int cbteTipo = wsfeClient.getCbteTipo();
            boolean tipoC = cbteTipo == 11 || cbteTipo == 12 || cbteTipo == 13;

            BigDecimal netoDet = BigDecimal.ZERO;
            BigDecimal ivaDet = BigDecimal.ZERO;
            for (FacturaVentaDetalle d : factura.getDetalles()) {
                netoDet = netoDet.add(BigDecimal.valueOf(d.getImporteNeto()));
                ivaDet = ivaDet.add(BigDecimal.valueOf(d.getImporteIva()));
            }

            BigDecimal neto, iva, total;
            if (tipoC) { // Factura C no discrimina IVA: todo va como neto
                total = BigDecimal.valueOf(factura.getImporteTotal()).setScale(2, RoundingMode.HALF_UP);
                neto = total;
                iva = BigDecimal.ZERO.setScale(2);
            } else { // Factura A/B: total = neto + IVA, redondeado a 2 decimales
                neto = netoDet.setScale(2, RoundingMode.HALF_UP);
                iva = ivaDet.setScale(2, RoundingMode.HALF_UP);
                total = neto.add(iva);
                factura.setImporteTotal(total.doubleValue());
                factura.setImporteSaldo(total.doubleValue());
            }

            int docTipo = 99; // Consumidor final
            long docNro = 0;
            if (cliente != null && cliente.getCuitCuil() != null) {
                String digitos = cliente.getCuitCuil().replaceAll("\\D", "");
                if (digitos.length() == 11) {
                    docTipo = 80; // CUIT
                    docNro = Long.parseLong(digitos);
                }
            }

            int ptoVta = puntoVenta.getNumero();
            long nro = wsfeClient.ultimoAutorizado(ptoVta, cbteTipo) + 1;
            String fecha = new SimpleDateFormat("yyyyMMdd").format(factura.getFechaEmision());

            WsfeClient.Resultado r = wsfeClient.solicitarCae(new WsfeClient.Solicitud(
                    ptoVta, cbteTipo, nro, docTipo, docNro,
                    condicionIva.getCodigoAfip(), fecha, total, neto, iva));

            factura.setNumero(nro);
            factura.setResultadoAfip(r.resultado());
            if ("A".equals(r.resultado())) {
                factura.setCae(r.cae());
                factura.setCaeFechaVencimiento(r.caeVto());
            } else {
                factura.setMotivoRechazo(r.observaciones());
                factura.setEstado("RECHAZADA");
            }
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo emitir en ARCA: " + e.getMessage(), e);
        }
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
                factura.getEstado());
    }
}