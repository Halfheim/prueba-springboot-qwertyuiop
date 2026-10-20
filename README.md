# TP CRUD Factura de Venta — Guía de Traspaso

## 1. Estado Actual (Objetivos 1 al 4.2)

- **Estructura Base:**
  - `backend/`: Proyecto Spring Boot + JPA copiado de `tp-anterior`.
  - `frontend/`: Servidor Express estático en puerto `3000` con configuración TypeScript (`package.json`, `tsconfig.json`, `src/index.ts`).
- **DTOs (Consigna 1):**
  - `FacturaRequestDTO` y `FacturaDetalleRequestDTO`: reciben identificadores (`clienteId`, `condicionIvaId`, `puntoVentaId`, `tipoMonedaId`, `articuloId`) y datos planos.
  - `FacturaReporteDTO`: extendido con los campos `id` y `estado`, conservando el constructor de 7 parámetros usado por `MainReportes`.
- **Manejo de Errores (`@RestControllerAdvice`):**
  - `RecursoNoEncontradoException`: excepción de dominio para entidades inexistentes.
  - `ErrorResponse`: estructura JSON uniforme (`status`, `mensaje`, `fecha`).
  - `GlobalExceptionHandler`: captura `RecursoNoEncontradoException` y devuelve `HTTP 404 Not Found`.
- **Persistencia y Lógica de Alta (Consigna 2.a y 2.b):**
  - `FacturaRepository`: proyección actualizada en `buscarFacturasFiltradas` (`id` y `estado`), más los métodos `buscarPorId`, `buscarListaPrecioPorArticuloId`, `guardar` y `eliminar`.
  - `FacturaService.crear()`: anotado con `@Transactional`, valida entidades con `em.find()`, asigna usuario de auditoría (`ID 1`), calcula importes (`importeNeto`, `importeIva` al 21%, `importeSubtotal` e `importeTotal`) y persiste cabecera y detalles en cascada.

---

## 2. Pendientes (Objetivos 4.3 al 7)

| Área | Archivo | Tareas Pendientes |
| :--- | :--- | :--- |
| **Backend (Servicio)** | `FacturaService.java` | Implementar `actualizar(Long id, FacturaRequestDTO dto)` (anulación lógica con `fechaAnulacion` y reemplazo de ítems con `clear()` + `addAll()` respetando `orphanRemoval`) y `eliminar(Long id)` con `@Transactional`. |
| **Backend (Controller)** | `FacturaRestController.java` | Mapear `POST /api/facturas` (`201 Created`), `PUT /api/facturas/{id}` (`200 OK`) y `DELETE /api/facturas/{id}` (`204 No Content`). |
| **Frontend (Modelos)** | `src/models/factura.dto.ts` | Definir las interfaces `FacturaDetalleRequestDTO`, `FacturaRequestDTO`, `FacturaReporteDTO` y `FacturaFiltrosDTO`. |
| **Frontend (Servicio)** | `src/FacturaService.ts` | Implementar cliente HTTP con `fetch` (`obtenerFacturas`, `crearFactura`, `actualizarEstado`, `eliminarFactura`). |
| **Frontend (UI y DOM)** | `public/index.html` y `src/app.ts` | Diseñar las 3 secciones (filtros, formulario de alta con tabla dinámica de detalles, grilla de comprobantes) y conectar eventos en el DOM mostrando avisos en `#mensaje-estado` (sin usar `alert()`). |

---

## 3. Aclaraciones Técnicas Clave

- **Base de Datos:** Mantiene la conexión a PostgreSQL (Supabase) heredada de `tp-anterior`.
- **Auditoría (`AuditoriaApp`):** Toda creación de factura busca el `Usuario` con `id = 1L` para completar `usuarioCarga` y `usuarioModificacion` (`nullable = false`). Asegurarse de contar con el registro `id = 1` en la tabla de usuarios.
- **Comandos de Compilación y Verificación:**
  - **Backend:**
    ```powershell
    cd backend && .\gradlew.bat compileJava
    ```
  - **Frontend:**
    ```powershell
    cd frontend && npm install && npm run build
    ```
