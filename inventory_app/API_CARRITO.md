# 🛒 Guía de APIs - Carrito de Compras Multi-Tenant

**Fecha:** 2025-12-06  
**Versión:** 1.0  
**Endpoint Base:** `http://localhost:8080/api/carrito`

---

## 🔐 Autenticación Requerida

**Todos los endpoints del carrito requieren autenticación con JWT DE EMPLEADO.**

⚠️ **IMPORTANTE:** Solo empleados autenticados pueden usar el carrito. Cada empleado tiene su propio carrito temporal.

**Header requerido:**
```
Authorization: Bearer {TOKEN_EMPLEADO}
```

El sistema automáticamente:
1. Extrae el `empleadoId` del JWT
2. Configura el `TenantContext` con el schema correcto
3. Todas las operaciones trabajan en el carrito del empleado autenticado

---

## 📋 ÍNDICE DE ENDPOINTS

1. [Agregar Producto](#1-agregar-producto-al-carrito) - `POST /api/carrito/agregar`
2. [Obtener Carrito](#2-obtener-carrito-del-empleado) - `GET /api/carrito`
3. [Actualizar Cantidad](#3-actualizar-cantidad-de-un-item) - `PUT /api/carrito/item/{id}`
4. [Eliminar Item](#4-eliminar-item-del-carrito) - `DELETE /api/carrito/item/{id}`
5. [Vaciar Carrito](#5-vaciar-carrito-completo) - `DELETE /api/carrito/vaciar`
6. [Resumen del Carrito](#6-obtener-resumen-del-carrito) - `GET /api/carrito/resumen`

---

## 🚀 FUNCIONALIDADES PRINCIPALES

### 1. Agregar Producto al Carrito

**Endpoint:** `POST /api/carrito/agregar`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {TOKEN_EMPLEADO}
```

**Body:**
```json
{
  "productoId": 1,
  "cantidad": 2
}
```

**Campos:**
- `productoId` ✅ **Requerido** - ID del producto a agregar
- `cantidad` ✅ **Requerido** - Cantidad a agregar (mínimo 1)

**Respuesta Exitosa (200 OK):**
```json
{
  "success": true,
  "message": "Producto agregado al carrito",
  "item": {
    "id": 1,
    "productoId": 1,
    "cantidad": 2,
    "precioUnitario": 1500000.00,
    "subtotal": 3000000.00,
    "createdAt": "2025-12-06T16:00:00.000+00:00",
    "producto": {
      "codigo": "PROD001",
      "nombre": "Laptop Dell Inspiron 15",
      "categoria": "ELECTRONICA",
      "stockDisponible": 23,
      "activo": true
    }
  },
  "totalCarrito": 3000000.00,
  "cantidadItems": 1,
  "schemaName": "empresa_3"
}
```

**Lógica Automática:**
- ✅ Si el producto YA está en el carrito → **incrementa la cantidad**
- ✅ Si el producto NO está en el carrito → **crea nuevo item**
- ✅ Valida que haya stock suficiente antes de agregar
- ✅ Valida que el producto esté activo
- ✅ Captura el precio actual del producto

**Errores Comunes:**
```json
{
  "success": false,
  "message": "Producto no encontrado"
}
```

```json
{
  "success": false,
  "message": "Stock insuficiente. Disponible: 5"
}
```

```json
{
  "success": false,
  "message": "El producto no está disponible"
}
```

---

### 2. Obtener Carrito del Empleado

**Endpoint:** `GET /api/carrito`

**Headers:**
```
Authorization: Bearer {TOKEN_EMPLEADO}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "items": [
    {
      "id": 1,
      "productoId": 1,
      "cantidad": 2,
      "precioUnitario": 1500000.00,
      "subtotal": 3000000.00,
      "createdAt": "2025-12-06T16:00:00.000+00:00",
      "producto": {
        "codigo": "PROD001",
        "nombre": "Laptop Dell Inspiron 15",
        "categoria": "ELECTRONICA",
        "stockDisponible": 23,
        "activo": true
      }
    },
    {
      "id": 2,
      "productoId": 2,
      "cantidad": 1,
      "precioUnitario": 85000.00,
      "subtotal": 85000.00,
      "createdAt": "2025-12-06T16:05:00.000+00:00",
      "producto": {
        "codigo": "PROD002",
        "nombre": "Mouse Logitech MX Master",
        "categoria": "ELECTRONICA",
        "stockDisponible": 49,
        "activo": true
      }
    }
  ],
  "cantidadItems": 2,
  "total": 3085000.00,
  "schemaName": "empresa_3"
}
```

**Caso: Carrito vacío**
```json
{
  "success": true,
  "items": [],
  "cantidadItems": 0,
  "total": 0.00,
  "schemaName": "empresa_3"
}
```

---

### 3. Actualizar Cantidad de un Item

**Endpoint:** `PUT /api/carrito/item/{id}`

**Ejemplo:** `PUT /api/carrito/item/1`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {TOKEN_EMPLEADO}
```

**Body:**
```json
{
  "cantidad": 5
}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "message": "Cantidad actualizada",
  "item": {
    "id": 1,
    "productoId": 1,
    "cantidad": 5,
    "precioUnitario": 1500000.00,
    "subtotal": 7500000.00,
    "producto": {
      "codigo": "PROD001",
      "nombre": "Laptop Dell Inspiron 15",
      "categoria": "ELECTRONICA",
      "stockDisponible": 20,
      "activo": true
    }
  },
  "totalCarrito": 7585000.00,
  "schemaName": "empresa_3"
}
```

**Validaciones:**
- ✅ Verifica stock disponible antes de actualizar
- ✅ La cantidad debe ser mínimo 1
- ✅ Si quieres eliminar el item, usa el endpoint DELETE

**Error: Stock insuficiente**
```json
{
  "success": false,
  "message": "Stock insuficiente. Disponible: 3"
}
```

---

### 4. Eliminar Item del Carrito

**Endpoint:** `DELETE /api/carrito/item/{id}`

**Ejemplo:** `DELETE /api/carrito/item/1`

**Headers:**
```
Authorization: Bearer {TOKEN_EMPLEADO}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "message": "Item eliminado del carrito",
  "totalCarrito": 85000.00,
  "cantidadItems": 1,
  "schemaName": "empresa_3"
}
```

---

### 5. Vaciar Carrito Completo

**Endpoint:** `DELETE /api/carrito/vaciar`

**Headers:**
```
Authorization: Bearer {TOKEN_EMPLEADO}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "message": "Carrito vaciado exitosamente",
  "schemaName": "empresa_3"
}
```

**Uso:** 
- Después de generar una factura
- Cuando el empleado quiere empezar de cero
- Al finalizar el turno

---

### 6. Obtener Resumen del Carrito

**Endpoint:** `GET /api/carrito/resumen`

**Headers:**
```
Authorization: Bearer {TOKEN_EMPLEADO}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "total": 3085000.00,
  "cantidadItems": 2,
  "schemaName": "empresa_3"
}
```

**Uso:** 
- Para mostrar badge con cantidad de items
- Para mostrar total sin cargar todos los detalles
- Actualización rápida del carrito en UI

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
    "apellido": "Vendedor",
    "rol": "VENDEDOR"
  },
  "schemaName": "empresa_3"
}
```

### Paso 2: Agregar Productos al Carrito

```bash
# Agregar Laptop
POST http://localhost:8080/api/carrito/agregar
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Body:
{
  "productoId": 1,
  "cantidad": 2
}

# Agregar Mouse
POST http://localhost:8080/api/carrito/agregar
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Body:
{
  "productoId": 2,
  "cantidad": 1
}
```

### Paso 3: Ver Carrito Completo

```bash
GET http://localhost:8080/api/carrito
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Paso 4: Actualizar Cantidad de un Item

```bash
PUT http://localhost:8080/api/carrito/item/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Body:
{
  "cantidad": 3
}
```

### Paso 5: Obtener Resumen (para el Badge)

```bash
GET http://localhost:8080/api/carrito/resumen
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Paso 6: Eliminar un Item

```bash
DELETE http://localhost:8080/api/carrito/item/2
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Paso 7: Vaciar Carrito (después de facturar)

```bash
DELETE http://localhost:8080/api/carrito/vaciar
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## ⚠️ CONSIDERACIONES IMPORTANTES

### Multi-Tenancy
- ✅ Cada empleado tiene su **propio carrito** aislado
- ✅ Los carritos son **específicos del schema** del tenant
- ✅ No es posible ver carritos de otros empleados o empresas

### Validaciones Automáticas
- ✅ **Stock disponible** - Verifica antes de agregar/actualizar
- ✅ **Producto activo** - Solo productos activos se pueden agregar
- ✅ **Precio actual** - Captura el precio del momento
- ✅ **Cantidad mínima** - Debe ser al menos 1

### Lógica de Negocio
- 🔄 **Agregar producto existente** → Incrementa cantidad automáticamente
- 🔄 **Precio congelado** → El precio se captura al agregar (no cambia si el producto se actualiza)
- 📅 **Temporal** - El carrito es temporal hasta generar factura
- 🧹 **Limpieza** - Se vacía después de crear factura

### Seguridad
- 🔒 Solo **empleados autenticados** pueden usar el carrito
- 🔒 Cada empleado solo ve **su propio carrito**
- 🔒 Las empresas no pueden acceder al carrito (solo empleados)

---

## 🔗 INTEGRACIÓN CON OTROS MÓDULOS

### Carrito → Facturas

Cuando el empleado esté listo para facturar:

```bash
# 1. Ver el carrito completo
GET /api/carrito

# 2. Crear factura con los productos del carrito
POST /api/facturas
{
  "clienteId": 1,
  "empleadoId": 2,  # ID del empleado autenticado
  "detalles": [
    {
      "productoId": 1,
      "cantidad": 2,
      "descuento": 0
    }
  ]
}

# 3. Vaciar el carrito después de facturar
DELETE /api/carrito/vaciar
```

### Carrito → Productos

El carrito siempre muestra información actualizada del producto:

```json
{
  "producto": {
    "stockDisponible": 23,  // Stock actual del producto
    "activo": true          // Estado actual del producto
  }
}
```

---

## 🐛 Troubleshooting

### Error: "Se requiere autenticación de empleado"
**Causa:** El JWT es de empresa, no de empleado  
**Solución:** Usa el endpoint `/api/auth/login` para empleados

### Error: "Stock insuficiente. Disponible: X"
**Causa:** No hay suficiente stock para la cantidad solicitada  
**Solución:** Reduce la cantidad o verifica el stock disponible

### Error: "El producto no está disponible"
**Causa:** El producto fue desactivado  
**Solución:** Elimina el item del carrito y selecciona otro producto

### Error: "Producto no encontrado"
**Causa:** El ID del producto no existe o fue eliminado  
**Solución:** Verifica que el ID sea correcto

---

## 💡 BUENAS PRÁCTICAS

### 1. **Actualizar UI en tiempo real**
```javascript
// Después de cada operación, actualizar el badge
GET /api/carrito/resumen
```

### 2. **Validar stock antes de facturar**
```javascript
// Verificar que todos los items tengan stock disponible
GET /api/carrito
// Revisar campo: producto.stockDisponible
```

### 3. **Limpiar carrito después de facturar**
```javascript
// Siempre vaciar después de crear factura exitosa
DELETE /api/carrito/vaciar
```

### 4. **Mostrar información del producto**
```javascript
// Usar la información del producto incluida en el item
item.producto.nombre
item.producto.stockDisponible
```

---

## 📚 Próximos Pasos

1. ✅ CRUD de Carrito funcionando
2. ✅ CRUD de Clientes funcionando
3. ✅ CRUD de Productos funcionando
4. ⏳ Implementar generación de Facturas desde carrito
5. ⏳ Implementar reportes de ventas

**🎉 ¡La API de Carrito de Compras está lista para usar!**