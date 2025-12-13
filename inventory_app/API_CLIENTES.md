# 👥 Guía de APIs - Gestión de Clientes Multi-Tenant

**Fecha:** 2025-12-06  
**Versión:** 1.0  
**Endpoint Base:** `http://localhost:8080/api/clientes`

---

## 🔐 Autenticación Requerida

**Todos los endpoints de clientes requieren autenticación con JWT.**

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

1. [Crear Cliente](#1-crear-cliente) - `POST /api/clientes`
2. [Listar Todos los Clientes](#2-listar-todos-los-clientes) - `GET /api/clientes`
3. [Obtener Cliente por ID](#3-obtener-cliente-por-id) - `GET /api/clientes/{id}`
4. [Obtener Cliente por Documento](#4-obtener-cliente-por-documento) - `GET /api/clientes/documento/{documento}`
5. [Actualizar Cliente](#5-actualizar-cliente) - `PUT /api/clientes/{id}`
6. [Desactivar Cliente](#6-desactivar-cliente) - `DELETE /api/clientes/{id}`
7. [Listar Clientes Activos](#7-listar-clientes-activos) - `GET /api/clientes/activos`
8. [Buscar por Nombre](#8-buscar-por-nombre) - `GET /api/clientes/buscar`
9. [Buscar por Ciudad](#9-buscar-por-ciudad) - `GET /api/clientes/ciudad/{ciudad}`

---

## 🚀 CRUD COMPLETO

### 1. Crear Cliente

**Endpoint:** `POST /api/clientes`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {TOKEN}
```

**Body:**
```json
{
  "nombre": "Juan",
  "apellido": "Pérez García",
  "documento": "1234567890",
  "email": "juan.perez@email.com",
  "telefono": "+57 300 123 4567",
  "direccion": "Calle 50 #25-30, Apto 501",
  "ciudad": "Bogotá",
  "pais": "Colombia"
}
```

**Campos:**
- `nombre` ✅ **Requerido** - Máx 100 caracteres
- `apellido` ✅ **Requerido** - Máx 100 caracteres
- `documento` ✅ **Requerido** - Único, máx 20 caracteres
- `email` ✅ **Requerido** - Debe ser email válido, máx 150 caracteres
- `telefono` - Opcional, máx 20 caracteres
- `direccion` - Opcional, máx 200 caracteres
- `ciudad` - Opcional, máx 100 caracteres
- `pais` - Opcional, máx 50 caracteres

**Campos Automáticos (NO enviar):**
- `id` - Generado por la BD
- `createdAt` - Se establece automáticamente
- `updatedAt` - Se establece automáticamente
- `activo` - Siempre `true` al crear

**Respuesta Exitosa (201 CREATED):**
```json
{
  "success": true,
  "message": "Cliente creado exitosamente",
  "cliente": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez García",
    "documento": "1234567890",
    "email": "juan.perez@email.com",
    "telefono": "+57 300 123 4567",
    "direccion": "Calle 50 #25-30, Apto 501",
    "ciudad": "Bogotá",
    "pais": "Colombia",
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
- `400 Bad Request` - Datos inválidos o documento duplicado
- `401 Unauthorized` - JWT inválido o expirado
- `500 Internal Server Error` - Error del servidor

---

### 2. Listar Todos los Clientes

**Endpoint:** `GET /api/clientes`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "clientes": [
    {
      "id": 1,
      "nombre": "Juan",
      "apellido": "Pérez García",
      "documento": "1234567890",
      "email": "juan.perez@email.com",
      "telefono": "+57 300 123 4567",
      "ciudad": "Bogotá",
      "activo": true
    },
    {
      "id": 2,
      "nombre": "María",
      "apellido": "López Sánchez",
      "documento": "0987654321",
      "email": "maria.lopez@email.com",
      "telefono": "+57 310 987 6543",
      "ciudad": "Medellín",
      "activo": true
    }
  ],
  "total": 2,
  "schemaName": "empresa_3"
}
```

---

### 3. Obtener Cliente por ID

**Endpoint:** `GET /api/clientes/{id}`

**Ejemplo:** `GET /api/clientes/1`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "cliente": {
    "id": 1,
    "nombre": "Juan",
    "apellido": "Pérez García",
    "documento": "1234567890",
    "email": "juan.perez@email.com",
    "telefono": "+57 300 123 4567",
    "direccion": "Calle 50 #25-30, Apto 501",
    "ciudad": "Bogotá",
    "pais": "Colombia",
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
  "message": "Cliente no encontrado"
}
```

---

### 4. Obtener Cliente por Documento

**Endpoint:** `GET /api/clientes/documento/{documento}`

**Ejemplo:** `GET /api/clientes/documento/1234567890`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta:** Igual que "Obtener por ID"

**Uso:** Útil para verificar si un cliente ya existe antes de crear una factura o venta.

---

### 5. Actualizar Cliente

**Endpoint:** `PUT /api/clientes/{id}`

**Ejemplo:** `PUT /api/clientes/1`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {TOKEN}
```

**Body (enviar TODOS los campos):**
```json
{
  "nombre": "Juan Carlos",
  "apellido": "Pérez García",
  "documento": "1234567890",
  "email": "juancarlos.perez@email.com",
  "telefono": "+57 300 999 8888",
  "direccion": "Carrera 15 #80-50, Casa 10",
  "ciudad": "Bogotá",
  "pais": "Colombia",
  "activo": true
}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "message": "Cliente actualizado exitosamente",
  "cliente": {
    "id": 1,
    "nombre": "Juan Carlos",
    "apellido": "Pérez García",
    "documento": "1234567890",
    "email": "juancarlos.perez@email.com",
    "telefono": "+57 300 999 8888",
    "direccion": "Carrera 15 #80-50, Casa 10",
    "ciudad": "Bogotá",
    "pais": "Colombia",
    "updatedAt": "2025-12-06T16:00:00.000+00:00",
    "activo": true
  },
  "schemaName": "empresa_3"
}
```

---

### 6. Desactivar Cliente

**Endpoint:** `DELETE /api/clientes/{id}`

**Ejemplo:** `DELETE /api/clientes/1`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "message": "Cliente desactivado exitosamente",
  "schemaName": "empresa_3"
}
```

**⚠️ IMPORTANTE:** Esto es una **desactivación lógica** (soft delete). El cliente NO se elimina de la BD, solo se marca como `activo = false`.

**Ventajas del soft delete:**
- Mantiene el historial de ventas/facturas
- Permite reactivar el cliente después
- No rompe relaciones con facturas existentes

---

## 🔍 BÚSQUEDAS Y FILTROS

### 7. Listar Clientes Activos

**Endpoint:** `GET /api/clientes/activos`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Descripción:** Retorna solo clientes con `activo = true`

**Respuesta (200 OK):**
```json
{
  "success": true,
  "clientes": [
    {
      "id": 1,
      "nombre": "Juan",
      "apellido": "Pérez García",
      "documento": "1234567890",
      "email": "juan.perez@email.com",
      "activo": true
    },
    {
      "id": 2,
      "nombre": "María",
      "apellido": "López Sánchez",
      "documento": "0987654321",
      "email": "maria.lopez@email.com",
      "activo": true
    }
  ],
  "total": 2,
  "schemaName": "empresa_3"
}
```

---

### 8. Buscar por Nombre

**Endpoint:** `GET /api/clientes/buscar?busqueda=juan`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Query Params:**
- `busqueda` - Texto a buscar (búsqueda parcial en nombre Y apellido, case-insensitive)

**Ejemplo:** `GET /api/clientes/buscar?busqueda=perez`

**Respuesta (200 OK):**
```json
{
  "success": true,
  "clientes": [
    {
      "id": 1,
      "nombre": "Juan",
      "apellido": "Pérez García",
      "documento": "1234567890",
      "email": "juan.perez@email.com",
      "activo": true
    },
    {
      "id": 5,
      "nombre": "Ana",
      "apellido": "Pérez Rodríguez",
      "documento": "1122334455",
      "email": "ana.perez@email.com",
      "activo": true
    }
  ],
  "total": 2,
  "busqueda": "perez",
  "schemaName": "empresa_3"
}
```

**Nota:** 
- La búsqueda es **case-insensitive** (no distingue mayúsculas/minúsculas)
- Busca en **nombre** Y **apellido**
- Retorna tanto clientes activos como inactivos

---

### 9. Buscar por Ciudad

**Endpoint:** `GET /api/clientes/ciudad/{ciudad}`

**Ejemplo:** `GET /api/clientes/ciudad/Bogotá`

**Headers:**
```
Authorization: Bearer {TOKEN}
```

**Respuesta (200 OK):**
```json
{
  "success": true,
  "clientes": [
    {
      "id": 1,
      "nombre": "Juan",
      "apellido": "Pérez García",
      "ciudad": "Bogotá",
      "activo": true
    },
    {
      "id": 3,
      "nombre": "Carlos",
      "apellido": "Ramírez",
      "ciudad": "Bogotá",
      "activo": true
    }
  ],
  "total": 2,
  "ciudad": "Bogotá",
  "schemaName": "empresa_3"
}
```

**Uso:** Útil para reportes de ventas por ciudad o campañas de marketing localizadas.

---

## 🧪 EJEMPLO COMPLETO DE FLUJO

### Paso 1: Login de Empleado

```bash
POST http://localhost:8080/api/auth/login

Body:
{
  "usuario": "carlos.admin",
  "password": "Admin@2024Tech",
  "tenantKey": "techstore-colombia-sas"
}

Respuesta:
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "schemaName": "empresa_3"
}
```

### Paso 2: Crear Cliente

```bash
POST http://localhost:8080/api/clientes
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Body:
{
  "nombre": "Pedro",
  "apellido": "Gómez Torres",
  "documento": "9876543210",
  "email": "pedro.gomez@email.com",
  "telefono": "+57 315 555 1234",
  "direccion": "Av. El Dorado #50-30",
  "ciudad": "Bogotá",
  "pais": "Colombia"
}
```

### Paso 3: Listar Clientes

```bash
GET http://localhost:8080/api/clientes
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Paso 4: Buscar Cliente por Documento (antes de crear factura)

```bash
GET http://localhost:8080/api/clientes/documento/9876543210
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```

### Paso 5: Actualizar Información del Cliente

```bash
PUT http://localhost:8080/api/clientes/1
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...

Body:
{
  "nombre": "Pedro José",
  "apellido": "Gómez Torres",
  "documento": "9876543210",
  "email": "pedrojose.gomez@email.com",
  "telefono": "+57 315 666 7777",
  "direccion": "Calle 127 #15-40",
  "ciudad": "Bogotá",
  "pais": "Colombia",
  "activo": true
}
```

---

## ⚠️ CONSIDERACIONES IMPORTANTES

### Multi-Tenancy
- ✅ Cada empresa tiene sus **propios clientes** aislados en su schema
- ✅ No es posible ver clientes de otras empresas
- ✅ El schema se configura automáticamente desde el JWT

### Validaciones
- ✅ El `documento` debe ser **único** dentro del schema del tenant
- ✅ El `email` debe ser un **email válido**
- ✅ Los campos `nombre`, `apellido`, `documento` y `email` son **obligatorios**

### Lógica de Negocio
- 📅 **createdAt** y **updatedAt** se gestionan automáticamente
- 🔄 **Desactivar** es preferible a **eliminar** para mantener historial
- 🔍 Las búsquedas son **case-insensitive** para mejor UX

### Seguridad
- 🔒 Todos los endpoints requieren **JWT válido**
- 🔒 El JWT debe pertenecer a una **empresa o empleado activo**
- 🔒 Solo se pueden gestionar clientes del **propio tenant**

---

## 🔗 INTEGRACIÓN CON OTROS MÓDULOS

### Clientes → Facturas
Cuando crees una factura, necesitarás el `clienteId`:

```bash
# 1. Buscar cliente por documento
GET /api/clientes/documento/1234567890

# 2. Usar el ID en la factura
POST /api/facturas
{
  "clienteId": 1,
  "productos": [...]
}
```

### Clientes → Reportes
Los clientes son la base para reportes de ventas:

```bash
# Clientes por ciudad (segmentación)
GET /api/clientes/ciudad/Bogotá

# Búsqueda rápida para autocompletar
GET /api/clientes/buscar?busqueda=juan
```

---

## 🐛 Troubleshooting

### Error: "Error: No se pudo determinar el tenant"
**Causa:** El JWT no contiene `schemaName` o es `public`  
**Solución:** Verifica que estés usando un JWT de empresa o empleado válido

### Error: "Cliente no encontrado"
**Causa:** El ID o documento no existe en el schema del tenant  
**Solución:** Verifica que el ID/documento sea correcto y pertenezca a tu empresa

### Error: "Documento duplicado"
**Causa:** Ya existe un cliente con ese documento en tu schema  
**Solución:** Usa el endpoint `/documento/{documento}` para verificar antes de crear

### Error: "Email inválido"
**Causa:** El formato del email no es válido  
**Solución:** Verifica que el email tenga formato `usuario@dominio.com`

---

## 📚 Próximos Pasos

1. ✅ CRUD de Clientes funcionando
2. ✅ CRUD de Productos funcionando
3. ⏳ Implementar gestión de Facturas (usando clientes y productos)
4. ⏳ Implementar sistema de Carrito de Compras
5. ⏳ Implementar reportes de ventas por cliente

**🎉 ¡La API de Clientes está lista para usar!**