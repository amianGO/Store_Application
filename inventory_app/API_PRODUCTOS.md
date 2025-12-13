# 📦 Guía de APIs - Gestión de Productos Multi-Tenant

**Fecha:** 2025-12-06  
**Versión:** 1.0  
**Endpoint Base:** `http://localhost:8080/api/productos`

---

## 🔐 Autenticación Requerida

**Todos los endpoints de productos requieren autenticación con JWT.**

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

---

## 📋 ÍNDICE DE ENDPOINTS

1. [Crear Producto](#1-crear-producto) - `POST /api/productos`
2. [Listar Todos los Productos](#2-listar-todos-los-productos) - `GET /api/productos`
3. [Obtener Producto por ID](#3-obtener-producto-por-id) - `GET /api/productos/{id}`
4. [Obtener Producto por Código](#4-obtener-producto-por-código) - `GET /api/productos/codigo/{codigo}`
5. [Actualizar Producto](#5-actualizar-producto) - `PUT /api/productos/{id}`
6. [Actualizar Stock](#6-actualizar-stock) - `PATCH /api/productos/{id}/stock`
7. [Eliminar Producto](#7-eliminar-producto) - `DELETE /api/productos/{id}`
8. [Buscar por Categoría](#8-buscar-por-categoría) - `GET /api/productos/categoria/{categoria}`
9. [Productos con Bajo Stock](#9-productos-con-bajo-stock) - `GET /api/productos/bajo-stock`
10. [Buscar por Rango de Precio](#10-buscar-por-rango-de-precio) - `GET /api/productos/rango-precio`
11. [Buscar por Nombre](#11-buscar-por-nombre) - `GET /api/productos/buscar`

---

## 📝 CATEGORÍAS DISPONIBLES

```
ELECTRONICA
ROPA
ALIMENTOS
BEBIDAS
HOGAR
SALUD
DEPORTES
JUGUETES
LIBROS
OTROS
```

---

## 🚀 CRUD COMPLETO

### 1. Crear Producto

**Endpoint:** `POST /api/productos`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {TOKEN}
```

**Body:**
```json
{
  "codigo": "PROD001",
  "nombre": "Laptop Dell Inspiron 15",
  "descripcion": "Laptop con procesador Intel Core i5, 8GB RAM, 256GB SSD",
  "precioCompra": 1200000,
  "precioVenta": 1500000,
  "stock": 25,
  "stockMinimo": 5,
  "categoria": "ELECTRONICA"
}
```

**Campos:**
- `codigo` ✅ **Requerido** - Único, máx 50 caracteres
- `nombre` ✅ **Requerido** - Máx 200 caracteres
- `descripcion` - Opcional
- `precioCompra` ✅ **Requerido** - Debe ser > 0
- `precioVenta` ✅ **Requerido** - Debe ser > 0
- `stock` ✅ **Requerido** - Debe ser >= 0
- `stockMinimo` ✅ **Requerido** - Debe ser >= 0
- `categoria` ✅ **Requerido** - Ver categorías disponibles

**Campos Automáticos (NO enviar):**
- `id` - Generado por la BD
- `createdAt` - Se establece automáticamente
- `updatedAt` - Se establece automáticamente
- `activo` - Siempre `true` al crear

**Respuesta Exitosa (201 CREATED):**
```json
{
  "success": true,
  "message": "Producto creado exitosamente",
  "producto": {
    "id": 1,
    "codigo": "PROD001",
    "nombre": "Laptop Dell Inspiron 15",
    "descripcion": "Laptop con procesador Intel Core i5, 8GB RAM, 256GB SSD",
    "precioCompra": 1200000.00,
    "precioVenta": 1500000.00,
    "stock": 25,
    "stockMinimo": 5,
    "categoria": "ELECTRONICA",
    "createdAt": "2025-12-06T15:30:00.000+00:00",
    "updatedAt": "2025-12-06T15:30:00.000+00:00",
    "activo": true
  },
  "tenantInfo": {
    "schemaName": "empresa_3",
    "empresaId": 3
  }
}
```

**Errores Comunes:**
- `400 Bad Request` - Datos inválidos o código duplicado
- `401 Unauthorized` - JWT inválido o expirado
- `500 Internal Server Error` - Error del servidor

---

### 2. Listar Todos los Productos

**Endpoint:** `GET /api/productos`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "productos": [
    {
      "id": 1,
      "codigo": "PROD001",
      "nombre": "Laptop Dell Inspiron 15",
      "precioVenta": 1500000.00,
      "stock": 25,
      "categoria": "ELECTRONICA",
      "activo": true
    },
    {
      "id": 2,
      "codigo": "PROD002",
      "nombre": "Mouse Logitech MX Master",
      "precioVenta": 85000.00,
      "stock": 50,
      "categoria": "ELECTRONICA",
      "activo": true
    }
  ],
  "total": 2,
  "schemaName": "empresa_3"
}
```

---

### 3. Obtener Producto por ID

**Endpoint:** `GET /api/productos/{id}`

**Ejemplo:** `GET /api/productos/1`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "producto": {
    "id": 1,
    "codigo": "PROD001",
    "nombre": "Laptop Dell Inspiron 15",
    "descripcion": "Laptop con procesador Intel Core i5, 8GB RAM, 256GB SSD",
    "precioCompra": 1200000.00,
    "precioVenta": 1500000.00,
    "stock": 25,
    "stockMinimo": 5,
    "categoria": "ELECTRONICA",
    "createdAt": "2025-12-06T15:30:00.000+00:00",
    "updatedAt": "2025-12-06T15:30:00.000+00:00",
    "activo": true
  },
  "schemaName": "empresa_3"
}
```

**Respuesta (404 Not Found):**
```json
{
  "success": false,
  "message": "Producto no encontrado"
}
```

---

### 4. Obtener Producto por Código

**Endpoint:** `GET /api/productos/codigo/{codigo}`

**Ejemplo:** `GET /api/productos/codigo/PROD001`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta:** Igual que "Obtener por ID"

---

### 5. Actualizar Producto

**Endpoint:** `PUT /api/productos/{id}`

**Ejemplo:** `PUT /api/productos/1`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {TOKEN}
```

**Body (enviar TODOS los campos):**
```json
{
  "codigo": "PROD001",
  "nombre": "Laptop Dell Inspiron 15 - Actualizado",
  "descripcion": "Laptop con procesador Intel Core i7, 16GB RAM, 512GB SSD",
  "precioCompra": 1400000,
  "precioVenta": 1800000,
  "stock": 30,
  "stockMinimo": 5,
  "categoria": "ELECTRONICA",
  "activo": true
}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "message": "Producto actualizado exitosamente",
  "producto": {
    "id": 1,
    "codigo": "PROD001",
    "nombre": "Laptop Dell Inspiron 15 - Actualizado",
    "descripcion": "Laptop con procesador Intel Core i7, 16GB RAM, 512GB SSD",
    "precioCompra": 1400000.00,
    "precioVenta": 1800000.00,
    "stock": 30,
    "stockMinimo": 5,
    "categoria": "ELECTRONICA",
    "updatedAt": "2025-12-06T16:00:00.000+00:00",
    "activo": true
  },
  "schemaName": "empresa_3"
}
```

---

### 6. Actualizar Stock

**Endpoint:** `PATCH /api/productos/{id}/stock`

**Ejemplo:** `PATCH /api/productos/1/stock?cantidad=-5`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Query Params:**
- `cantidad` - Número positivo (incrementa) o negativo (decrementa)

**Ejemplos:**
- Agregar 10 unidades: `?cantidad=10`
- Quitar 5 unidades: `?cantidad=-5`

**Respuesta (200 OK):**
```json
{
  "success": true,
  "message": "Stock actualizado exitosamente",
  "producto": {
    "id": 1,
    "codigo": "PROD001",
    "nombre": "Laptop Dell Inspiron 15",
    "stock": 25,
    "activo": true
  },
  "schemaName": "empresa_3"
}
```

**Lógica Automática:**
- Si el stock llega a **0**, el producto se **desactiva** automáticamente
- Si se agrega stock a un producto **inactivo**, se **reactiva** automáticamente

**Error (400 Bad Request):**
```json
{
  "success": false,
  "message": "Error al actualizar stock: Stock insuficiente"
}
```

---

### 7. Eliminar Producto

**Endpoint:** `DELETE /api/productos/{id}`

**Ejemplo:** `DELETE /api/productos/1`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "message": "Producto eliminado exitosamente",
  "schemaName": "empresa_3"
}
```

**⚠️ IMPORTANTE:** Esto es una **eliminación física** (hard delete). El producto se elimina permanentemente de la base de datos.

---

## 🔍 BÚSQUEDAS Y FILTROS

### 8. Buscar por Categoría

**Endpoint:** `GET /api/productos/categoria/{categoria}`

**Ejemplo:** `GET /api/productos/categoria/ELECTRONICA`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "productos": [
    {
      "id": 1,
      "codigo": "PROD001",
      "nombre": "Laptop Dell Inspiron 15",
      "categoria": "ELECTRONICA",
      "stock": 25,
      "activo": true
    },
    {
      "id": 2,
      "codigo": "PROD002",
      "nombre": "Mouse Logitech MX Master",
      "categoria": "ELECTRONICA",
      "stock": 50,
      "activo": true
    }
  ],
  "total": 2,
  "categoria": "ELECTRONICA",
  "schemaName": "empresa_3"
}
```

---

### 9. Productos con Bajo Stock

**Endpoint:** `GET /api/productos/bajo-stock`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Descripción:** Retorna productos donde `stock < stockMinimo`

**Respuesta (200 OK):**
```json
{
  "success": true,
  "productos": [
    {
      "id": 3,
      "codigo": "PROD003",
      "nombre": "Teclado Mecánico",
      "stock": 3,
      "stockMinimo": 10,
      "activo": true
    }
  ],
  "total": 1,
  "schemaName": "empresa_3"
}
```

---

### 10. Buscar por Rango de Precio

**Endpoint:** `GET /api/productos/rango-precio?min=100000&max=500000`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Query Params:**
- `min` - Precio mínimo (requerido)
- `max` - Precio máximo (requerido)

**Ejemplo:** `GET /api/productos/rango-precio?min=50000&max=200000`

**Respuesta (200 OK):**
```json
{
  "success": true,
  "productos": [
    {
      "id": 2,
      "codigo": "PROD002",
      "nombre": "Mouse Logitech MX Master",
      "precioVenta": 85000.00,
      "stock": 50,
      "activo": true
    },
    {
      "id": 4,
      "codigo": "PROD004",
      "nombre": "Teclado Mecánico RGB",
      "precioVenta": 150000.00,
      "stock": 15,
      "activo": true
    }
  ],
  "total": 2,
  "rangoPrecio": {
    "min": 50000,
    "max": 200000
  },
  "schemaName": "empresa_3"
}
```

**Nota:** Solo muestra productos **activos** dentro del rango de precio.

---

### 11. Buscar por Nombre

**Endpoint:** `GET /api/productos/buscar?nombre=laptop`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Query Params:**
- `nombre` - Texto a buscar (búsqueda parcial, case-insensitive)

**Ejemplo:** `GET /api/productos/buscar?nombre=laptop`

**Respuesta (200 OK):**
```json
{
  "success": true,
  "productos": [
    {
      "id": 1,
      "codigo": "PROD001",
      "nombre": "Laptop Dell Inspiron 15",
      "precioVenta": 1500000.00,
      "stock": 25,
      "activo": true
    },
    {
      "id": 5,
      "codigo": "PROD005",
      "nombre": "Laptop HP Pavilion",
      "precioVenta": 1300000.00,
      "stock": 10,
      "activo": true
    }
  ],
  "total": 2,
  "busqueda": "laptop",
  "schemaName": "empresa_3"
}
```

**Nota:** 
- La búsqueda es **case-insensitive** (no distingue mayúsculas/minúsculas)
- Solo muestra productos **activos**
- Busca coincidencias parciales en el nombre

---

## 🧪 EJEMPLO COMPLETO DE FLUJO

### Paso 1: Login de Empresa

```bash
POST http://localhost:8080/api/auth/empresa/login

Body:
{
  "email": "admin@techstore.com",
  "password": "Tech@2024Store"
}

Respuesta:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "schemaName": "empresa_3"
}
```

### Paso 2: Crear Producto

```bash
POST http://localhost:8080/api/productos
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Body:
{
  "codigo": "ELEC001",
  "nombre": "iPhone 15 Pro",
  "descripcion": "Smartphone Apple iPhone 15 Pro, 256GB",
  "precioCompra": 4500000,
  "precioVenta": 5500000,
  "stock": 10,
  "stockMinimo": 3,
  "categoria": "ELECTRONICA"
}
```

### Paso 3: Listar Productos

```bash
GET http://localhost:8080/api/productos
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Paso 4: Actualizar Stock (Venta de 2 unidades)

```bash
PATCH http://localhost:8080/api/productos/1/stock?cantidad=-2
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Paso 5: Buscar Productos con Bajo Stock

```bash
GET http://localhost:8080/api/productos/bajo-stock
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

---

## ⚠️ CONSIDERACIONES IMPORTANTES

### Multi-Tenancy
- ✅ Cada empresa tiene sus **propios productos** aislados en su schema
- ✅ No es posible ver productos de otras empresas
- ✅ El schema se configura automáticamente desde el JWT

### Validaciones
- ✅ El `codigo` debe ser **único** dentro del schema del tenant
- ✅ Los precios deben ser **mayores a 0**
- ✅ El stock no puede ser **negativo**
- ✅ La categoría debe ser una de las **categorías válidas**

### Lógica de Negocio
- 🔄 **Stock = 0** → El producto se **desactiva** automáticamente
- 🔄 **Agregar stock a producto inactivo** → Se **reactiva** automáticamente
- 📅 **createdAt** y **updatedAt** se gestionan automáticamente

### Seguridad
- 🔒 Todos los endpoints requieren **JWT válido**
- 🔒 El JWT debe pertenecer a una **empresa o empleado activo**
- 🔒 Solo se pueden gestionar productos del **propio tenant**

---

## 🐛 Troubleshooting

### Error: "Error: No se pudo determinar el tenant"
**Causa:** El JWT no contiene `schemaName` o es `public`  
**Solución:** Verifica que estés usando un JWT de empresa o empleado válido

### Error: "Producto no encontrado"
**Causa:** El ID no existe en el schema del tenant  
**Solución:** Verifica que el ID sea correcto y pertenezca a tu empresa

### Error: "Stock insuficiente"
**Causa:** Intentas reducir el stock más de lo disponible  
**Solución:** Verifica el stock actual antes de la operación

### Error: "column 'fecha_registro' does not exist"
**Causa:** La entidad usa nombres de columna incorrectos  
**Solución:** Este error ya está corregido con `created_at` y `updated_at`

---

## 📚 Próximos Pasos

1. ✅ CRUD de Productos funcionando
2. ⏳ Implementar gestión de Clientes
3. ⏳ Implementar gestión de Facturas
4. ⏳ Implementar sistema de Carrito de Compras
5. ⏳ Implementar reportes de ventas

**🎉 ¡La API de Productos está lista para usar!**