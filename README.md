# Trabajo Práctico: CRUD Factura de Venta

Aplicación integral (Backend Spring Boot + Frontend TypeScript/HTML5) para la gestión contable de Facturas de Venta, con persistencia relacional en PostgreSQL (Supabase) y consumo mediante API REST.

---

## 🛠️ Tecnologías Utilizadas

- **Backend:**
  - Java 21
  - Spring Boot 3.2.4
  - Spring Data JPA / Hibernate ORM 6.4
  - PostgreSQL (Supabase)
  - Gradle
  - Lombok
- **Frontend:**
  - TypeScript 5.6
  - Node.js / Express 4
  - Bootstrap 5.3 (CDN)
  - HTML5 / CSS3

---

## 🏛️ Arquitectura del Sistema

```
prueba-springboot-qwertyuiop/
├── backend/                         # Servidor API Spring Boot (Puerto 8080)
│   ├── src/main/java/tp/
│   │   ├── FacturaRestController.java   # Endpoints REST (GET, POST, PUT, DELETE)
│   │   ├── FacturaService.java          # Lógica de negocio transaccional (@Transactional)
│   │   ├── FacturaRepository.java       # Consultas JPQL y EntityManager
│   │   ├── GlobalExceptionHandler.java  # Manejo global de excepciones (@RestControllerAdvice)
│   │   ├── RecursoNoEncontradoException.java
│   │   ├── FacturaRequestDTO.java       # DTOs de entrada desacoplados
│   │   ├── FacturaDetalleRequestDTO.java
│   │   └── FacturaReporteDTO.java       # DTO de salida
│   └── src/main/resources/
│       ├── application.properties       # Conexión DataSource a PostgreSQL
│       └── META-INF/persistence.xml
└── frontend/                        # Cliente Web TypeScript (Puerto 3000)
    ├── public/
    │   └── index.html               # Vista unificada (Filtros, Alta dinámica, Listado)
    └── src/
        ├── index.ts                 # Servidor Express estático
        ├── app.ts                   # Lógica UI, estado en memoria y eventos DOM
        ├── FacturaService.ts        # Cliente HTTP centralizado (fetch + async/await)
        └── models/
            └── factura.dto.ts       # Interfaces TypeScript que espejan los DTOs de Java
```

---

## 🚀 Puesta en Marcha

### 1. Iniciar el Backend (Spring Boot)

Abrir una terminal en la carpeta `backend`:

```bash
cd backend
./gradlew bootRun
```
*En Windows (PowerShell / CMD):*
```powershell
cd backend
.\gradlew.bat bootRun
```
El servidor backend quedará disponible en: `http://localhost:8080`

### 2. Iniciar el Frontend (TypeScript / Express)

Abrir otra terminal en la carpeta `frontend`:

```bash
cd frontend
npm install
npm run build
npm start
```
El servidor web quedará disponible en: `http://localhost:3000`

### 3. Abrir la Aplicación

Ingresar desde cualquier navegador a:  
👉 **[http://localhost:3000/index.html](http://localhost:3000/index.html)**

---

## 📖 Guía de Uso del Sistema (Paso a Paso)

La aplicación web está diseñada en una vista unificada dividida en 3 paneles interactivos:

```
+--------------------------------------------------------------------------+
|                       GESTIÓN DE FACTURAS DE VENTA                       |
|   [ Mensaje de estado: avisos de éxito en verde / errores en rojo ]      |
+--------------------------------------------------------------------------+
|  1. FILTROS DE BÚSQUEDA                                                  |
|     Fecha Desde | Fecha Hasta | Estado (Todos/EMITIDA/...) | Monto Mínimo |
|     [ Botón Buscar ]  [ Botón Limpiar ]                                  |
+--------------------------------------------------------------------------+
|  2. NUEVA FACTURA DE VENTA                                               |
|     Número | Fecha | Punto Venta | Condición IVA | Cliente | Moneda ...  |
|     TABLA DE DETALLES:                                                   |
|       Artículo ID | Descripción | Cantidad | P. Unitario | Bonif% | Subtotal|
|     [ + Agregar ítem ]                                 Total: $ XXX,XX   |
|     [ Botón Guardar Factura ]                                            |
+--------------------------------------------------------------------------+
|  3. COMPROBANTES REGISTRADOS                                             |
|     Nº Factura | Fecha | Cliente | IVA | P. Venta | Total | Items | Estado|
|     Acciones: [ Anular ] [ Eliminar ]                                    |
+--------------------------------------------------------------------------+
```

---

### Función 1: Búsqueda y Filtrado de Comprobantes

Ubicada en la primera tarjeta superior (**"Filtros de Búsqueda"**):

1. **Campos de filtro disponibles (todos opcionales):**
   - **Fecha Desde / Fecha Hasta:** Permite acotar el rango de emisión de las facturas.
   - **Estado:** Menú desplegable para elegir ver comprobantes `EMITIDA`, `PAGADA`, `ANULADA` o `Todos`.
   - **Monto Mínimo:** Filtra facturas cuyo importe total sea mayor o igual al monto ingresado (ej: `10000`).
2. **Cómo ejecutar la búsqueda:**
   - Hacer click en **`Buscar`**: La grilla inferior se actualizará mostrando únicamente las facturas que cumplan con los criterios seleccionados.
3. **Cómo restablecer los filtros:**
   - Hacer click en **`Limpiar`**: Borra todos los inputs de filtro y recarga el listado completo de facturas registradas.

---

### Función 2: Alta de una Nueva Factura con Detalles Dinámicos

Ubicada en la segunda tarjeta (**"Nueva Factura de Venta"**):

1. **Completar los datos de la cabecera:**
   - **Número de Factura (Obligatorio):** Ingresar el número del comprobante (ej: `1015`).
   - **Fecha de Emisión (Obligatoria):** Se autocompleta con la fecha de hoy, pero se puede modificar desde el calendario.
   - **Punto de Venta ID (Obligatorio):** ID del punto de venta existente en la base de datos (ej: `1` para Local Mendoza Centro).
   - **Condición IVA ID (Obligatorio):** ID de la condición fiscal (ej: `1` para Responsable Inscripto).
   - **Cliente ID (Opcional):** ID del cliente (ej: `1` para Juan Lopez). Si se deja vacío, la factura se registrará como "Consumidor Final".
   - **Tipo Moneda ID (Opcional):** ID de la moneda (ej: `1` para Pesos Argentinos).
   - **Estado:** Seleccionar `EMITIDA` o `PAGADA`.
   - **Observaciones:** Texto libre opcional (ej: *"Venta mostrador"*).

2. **Cargar los renglones/detalles de la factura:**
   - La tabla inicia con un renglón listo para cargar.
   - **Agregar más renglones:** Hacer click en el botón verde **`+ Agregar ítem`**. Se sumará una nueva fila a la tabla.
   - **Completar cada ítem:**
     - **Artículo (ID):** ID del artículo en la base de datos (ej: `1`).
     - **Descripción:** Nombre o detalle del producto (ej: *"Gaseosa Cola 1.5L"*).
     - **Cantidad:** Número de unidades (ej: `2`).
     - **Precio Unitario:** Precio por unidad (ej: `1500`).
     - **Bonif. (%):** Descuento porcentual si aplica (ej: `10` para un 10% de bonificación, o `0`).
   - **Cálculo automático en tiempo real:**  
     A medida que se escriben las cantidades, precios o bonificaciones, el sistema calcula automáticamente en pantalla:
     $$\text{Neto} = \text{Cantidad} \times \text{Precio} \times \left(1 - \frac{\text{Bonif}}{100}\right)$$
     $$\text{Subtotal (c/IVA)} = \text{Neto} + (\text{Neto} \times 0.21)$$
     Y actualiza de inmediato el **Importe Total** al pie de la tabla.
   - **Quitar un ítem:** Si se desea remover un renglón, pulsar el botón rojo **`Quitar`** de esa fila. La fila se elimina y el importe total se recalcula al instante.

3. **Guardar el comprobante:**
   - Hacer click en el botón azul **`Guardar Factura`**.
   - Si todo es correcto:
     - El servidor persistirá la factura y sus detalles en cascada dentro de PostgreSQL.
     - Aparecerá un aviso verde: *"Factura XXXX creada exitosamente"*.
     - El formulario se limpiará automáticamente.
     - La nueva factura aparecerá inmediatamente en la grilla inferior.

---

### Función 3: Anulación de Facturas

Ubicada en la tercera tarjeta (**"Comprobantes Registrados"**):

1. En la columna **Acciones** de la tabla, las facturas que están activas (`EMITIDA` o `PAGADA`) disponen del botón amarillo **`Anular`**.
2. Al presionarlo, el navegador solicitará confirmación:  
   `¿Anular la factura XXXX?`
3. Al hacer click en **Aceptar**:
   - Se envía una petición `PUT` al backend.
   - El estado de la factura cambia a **`ANULADA`** y se registra la fecha de anulación.
   - La tabla se refresca mostrando el estado actualizado y el botón "Anular" desaparece de ese comprobante para evitar re-anulaciones.

---

### Función 4: Eliminación Física de Facturas

1. Cada fila de la tabla cuenta con el botón rojo **`Eliminar`**.
2. Al presionarlo, el sistema solicita confirmación:  
   `¿Eliminar la factura XXXX?`
3. Al hacer click en **Aceptar**:
   - Se envía una petición `DELETE` al servidor.
   - La factura y todos sus detalles asociados se eliminan físicamente de la base de datos (`orphanRemoval`).
   - Aparece la notificación verde en pantalla y la fila desaparece de la grilla.

---

### Función 5: Manejo Visual de Errores y Validaciones

- Si se intenta guardar una factura sin fecha, sin punto de venta o con la tabla de ítems vacía, el sistema no envía la petición y muestra una alerta roja en pantalla indicando el dato faltante.
- Si se ingresa un ID de artículo, cliente o punto de venta que no existe en la base de datos, el backend responde con un error `404 Not Found` vía `@RestControllerAdvice` y la interfaz muestra amigablemente el mensaje devuelto por el servidor (ej: *"Error al guardar la factura: Error 404: No existe el articulo con id 999"*).

---

## 📋 Endpoints de la API REST (Para pruebas directas / Postman)

Base URL: `http://localhost:8080/api/facturas`

| Método | Ruta | Parámetros / Body | Código Respuesta | Descripción |
| :--- | :--- | :--- | :---: | :--- |
| **GET** | `/api/facturas` | Query params: `fechaDesde`, `fechaHasta`, `estado`, `montoMinimo` | `200 OK` | Retorna lista de `FacturaReporteDTO`. |
| **POST** | `/api/facturas` | Body JSON: `FacturaRequestDTO` | `201 Created` | Crea factura y detalles en cascada. Retorna `FacturaReporteDTO`. |
| **PUT** | `/api/facturas/{id}` | Path: `{id}`, Body JSON: `{ "estado": "ANULADA", "observaciones": "..." }` | `200 OK` | Actualiza estado y observaciones. |
| **DELETE** | `/api/facturas/{id}` | Path: `{id}` | `204 No Content` | Elimina la factura y sus detalles. |
