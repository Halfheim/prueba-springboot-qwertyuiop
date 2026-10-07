package com.ejemplo.loader;

import com.ejemplo.entities.*;
import com.ejemplo.repository.*;
import com.ejemplo.service.FacturaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final FacturaRepository facturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final PuntoVentaRepository puntoVentaRepository;
    private final CondicionIvaRepository condicionIvaRepository;
    private final TipoMonedaRepository tipoMonedaRepository;
    private final ClienteRepository clienteRepository;
    private final ContactoRepository contactoRepository;
    private final DomicilioRepository domicilioRepository;
    private final ArticuloRepository articuloRepository;
    private final ListaPrecioRepository listaPrecioRepository;
    private final ListaPrecioArticuloRepository listaPrecioArticuloRepository;
    private final FacturaService facturaService;

    public DataLoader(
            FacturaRepository facturaRepository,
            UsuarioRepository usuarioRepository,
            PuntoVentaRepository puntoVentaRepository,
            CondicionIvaRepository condicionIvaRepository,
            TipoMonedaRepository tipoMonedaRepository,
            ClienteRepository clienteRepository,
            ContactoRepository contactoRepository,
            DomicilioRepository domicilioRepository,
            ArticuloRepository articuloRepository,
            ListaPrecioRepository listaPrecioRepository,
            ListaPrecioArticuloRepository listaPrecioArticuloRepository,
            FacturaService facturaService
    ) {
        this.facturaRepository = facturaRepository;
        this.usuarioRepository = usuarioRepository;
        this.puntoVentaRepository = puntoVentaRepository;
        this.condicionIvaRepository = condicionIvaRepository;
        this.tipoMonedaRepository = tipoMonedaRepository;
        this.clienteRepository = clienteRepository;
        this.contactoRepository = contactoRepository;
        this.domicilioRepository = domicilioRepository;
        this.articuloRepository = articuloRepository;
        this.listaPrecioRepository = listaPrecioRepository;
        this.listaPrecioArticuloRepository = listaPrecioArticuloRepository;
        this.facturaService = facturaService;
    }

    @Override
    public void run(String... args) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        if (facturaRepository.count() == 0) {
            System.out.println(">> Base de datos sin facturas. Cargando datos de prueba del TP...");

            // 1. Usuario base
            Usuario usuarioAdmin = new Usuario();
            usuarioAdmin.setUsuario("admin");
            usuarioAdmin.setClave("admin123");
            usuarioAdmin.setNombre("Santi");
            usuarioAdmin.setApellido("Admin");
            usuarioRepository.save(usuarioAdmin);

            // 2. Punto de Venta
            PuntoVenta pv = new PuntoVenta();
            pv.setNumero(1);
            pv.setDescripcion("Sucursal Central");
            pv.setTipoEmision("Electronica");
            pv.setDomicilioComercial("Av. San Martin 1234");
            pv.setFechaAlta(new Date());
            pv.setFechaModificacion(new Date());
            pv.setUsuarioCarga(usuarioAdmin);
            pv.setUsuarioModificacion(usuarioAdmin);
            puntoVentaRepository.save(pv);

            // 3. Condición de IVA
            CondicionIva condicionIva = new CondicionIva();
            condicionIva.setCodigoAfip(1);
            condicionIva.setDenominacion("IVA Responsable Inscripto");
            condicionIva.setFechaAlta(new Date());
            condicionIva.setFechaModificacion(new Date());
            condicionIva.setUsuarioCarga(usuarioAdmin);
            condicionIva.setUsuarioModificacion(usuarioAdmin);
            condicionIvaRepository.save(condicionIva);

            // 4. Tipo de Moneda
            TipoMoneda tipoMoneda = new TipoMoneda();
            tipoMoneda.setCodigoAfip("PES");
            tipoMoneda.setDenominacion("Pesos Argentinos");
            tipoMoneda.setSimbolo("$");
            tipoMoneda.setFechaAlta(new Date());
            tipoMoneda.setFechaModificacion(new Date());
            tipoMoneda.setUsuarioCarga(usuarioAdmin);
            tipoMoneda.setUsuarioModificacion(usuarioAdmin);
            tipoMonedaRepository.save(tipoMoneda);

            // 5. Contacto y Domicilio para Cliente
            Contacto contacto = new Contacto();
            contacto.setEmail("info@empresatech.com");
            contacto.setTelefono("0261-4234567");
            contacto.setCelular("261-5556677");
            contactoRepository.save(contacto);

            Domicilio domicilio = new Domicilio();
            domicilio.setNombreCalle("Belgrano");
            domicilio.setNumeroCalle("800");
            domicilioRepository.save(domicilio);

            // 6. Cliente ("Empresa Tech S.A." del PDF)
            Cliente clienteEmpresa = new Cliente();
            clienteEmpresa.setDenominacion("Empresa Tech S.A.");
            clienteEmpresa.setCuitCuil("30-71122334-9");
            clienteEmpresa.setContacto(contacto);
            clienteEmpresa.setDomicilio(domicilio);
            clienteEmpresa.setFechaAlta(new Date());
            clienteEmpresa.setFechaModificacion(new Date());
            clienteEmpresa.setUsuarioCarga(usuarioAdmin);
            clienteEmpresa.setUsuarioModificacion(usuarioAdmin);
            clienteRepository.save(clienteEmpresa);

            // 7. Artículos y Lista de Precios
            ListaPrecio listaPrecio = new ListaPrecio();
            listaPrecio.setCodigo("LP1");
            listaPrecio.setDenominacion("Lista Principal");
            listaPrecio.setFechaAlta(new Date());
            listaPrecio.setFechaModificacion(new Date());
            listaPrecio.setUsuarioCarga(usuarioAdmin);
            listaPrecio.setUsuarioModificacion(usuarioAdmin);
            listaPrecioRepository.save(listaPrecio);

            Articulo articulo1 = new Articulo();
            articulo1.setCodigo("ART-001");
            articulo1.setDenominacion("Teclado Mecanico");
            articulo1.setFechaAlta(new Date());
            articulo1.setFechaModificacion(new Date());
            articulo1.setUsuarioCarga(usuarioAdmin);
            articulo1.setUsuarioModificacion(usuarioAdmin);
            articuloRepository.save(articulo1);

            ListaPrecioArticulo lpa1 = new ListaPrecioArticulo();
            lpa1.setArticulo(articulo1);
            lpa1.setListaPrecio(listaPrecio);
            lpa1.setPrecioVenta(750.0);
            lpa1.setFechaAlta(new Date());
            lpa1.setFechaModificacion(new Date());
            lpa1.setUsuarioCarga(usuarioAdmin);
            lpa1.setUsuarioModificacion(usuarioAdmin);
            listaPrecioArticuloRepository.save(lpa1);

            // 8. Factura 1001 (Consumidor Final, Importe 1500.0, 2 items, Fecha 2026-03-15)
            FacturaVenta f1001 = FacturaVenta.builder()
                    .numero(1001L)
                    .fechaEmision(sdf.parse("2026-03-15"))
                    .puntoVenta(pv)
                    .cliente(null) // Para que COALESCE devuelva 'Consumidor Final'
                    .condicionIva(condicionIva)
                    .tipoMoneda(tipoMoneda)
                    .importeCobrado(1500.0)
                    .importeSaldo(0.0)
                    .importeTotal(1500.0)
                    .estado("EMITIDA")
                    .cae("12345678901234")
                    .caeFechaVencimiento(sdf.parse("2026-03-25"))
                    .resultadoAfip("A")
                    .detalles(new ArrayList<>())
                    .build();
            f1001.setFechaAlta(new Date());
            f1001.setFechaModificacion(new Date());
            f1001.setUsuarioCarga(usuarioAdmin);
            f1001.setUsuarioModificacion(usuarioAdmin);

            // Detalle 1
            FacturaVentaDetalle d1 = FacturaVentaDetalle.builder()
                    .factura(f1001)
                    .listaPrecioArticulo(lpa1)
                    .descripcion("Item 1")
                    .cantidad(1.0)
                    .precioUnitario(750.0)
                    .importeSubtotal(750.0)
                    .build();
            // Detalle 2
            FacturaVentaDetalle d2 = FacturaVentaDetalle.builder()
                    .factura(f1001)
                    .listaPrecioArticulo(lpa1)
                    .descripcion("Item 2")
                    .cantidad(1.0)
                    .precioUnitario(750.0)
                    .importeSubtotal(750.0)
                    .build();
            f1001.getDetalles().add(d1);
            f1001.getDetalles().add(d2);
            facturaRepository.save(f1001);

            // 9. Factura 1002 (Empresa Tech S.A., Importe 85000.5, 5 items, Fecha 2026-03-20)
            FacturaVenta f1002 = FacturaVenta.builder()
                    .numero(1002L)
                    .fechaEmision(sdf.parse("2026-03-20"))
                    .puntoVenta(pv)
                    .cliente(clienteEmpresa)
                    .condicionIva(condicionIva)
                    .tipoMoneda(tipoMoneda)
                    .importeCobrado(85000.5)
                    .importeSaldo(0.0)
                    .importeTotal(85000.5)
                    .estado("EMITIDA")
                    .cae("98765432109876")
                    .caeFechaVencimiento(sdf.parse("2026-03-30"))
                    .resultadoAfip("A")
                    .detalles(new ArrayList<>())
                    .build();
            f1002.setFechaAlta(new Date());
            f1002.setFechaModificacion(new Date());
            f1002.setUsuarioCarga(usuarioAdmin);
            f1002.setUsuarioModificacion(usuarioAdmin);

            for (int i = 1; i <= 5; i++) {
                FacturaVentaDetalle d = FacturaVentaDetalle.builder()
                        .factura(f1002)
                        .listaPrecioArticulo(lpa1)
                        .descripcion("Item " + i)
                        .cantidad(1.0)
                        .precioUnitario(17000.1)
                        .importeSubtotal(17000.1)
                        .build();
                f1002.getDetalles().add(d);
            }
            facturaRepository.save(f1002);

            // 10. Factura 1003 (Para testear filtro de estado 'ANULADA')
            FacturaVenta f1003 = FacturaVenta.builder()
                    .numero(1003L)
                    .fechaEmision(sdf.parse("2026-03-25"))
                    .puntoVenta(pv)
                    .cliente(clienteEmpresa)
                    .condicionIva(condicionIva)
                    .tipoMoneda(tipoMoneda)
                    .importeCobrado(0.0)
                    .importeSaldo(3200.0)
                    .importeTotal(3200.0)
                    .estado("ANULADA")
                    .detalles(new ArrayList<>())
                    .build();
            f1003.setFechaAlta(new Date());
            f1003.setFechaModificacion(new Date());
            f1003.setUsuarioCarga(usuarioAdmin);
            f1003.setUsuarioModificacion(usuarioAdmin);

            FacturaVentaDetalle d3 = FacturaVentaDetalle.builder()
                    .factura(f1003)
                    .listaPrecioArticulo(lpa1)
                    .descripcion("Item Anulado")
                    .cantidad(1.0)
                    .precioUnitario(3200.0)
                    .importeSubtotal(3200.0)
                    .build();
            f1003.getDetalles().add(d3);
            facturaRepository.save(f1003);

            System.out.println(">> Datos de prueba cargados correctamente.");
        } else {
            System.out.println(">> La base de datos ya contiene " + facturaRepository.count() + " facturas.");
        }

        // Generar archivos PDF y Excel en disco según Consigna Parte 2
        var todas = facturaService.buscarFacturasFiltradas(null, null, null, null);
        facturaService.guardarReportesEnDisco(todas);
    }
}
