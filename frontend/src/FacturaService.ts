import {
    FacturaFiltrosDTO,
    FacturaReporteDTO,
    FacturaRequestDTO
} from './models/factura.dto.js';

const API_BASE_URL = 'http://localhost:8080/api/facturas';

export class FacturaService {

    private async manejarRespuesta<T>(respuesta: Response): Promise<T> {
        if (!respuesta.ok) {
            const mensaje = await respuesta.text();
            throw new Error(`Error ${respuesta.status}: ${mensaje || respuesta.statusText}`);
        }
        if (respuesta.status === 204) {
            return undefined as T;
        }
        return respuesta.json() as Promise<T>;
    }

    private construirQueryString(filtros: FacturaFiltrosDTO): string {
        const params = new URLSearchParams();
        Object.entries(filtros).forEach(([clave, valor]) => {
            if (valor !== undefined && valor !== null && String(valor).trim() !== '') {
                params.append(clave, String(valor));
            }
        });
        const query = params.toString();
        return query ? `?${query}` : '';
    }

    async obtenerFacturas(filtros: FacturaFiltrosDTO = {}): Promise<FacturaReporteDTO[]> {
        const url = `${API_BASE_URL}${this.construirQueryString(filtros)}`;
        const respuesta = await fetch(url);
        return this.manejarRespuesta<FacturaReporteDTO[]>(respuesta);
    }

    async crearFactura(factura: FacturaRequestDTO): Promise<FacturaReporteDTO> {
        const respuesta = await fetch(API_BASE_URL, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(factura)
        });
        return this.manejarRespuesta<FacturaReporteDTO>(respuesta);
    }

    async actualizarEstado(id: number, estado: string, observaciones?: string): Promise<FacturaReporteDTO> {
        const respuesta = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'PUT',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ estado, observaciones })
        });
        return this.manejarRespuesta<FacturaReporteDTO>(respuesta);
    }

    async eliminarFactura(id: number): Promise<void> {
        const respuesta = await fetch(`${API_BASE_URL}/${id}`, {
            method: 'DELETE'
        });
        return this.manejarRespuesta<void>(respuesta);
    }
}
