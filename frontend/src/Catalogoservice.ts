import { ArticuloOpcionDTO, OpcionDTO } from './models/factura.dto.js'; 
const API_BASE_URL = 'http://localhost:8080/api';
 
export class CatalogoService {
 
    private async obtener(ruta: string): Promise<OpcionDTO[]> {
        const respuesta = await fetch(`${API_BASE_URL}/${ruta}`);
        if (!respuesta.ok) {
            throw new Error(`Error ${respuesta.status} al pedir ${ruta}`);
        }
        return respuesta.json() as Promise<OpcionDTO[]>;
    }
 
    obtenerPuntosVenta(): Promise<OpcionDTO[]> {
        return this.obtener('puntos-venta');
    }
 
    obtenerCondicionesIva(): Promise<OpcionDTO[]> {
        return this.obtener('condiciones-iva');
    }
 
    obtenerTiposMoneda(): Promise<OpcionDTO[]> {
        return this.obtener('tipos-moneda');
    }

    obtenerClientes(): Promise<OpcionDTO[]> {
        return this.obtener('clientes');
    }

 async obtenerArticulos(): Promise<ArticuloOpcionDTO[]> {
    const respuesta = await fetch(`${API_BASE_URL}/articulos`);
    if (!respuesta.ok) {
        throw new Error(`Error ${respuesta.status} al pedir articulos`);
    }
    return respuesta.json() as Promise<ArticuloOpcionDTO[]>;
}
}
 