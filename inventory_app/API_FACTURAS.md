# 🧾 Guía de APIs - Gestión de Facturas Multi-Tenant

**Fecha:** 2025-12-06  
**Versión:** 1.0  
**Endpoint Base:** `http://localhost:8080/api/facturas`

---

## 🔐 Autenticación Requerida

**Todos los endpoints de facturas requieren autenticación con JWT.**

Puedes usar:
- **JWT de Empresa** (obtenido del login de empresa)
- **JWT de Empleado** (obtenido del login de empleado)

**Header requerido:**
```
Authorization: Bearer {TOKEN}
```

El sistema automáticamente:
1. Extrae el `schemaName` del JWT
2. Configura el `TenantContext`
3. Todas las operaciones se ejecutan en el schema del tenant
4. **Actualiza automáticamente el stock** de productos al crear/anular facturas

---

## 📋 ÍNDICE DE ENDPOINTS

1. [Crear Factura](#1-crear-factura) - `POST /api/facturas`
2. [Listar Todas las Facturas](#2-listar-todas-las-facturas) - `GET /api/facturas`
3. [Obtener Factura por ID](#3-obtener-factura-por-id) - `GET /api/facturas/{id}`
4. [Obtener por Número de Factura](#4-obtener-por-número-de-factura) - `GET /api/facturas/numero/{numeroFactura}`
5. [Listar por Cliente](#5-listar-facturas-por-cliente) - `GET /api/facturas/cliente/{clienteId}`
6. [Listar por Empleado](#6-listar-facturas-por-empleado) - `GET /api/facturas/empleado/{empleadoId}`
7. [Listar por Rango de Fechas](#7-listar-por-rango-de-fechas) - `GET /api/facturas/fecha`
8. [Listar por Estado](#8-listar-por-estado) - `GET /api/facturas/estado/{estado}`
9. [Total Ventas del Día](#9-total-ventas-del-día) - `GET /api/facturas/ventas-dia`
10. [Anular Factura](#10-anular-factura) - `PATCH /api/facturas/{id}/anular`
11. [Eliminar Factura](#11-eliminar-factura) - `DELETE /api/facturas/{id}`

---

## 🚀 CRUD COMPLETO

### 1. Crear Factura

**Endpoint:** `POST /api/facturas`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {TOKEN}
```

**Body:**
```json
{
  "clienteId": 1,
  "empleadoId": 2,
  "metodoPago": "EFECTIVO",
  "impuesto": 0,
  "descuento": 0,
  "notas": "Factura de prueba",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2,
      "descuento": 0
    },
    {
      "productoId": 2,
      "cantidad": 1,
      "descuento": 5000
    }
  ]
}
```

**Campos:**
- `clienteId` ✅ **Requerido** - ID del cliente (debe existir en el schema)
- `empleadoId` ✅ **Requerido** - ID del empleado que registra la venta
- `metodoPago` - Opcional (EFECTIVO, TARJETA, TRANSFERENCIA, etc.)
- `impuesto` - Opcional, valor del impuesto (ej: IVA)
- `descuento` - Opcional, descuento general de la factura
- `notas` - Opcional, observaciones de la factura
- `detalles` ✅ **Requerido** - Array con al menos 1 producto
  - `productoId` ✅ **Requerido** - ID del producto
  - `cantidad` ✅ **Requerido** - Cantidad a vender (mínimo 1)
  - `descuento` - Opcional, descuento específico del producto

**Campos Automáticos (NO enviar):**
- `id` - Generado por la BD
- `numeroFactura` - Generado automáticamente (FAC-timestamp)
- `fecha` - Se establece automáticamente
- `subtotal` - Calculado automáticamente
- `total` - Calculado automáticamente
- `estado` - Siempre "COMPLETADA" al crear
- `createdAt` - Se establece automáticamente
- `updatedAt` - Se establece automáticamente

**Respuesta Exitosa (201 CREATED):**
```json
{
  "success": true,
  "message": "Factura creada exitosamente",
  "factura": {
    "id": 1,
    "numeroFactura": "FAC-1733533200000",
    "clienteId": 1,
    "empleadoId": 2,
    "fecha": "2025-12-06T20:30:00.000+00:00",
    "subtotal": 3080000.00,
    "impuesto": 0.00,
    "descuento": 0.00,
    "total": 3080000.00,
    "metodoPago": "EFECTIVO",
    "estado": "COMPLETADA",
    "notas": "Factura de prueba",
    "detalles": [
      {
        "id": 1,
        "productoId": 1,
        "productoCodigo": "PROD001",
        "productoNombre": "Laptop Dell Inspiron 15",
        "productoCategoria": "ELECTRONICA",
        "cantidad": 2,
        "precioUnitario": 1500000.00,
        "descuento": 0.00,
        "subtotal": 3000000.00
      },
      {
        "id": 2,
        "productoId": 2,
        "productoCodigo": "PROD002",
        "productoNombre": "Mouse Logitech MX Master",
        "productoCategoria": "ELECTRONICA",
        "cantidad": 1,
        "precioUnitario": 85000.00,
        "descuento": 5000.00,
        "subtotal": 80000.00
      }
    ],
    "createdAt": "2025-12-06T20:30:00.000+00:00"
  },
  "schemaName": "empresa_4"
}
```

**Lógica Automática:**
- ✅ Captura el **precio actual** del producto al momento de facturar
- ✅ Calcula **subtotales** por cada detalle: (precio × cantidad) - descuento
- ✅ Calcula **subtotal general**: suma de todos los subtotales
- ✅ Calcula **total**: subtotal + impuesto - descuento general
- ✅ **Reduce el stock** de los productos automáticamente
- ✅ Genera **número de factura** único
- ✅ Almacena información del producto (código, nombre, categoría) para historial

**Errores Comunes:**
```json
{
  "success": false,
  "message": "Cliente no encontrado"
}
```

```json
{
  "success": false,
  "message": "Producto no encontrado: 99"
}
```

```json
{
  "success": false,
  "message": "Stock insuficiente"
}
```

---

### 2. Listar Todas las Facturas

**Endpoint:** `GET /api/facturas`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "facturas": [
    {
      "id": 1,
      "numeroFactura": "FAC-1733533200000",
      "clienteId": 1,
      "empleadoId": 2,
      "fecha": "2025-12-06T20:30:00.000+00:00",
      "subtotal": 3080000.00,
      "impuesto": 0.00,
      "descuento": 0.00,
      "total": 3080000.00,
      "metodoPago": "EFECTIVO",
      "estado": "COMPLETADA",
      "detalles": [...],
      "createdAt": "2025-12-06T20:30:00.000+00:00"
    }
  ],
  "total": 1,
  "schemaName": "empresa_4"
}
```

---

### 3. Obtener Factura por ID

**Endpoint:** `GET /api/facturas/{id}`

**Ejemplo:** `GET /api/facturas/1`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "factura": {
    "id": 1,
    "numeroFactura": "FAC-1733533200000",
    "clienteId": 1,
    "empleadoId": 2,
    "fecha": "2025-12-06T20:30:00.000+00:00",
    "subtotal": 3080000.00,
    "total": 3080000.00,
    "metodoPago": "EFECTIVO",
    "estado": "COMPLETADA",
    "detalles": [...]
  },
  "schemaName": "empresa_4"
}
```

**Respuesta (404 Not Found):**
```json
{
  "success": false,
  "message": "Factura no encontrada"
}
```

---

### 4. Obtener por Número de Factura

**Endpoint:** `GET /api/facturas/numero/{numeroFactura}`

**Ejemplo:** `GET /api/facturas/numero/FAC-1733533200000`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta:** Igual que "Obtener por ID"

**Uso:** Útil para búsquedas rápidas cuando el cliente tiene el número de factura.

---

### 5. Listar Facturas por Cliente

**Endpoint:** `GET /api/facturas/cliente/{clienteId}`

**Ejemplo:** `GET /api/facturas/cliente/1`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "facturas": [
    {
      "id": 1,
      "numeroFactura": "FAC-1733533200000",
      "clienteId": 1,
      "total": 3080000.00,
      "estado": "COMPLETADA"
    },
    {
      "id": 3,
      "numeroFactura": "FAC-1733540000000",
      "clienteId": 1,
      "total": 500000.00,
      "estado": "COMPLETADA"
    }
  ],
  "total": 2,
  "schemaName": "empresa_4"
}
```

**Uso:** Historial de compras del cliente.

---

### 6. Listar Facturas por Empleado

**Endpoint:** `GET /api/facturas/empleado/{empleadoId}`

**Ejemplo:** `GET /api/facturas/empleado/2`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "facturas": [...],
  "total": 5,
  "schemaName": "empresa_4"
}
```

**Uso:** Reporte de ventas por vendedor.

---

### 7. Listar por Rango de Fechas

**Endpoint:** `GET /api/facturas/fecha?fechaInicio=2025-12-01&fechaFin=2025-12-31`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Query Params:**
- `fechaInicio` ✅ **Requerido** - Formato: `yyyy-MM-dd`
- `fechaFin` ✅ **Requerido** - Formato: `yyyy-MM-dd`

**Ejemplo:** `GET /api/facturas/fecha?fechaInicio=2025-12-01&fechaFin=2025-12-31`

**Respuesta (200 OK):**
```json
{
  "success": true,
  "facturas": [...],
  "total": 10,
  "schemaName": "empresa_4"
}
```

**Uso:** Reportes mensuales, semanales, etc.

---

### 8. Listar por Estado

**Endpoint:** `GET /api/facturas/estado/{estado}`

**Ejemplo:** `GET /api/facturas/estado/COMPLETADA`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Estados Válidos:**
- `COMPLETADA` - Factura finalizada exitosamente
- `ANULADA` - Factura anulada (stock devuelto)
- `PENDIENTE` - (Si se implementa facturación a crédito)

**Respuesta (200 OK):**
```json
{
  "success": true,
  "facturas": [...],
  "total": 8,
  "estado": "COMPLETADA",
  "schemaName": "empresa_4"
}
```

---

### 9. Total Ventas del Día

**Endpoint:** `GET /api/facturas/ventas-dia?fecha=2025-12-06`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Query Params:**
- `fecha` ✅ **Requerido** - Formato: `yyyy-MM-dd`

**Ejemplo:** `GET /api/facturas/ventas-dia?fecha=2025-12-06`

**Respuesta (200 OK):**
```json
{
  "success": true,
  "fecha": "2025-12-06T00:00:00.000+00:00",
  "totalVentas": 5580000.00,
  "schemaName": "empresa_4"
}
```

**Uso:** Cierre de caja, reportes diarios.

**Nota:** Solo suma facturas con estado `COMPLETADA`.

---

### 10. Anular Factura

**Endpoint:** `PATCH /api/facturas/{id}/anular`

**Ejemplo:** `PATCH /api/facturas/1/anular`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "message": "Factura anulada exitosamente",
  "schemaName": "empresa_4"
}
```

**⚠️ IMPORTANTE:** Esta operación:
- ✅ Cambia el estado de la factura a `ANULADA`
- ✅ **Devuelve el stock** de todos los productos de la factura
- ✅ Mantiene el registro histórico
- ❌ NO elimina la factura de la BD

**Ejemplo:** Si la factura tenía 2 laptops, al anularla se suman +2 al stock.

---

### 11. Eliminar Factura

**Endpoint:** `DELETE /api/facturas/{id}`

**Ejemplo:** `DELETE /api/facturas/1`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "message": "Factura eliminada exitosamente",
  "schemaName": "empresa_4"
}
```

**⚠️ IMPORTANTE:** Esto es una **eliminación física** (hard delete). La factura se elimina permanentemente.

**Recomendación:** Usar **ANULAR** en lugar de **ELIMINAR** para mantener historial.

---

## 🧪 EJEMPLO COMPLETO DE FLUJO

### Paso 1: Login de Empleado

```bash
POST http://localhost:8080/api/auth/login

Body:
{
  "usuario": "juan.vendedor",
  "password": "Vendedor@2024",
  "tenantKey": "techstore-colombia-sas"
}

Respuesta:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "empleado": {
    "id": 2,
    "nombre": "Juan",
    "rol": "VENDEDOR"
  },
  "schemaName": "empresa_4"
}
```

### Paso 2: Verificar Cliente (Opcional)

```bash
GET http://localhost:8080/api/clientes/documento/1234567890
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Paso 3: Agregar Productos al Carrito

```bash
POST http://localhost:8080/api/carrito/agregar
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Body:
{
  "productoId": 1,
  "cantidad": 2
}
```

### Paso 4: Ver Carrito

```bash
GET http://localhost:8080/api/carrito
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Paso 5: Crear Factura

```bash
POST http://localhost:8080/api/facturas
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Body:
{
  "clienteId": 1,
  "empleadoId": 2,
  "metodoPago": "EFECTIVO",
  "impuesto": 0,
  "descuento": 0,
  "notas": "Venta realizada en tienda",
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2,
      "descuento": 0
    }
  ]
}
```

### Paso 6: Vaciar Carrito

```bash
DELETE http://localhost:8080/api/carrito/vaciar
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Paso 7: Consultar Total Ventas del Día

```bash
GET http://localhost:8080/api/facturas/ventas-dia?fecha=2025-12-06
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## ⚠️ CONSIDERACIONES IMPORTANTES

### Multi-Tenancy
- ✅ Cada empresa tiene sus **propias facturas** aisladas
- ✅ No es posible ver facturas de otras empresas
- ✅ El schema se configura automáticamente desde el JWT

### Gestión de Stock
- ✅ **Crear factura** → Reduce stock automáticamente
- ✅ **Anular factura** → Devuelve stock automáticamente
- ✅ Si un producto queda sin stock, se **desactiva** automáticamente

### Cálculos Automáticos
- 📊 **Subtotal detalle** = (precio × cantidad) - descuento detalle
- 📊 **Subtotal factura** = Suma de todos los subtotales de detalles
- 📊 **Total factura** = Subtotal + Impuesto - Descuento general

### Datos Históricos
- 📅 La factura **almacena** código, nombre y categoría del producto
- 📅 Si el producto se modifica después, la factura mantiene los datos originales
- 📅 El precio se **congela** al momento de crear la factura

### Seguridad
- 🔒 Todos los endpoints requieren **JWT válido**
- 🔒 Solo se pueden gestionar facturas del **propio tenant**
- 🔒 El empleado debe existir y estar activo

---

## 🐛 Troubleshooting

### Error: "Cliente no encontrado"
**Causa:** El clienteId no existe en el schema del tenant  
**Solución:** Verifica que el cliente exista con `GET /api/clientes/{id}`

### Error: "Producto no encontrado: X"
**Causa:** El productoId no existe o fue eliminado  
**Solución:** Verifica que el producto exista con `GET /api/productos/{id}`

### Error: "Stock insuficiente"
**Causa:** No hay suficiente stock del producto  
**Solución:** Verifica el stock disponible antes de facturar

### Error: "Error: No se pudo determinar el tenant"
**Causa:** El JWT no contiene schemaName válido  
**Solución:** Verifica que estés usando un JWT de empresa o empleado válido

---

## 💡 BUENAS PRÁCTICAS

### 1. **Flujo Recomendado**
```
Login → Buscar Cliente → Agregar al Carrito → Crear Factura → Vaciar Carrito
```

### 2. **Validar Stock Antes**
```javascript
// Antes de crear la factura, verificar stock
GET /api/productos/{id}
// Revisar campo: stock >= cantidad solicitada
```

### 3. **Usar Anular en lugar de Eliminar**
```javascript
// Preferir anular para mantener historial
PATCH /api/facturas/{id}/anular
// En lugar de DELETE /api/facturas/{id}
```

### 4. **Reportes Periódicos**
```javascript
// Cierre de caja diario
GET /api/facturas/ventas-dia?fecha=2025-12-06

// Reporte mensual
GET /api/facturas/fecha?fechaInicio=2025-12-01&fechaFin=2025-12-31
```

---

## 📚 Próximos Pasos

1. ✅ CRUD de Facturas funcionando
2. ✅ Integración con Productos (stock automático)
3. ✅ Integración con Clientes
4. ✅ Integración con Carrito
5. ⏳ Implementar reportes avanzados de ventas
6. ⏳ Implementar exportación a PDF

**🎉 ¡La API de Facturas está lista para usar!**