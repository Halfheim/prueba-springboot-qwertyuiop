import {
    FacturaDetalleRequestDTO,
    FacturaFiltrosDTO,
    FacturaReporteDTO,
    FacturaRequestDTO
} from './models/factura.dto.js';
import { FacturaService } from './FacturaService.js';

const facturaService = new FacturaService();

const tbody = document.getElementById('facturas-tbody') as HTMLTableSectionElement;
const filtrosForm = document.getElementById('filtros-form') as HTMLFormElement;
const btnLimpiar = document.getElementById('btn-limpiar') as HTMLButtonElement;

const facturaForm = document.getElementById('factura-form') as HTMLFormElement;
const numeroInput = document.getElementById('numero') as HTMLInputElement;
const fechaEmisionInput = document.getElementById('fecha-emision') as HTMLInputElement;
const puntoVentaInput = document.getElementById('punto-venta-id') as HTMLInputElement;
const condicionIvaInput = document.getElementById('condicion-iva-id') as HTMLInputElement;
const clienteInput = document.getElementById('cliente-id') as HTMLInputElement;
const tipoMonedaInput = document.getElementById('tipo-moneda-id') as HTMLInputElement;
const estadoFacturaInput = document.getElementById('estado-factura') as HTMLSelectElement;
const observacionesInput = document.getElementById('observaciones') as HTMLInputElement;

const detallesTbody = document.getElementById('detalles-tbody') as HTMLTableSectionElement;
const importeTotalCell = document.getElementById('importe-total') as HTMLTableCellElement;
const btnAgregarDetalle = document.getElementById('btn-agregar-detalle') as HTMLButtonElement;
const mensajeEstado = document.getElementById('mensaje-estado') as HTMLDivElement;

// Estado de los renglones en memoria
let detalles: FacturaDetalleRequestDTO[] = [];

function mostrarMensaje(texto: string, esError: boolean = false): void {
    mensajeEstado.textContent = texto;
    mensajeEstado.className = esError ? 'alert alert-danger' : 'alert alert-success';
}

function ocultarMensaje(): void {
    mensajeEstado.className = 'alert d-none';
    mensajeEstado.textContent = '';
}

function formatearFecha(fechaStr: string | undefined): string {
    if (!fechaStr) return '-';
    const fecha = new Date(fechaStr);
    if (isNaN(fecha.getTime())) return fechaStr;
    return fecha.toLocaleDateString('es-AR');
}

function formatearMoneda(valor: number | undefined): string {
    if (valor === undefined || valor === null) return '0,00';
    return valor.toLocaleString('es-AR', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
}

// Neto bonificado + IVA 21% (misma fórmula que FacturaService en el backend)
function calcularSubtotal(detalle: FacturaDetalleRequestDTO): number {
    const bruto = (detalle.cantidad || 0) * (detalle.precioUnitario || 0);
    const neto = bruto * (1 - ((detalle.porcentajeBonificacion || 0) / 100));
    const iva = neto * 0.21;
    return neto + iva;
}

function calcularImporteTotal(): number {
    return detalles.reduce((total, detalle) => total + calcularSubtotal(detalle), 0);
}

function actualizarImporteTotal(): void {
    importeTotalCell.textContent = formatearMoneda(calcularImporteTotal());
}

function crearDetalleVacio(): FacturaDetalleRequestDTO {
    return {
        articuloId: 1,
        descripcion: '',
        cantidad: 1,
        precioUnitario: 0,
        porcentajeBonificacion: 0
    };
}

function crearBoton(texto: string, clases: string, onClick: () => void): HTMLButtonElement {
    const boton = document.createElement('button');
    boton.type = 'button';
    boton.textContent = texto;
    boton.className = `btn btn-sm ${clases}`;
    boton.addEventListener('click', onClick);
    return boton;
}

function crearInputNumber(valor: number, step: string, min: string, onChange: (valor: number) => void): HTMLInputElement {
    const input = document.createElement('input');
    input.type = 'number';
    input.className = 'form-control';
    input.step = step;
    input.min = min;
    input.value = valor.toString();
    input.required = true;
    input.addEventListener('input', () => {
        const nuevoValor = parseFloat(input.value);
        onChange(isNaN(nuevoValor) ? 0 : nuevoValor);
        actualizarImporteTotal();
    });
    return input;
}

function crearFilaDetalle(detalle: FacturaDetalleRequestDTO, indice: number): HTMLTableRowElement {
    const fila = document.createElement('tr');

    const celdaArticulo = document.createElement('td');
    const inputArticulo = crearInputNumber(detalle.articuloId, '1', '1', (valor) => {
        detalles[indice].articuloId = valor;
    });
    celdaArticulo.appendChild(inputArticulo);

    const celdaDescripcion = document.createElement('td');
    const inputDescripcion = document.createElement('input');
    inputDescripcion.type = 'text';
    inputDescripcion.className = 'form-control';
    inputDescripcion.placeholder = 'Descripción del ítem';
    inputDescripcion.value = detalle.descripcion;
    inputDescripcion.required = true;
    inputDescripcion.addEventListener('input', () => {
        detalles[indice].descripcion = inputDescripcion.value;
    });
    celdaDescripcion.appendChild(inputDescripcion);

    const celdaSubtotal = document.createElement('td');
    celdaSubtotal.className = 'text-end align-middle';
    celdaSubtotal.textContent = formatearMoneda(calcularSubtotal(detalle));

    const celdaCantidad = document.createElement('td');
    const inputCantidad = crearInputNumber(detalle.cantidad, '1', '1', (valor) => {
        detalles[indice].cantidad = valor;
        celdaSubtotal.textContent = formatearMoneda(calcularSubtotal(detalles[indice]));
    });
    celdaCantidad.appendChild(inputCantidad);

    const celdaPrecio = document.createElement('td');
    const inputPrecio = crearInputNumber(detalle.precioUnitario, '0.01', '0', (valor) => {
        detalles[indice].precioUnitario = valor;
        celdaSubtotal.textContent = formatearMoneda(calcularSubtotal(detalles[indice]));
    });
    celdaPrecio.appendChild(inputPrecio);

    const celdaBonificacion = document.createElement('td');
    const inputBonificacion = crearInputNumber(detalle.porcentajeBonificacion, '0.01', '0', (valor) => {
        detalles[indice].porcentajeBonificacion = valor;
        celdaSubtotal.textContent = formatearMoneda(calcularSubtotal(detalles[indice]));
    });
    celdaBonificacion.appendChild(inputBonificacion);

    const celdaAcciones = document.createElement('td');
    celdaAcciones.className = 'text-center align-middle';
    const btnEliminar = crearBoton('Quitar', 'btn-outline-danger', () => {
        detalles.splice(indice, 1);
        renderizarDetalles();
        actualizarImporteTotal();
    });
    celdaAcciones.appendChild(btnEliminar);

    fila.appendChild(celdaArticulo);
    fila.appendChild(celdaDescripcion);
    fila.appendChild(celdaCantidad);
    fila.appendChild(celdaPrecio);
    fila.appendChild(celdaBonificacion);
    fila.appendChild(celdaSubtotal);
    fila.appendChild(celdaAcciones);

    return fila;
}

function renderizarDetalles(): void {
    detallesTbody.innerHTML = '';
    detalles.forEach((detalle, indice) => {
        detallesTbody.appendChild(crearFilaDetalle(detalle, indice));
    });
}

function agregarDetalle(): void {
    detalles.push(crearDetalleVacio());
    renderizarDetalles();
    actualizarImporteTotal();
}

function reiniciarFormularioAlta(): void {
    facturaForm.reset();
    fechaEmisionInput.value = new Date().toISOString().split('T')[0];
    detalles = [crearDetalleVacio()];
    renderizarDetalles();
    actualizarImporteTotal();
}

function renderizarFacturas(facturas: FacturaReporteDTO[]): void {
    tbody.innerHTML = '';

    if (facturas.length === 0) {
        const fila = document.createElement('tr');
        fila.innerHTML = '<td colspan="9" class="text-center">No se encontraron facturas.</td>';
        tbody.appendChild(fila);
        return;
    }

    facturas.forEach(factura => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${factura.numeroFactura ?? '-'}</td>
            <td>${formatearFecha(factura.fechaEmision)}</td>
            <td>${factura.clienteDenominacion ?? '-'}</td>
            <td>${factura.condicionIva ?? '-'}</td>
            <td>${factura.puntoVentaDescripcion ?? '-'}</td>
            <td class="text-end">${formatearMoneda(factura.importeTotal)}</td>
            <td class="text-center">${factura.cantidadItems}</td>
            <td class="text-center">${factura.estado}</td>
            <td class="text-center"></td>
        `;

        const celdaAcciones = fila.cells[fila.cells.length - 1];
        const grupoBotones = document.createElement('div');
        grupoBotones.className = 'btn-group';

        if (factura.estado !== 'ANULADA') {
            grupoBotones.appendChild(crearBoton('Anular', 'btn-outline-warning', async () => {
                if (!confirm(`¿Anular la factura ${factura.numeroFactura}?`)) return;
                try {
                    await facturaService.actualizarEstado(factura.id, 'ANULADA', 'Factura anulada desde la web');
                    mostrarMensaje(`Factura ${factura.numeroFactura} anulada correctamente.`);
                    await cargarFacturas();
                } catch (error) {
                    mostrarMensaje('Error al anular la factura: ' + (error as Error).message, true);
                    console.error(error);
                }
            }));
        }

        grupoBotones.appendChild(crearBoton('Eliminar', 'btn-outline-danger', async () => {
            if (!confirm(`¿Eliminar la factura ${factura.numeroFactura}?`)) return;
            try {
                await facturaService.eliminarFactura(factura.id);
                mostrarMensaje(`Factura ${factura.numeroFactura} eliminada correctamente.`);
                await cargarFacturas();
            } catch (error) {
                mostrarMensaje('Error al eliminar la factura: ' + (error as Error).message, true);
                console.error(error);
            }
        }));

        celdaAcciones.appendChild(grupoBotones);
        tbody.appendChild(fila);
    });
}

function leerFiltros(): FacturaFiltrosDTO {
    return {
        fechaDesde: (document.getElementById('filtro-fecha-desde') as HTMLInputElement).value,
        fechaHasta: (document.getElementById('filtro-fecha-hasta') as HTMLInputElement).value,
        estado: (document.getElementById('filtro-estado') as HTMLSelectElement).value,
        montoMinimo: (document.getElementById('filtro-monto-minimo') as HTMLInputElement).value
    };
}

async function cargarFacturas(): Promise<void> {
    try {
        const filtros = leerFiltros();
        const facturas = await facturaService.obtenerFacturas(filtros);
        renderizarFacturas(facturas);
    } catch (error) {
        tbody.innerHTML = `<tr><td colspan="9" class="text-center text-danger">Error al cargar facturas: ${(error as Error).message}</td></tr>`;
        console.error(error);
    }
}

filtrosForm.addEventListener('submit', (evento) => {
    evento.preventDefault();
    ocultarMensaje();
    cargarFacturas();
});

btnLimpiar.addEventListener('click', () => {
    filtrosForm.reset();
    ocultarMensaje();
    cargarFacturas();
});

btnAgregarDetalle.addEventListener('click', agregarDetalle);

facturaForm.addEventListener('submit', async (evento) => {
    evento.preventDefault();

    if (detalles.length === 0) {
        mostrarMensaje('Debe agregar al menos un detalle a la factura.', true);
        return;
    }

    const nuevaFactura: FacturaRequestDTO = {
        numero: parseInt(numeroInput.value, 10),
        fechaEmision: fechaEmisionInput.value,
        puntoVentaId: parseInt(puntoVentaInput.value, 10),
        condicionIvaId: parseInt(condicionIvaInput.value, 10),
        estado: estadoFacturaInput.value,
        detalles: detalles.map(d => ({
            articuloId: d.articuloId,
            descripcion: d.descripcion.trim(),
            cantidad: d.cantidad,
            precioUnitario: d.precioUnitario,
            porcentajeBonificacion: d.porcentajeBonificacion
        }))
    };

    if (clienteInput.value.trim() !== '') {
        nuevaFactura.clienteId = parseInt(clienteInput.value, 10);
    }
    if (tipoMonedaInput.value.trim() !== '') {
        nuevaFactura.tipoMonedaId = parseInt(tipoMonedaInput.value, 10);
    }
    if (observacionesInput.value.trim() !== '') {
        nuevaFactura.observaciones = observacionesInput.value.trim();
    }

    try {
        const creada = await facturaService.crearFactura(nuevaFactura);
        mostrarMensaje(`Factura ${creada.numeroFactura} creada exitosamente.`);
        reiniciarFormularioAlta();
        await cargarFacturas();
    } catch (error) {
        mostrarMensaje('Error al guardar la factura: ' + (error as Error).message, true);
        console.error(error);
    }
});

// Inicialización de la vista
reiniciarFormularioAlta();
cargarFacturas();
