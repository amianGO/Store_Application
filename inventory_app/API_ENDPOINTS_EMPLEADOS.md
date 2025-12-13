# 🔐 API Endpoints - Sistema Multi-Tenant

## 📌 Información General

- **Base URL:** `http://localhost:8080`
- **Autenticación:** Bearer Token (JWT)
- **Content-Type:** `application/json`

---

## 🏢 EMPRESAS (Schema: PUBLIC)

### 1. Registrar Empresa
```http
POST /api/auth/empresa/registro
```

**Headers:** Ninguno requerido

**Body:**
```json
{
  "nombre": "string (max 100)",
  "nit": "string (max 20, requerido)",
  "email": "string (válido)",
  "password": "string (min 8, patrón especial @#$%^&+=)",
  "confirmPassword": "string",
  "telefono": "string (max 15)",
  "direccion": "string (max 255)",
  "ciudad": "string (max 50)",
  "pais": "string (max 50)",
  "industria": "string (max 100, opcional)",
  "numeroEmpleados": number (opcional)
}
```

**Response 201:**
```json
{
  "success": true,
  "message": "Empresa registrada exitosamente",
  "empresa": {
    "id": 1,
    "nombre": "Mi Empresa",
    "email": "admin@miempresa.com",
    "tenantKey": "miempresa-abc123",
    "schemaName": "empresa_1",
    "verificada": false,
    "activa": true
  },
  "suscripcion": {
    "planNombre": "BASICO",
    "estado": "ACTIVA"
  }
}
```

---

### 2. Login de Empresa
```http
POST /api/auth/empresa/login
```

**Body:**
```json
{
  "email": "admin@miempresa.com",
  "password": "MiPassword@2024"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "email": "admin@miempresa.com",
  "empresaId": 1,
  "tenantKey": "miempresa-abc123",
  "schemaName": "empresa_1"
}
```

**JWT Claims:**
```json
{
  "empresaId": 1,
  "tenantKey": "miempresa-abc123",
  "schemaName": "empresa_1",
  "rol": "EMPRESA",
  "tipo": "empresa_login"
}
```

---

### 3. Ver Perfil de Empresa
```http
GET /api/auth/empresa/perfil
```

**Headers:**
```
Authorization: Bearer {TOKEN_EMPRESA}
```

**Response 200:**
```json
{
  "id": 1,
  "nombre": "Mi Empresa",
  "email": "admin@miempresa.com",
  "telefono": "+57 300 123 4567",
  "direccion": "Calle 100 #15-20",
  "ciudad": "Bogotá",
  "pais": "Colombia",
  "tenantKey": "miempresa-abc123",
  "schemaName": "empresa_1",
  "verificada": true,
  "activa": true
}
```

---

### 4. Verificar Empresa (Admin)
```http
POST /api/auth/empresa/{id}/verificar
```

**Response 200:**
```json
{
  "message": "Empresa verificada exitosamente",
  "empresa": { ... }
}
```

---

## 👥 EMPLEADOS (Schema: TENANT)

### 5. Registrar Empleado
```http
POST /api/empresas/empleados
```

**Headers:**
```
Authorization: Bearer {TOKEN_EMPRESA}
Content-Type: application/json
```

**Body:**
```json
{
  "nombre": "Carlos",
  "apellido": "Rodríguez",
  "documento": "1234567890",
  "usuario": "carlos.admin",
  "password": "Admin@2024Tech",
  "confirmPassword": "Admin@2024Tech",
  "telefono": "+57 310 555 1234",
  "email": "carlos@miempresa.com",
  "cargo": "Administrador",
  "rol": "ADMIN"
}
```

**Roles Disponibles:**
- `ADMIN` - Administrador total
- `GERENTE` - Gerente con permisos limitados
- `VENDEDOR` - Solo ventas

**Response 201:**
```json
{
  "success": true,
  "message": "Empleado registrado exitosamente",
  "empleado": {
    "id": 1,
    "nombre": "Carlos",
    "apellido": "Rodríguez",
    "documento": "1234567890",
    "usuario": "carlos.admin",
    "email": "carlos@miempresa.com",
    "cargo": "Administrador",
    "rol": "ADMIN",
    "activo": true
  },
  "tenantInfo": {
    "schemaName": "empresa_1",
    "empresaId": 1,
    "tenantKey": "miempresa-abc123"
  }
}
```

---

### 6. Login de Empleado
```http
POST /api/auth/login
```

**Headers:** Ninguno requerido

**Body:**
```json
{
  "usuario": "carlos.admin",
  "password": "Admin@2024Tech",
  "tenantKey": "miempresa-abc123"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "usuario": "carlos.admin",
  "empleadoId": 1,
  "rol": "ADMIN",
  "schemaName": "empresa_1",
  "empresaId": 1
}
```

**JWT Claims:**
```json
{
  "empresaId": 1,
  "schemaName": "empresa_1",
  "tenantKey": "miempresa-abc123",
  "empleadoId": 1,
  "rol": "ADMIN",
  "tipo": "empleado_login"
}
```

---

### 7. Registro de Empleado (DESHABILITADO)
```http
POST /api/auth/register
```

**Response 403:**
```json
{
  "success": false,
  "message": "El registro de empleados debe hacerse a través de /api/empresas/empleados con autenticación de empresa"
}
```

---

## 🔒 Seguridad y Validaciones

### Validación de Contraseña
Las contraseñas deben cumplir:
- Mínimo 6 caracteres
- Al menos una mayúscula
- Al menos una minúscula
- Al menos un número
- Al menos un carácter especial: `@#$%^&+=`

**Ejemplo válido:** `Admin@2024Tech`

**Ejemplo inválido:** `admin2024` (falta mayúscula y especial)

---

### Configuración de Seguridad

#### Endpoints Públicos (sin JWT)
- `POST /api/auth/empresa/registro`
- `POST /api/auth/empresa/login`
- `POST /api/auth/login` (login empleados)
- `POST /api/auth/empresa/{id}/verificar`

#### Endpoints Protegidos (requieren JWT)
- `GET /api/auth/empresa/perfil`
- `PUT /api/auth/empresa/perfil`
- `POST /api/empresas/empleados`

---

## 🌐 TenantInterceptor - Configuración Automática

El `TenantInterceptor` intercepta todas las peticiones a `/api/**` y:

### Para Endpoints Públicos:
1. Resetea el `TenantContext` a schema `public`
2. Permite la ejecución sin validar JWT

### Para Endpoints Protegidos:
1. Extrae el token del header `Authorization: Bearer {token}`
2. Valida el token
3. Extrae el claim `schemaName` del JWT
4. Configura `TenantContext.setCurrentTenant(schemaName)`
5. Todas las consultas JPA se ejecutan en ese schema

### Para Login de Empleados (caso especial):
1. Es público (no requiere JWT previo)
2. `AuthController` configura manualmente el `TenantContext`:
   - Lee `tenantKey` del body
   - Busca la empresa en `public.empresas`
   - Configura el schema: `TenantContext.setCurrentTenant(empresa.getSchemaName())`
   - Valida credenciales en el schema del tenant
   - Genera JWT con datos del empleado y tenant

---

## 📊 Estructura Multi-Tenant

```
┌──────────────────────────────────────────┐
│          SCHEMA: public                  │
├──────────────────────────────────────────┤
│  - empresas                              │
│  - suscripciones                         │
│  - planes                                │
└──────────────────────────────────────────┘
           │
           ├─────────────────────────────┐
           │                             │
┌──────────▼──────────┐    ┌─────────────▼────────┐
│ SCHEMA: empresa_1   │    │ SCHEMA: empresa_2    │
├─────────────────────┤    ├──────────────────────┤
│  - empleados        │    │  - empleados         │
│  - productos        │    │  - productos         │
│  - clientes         │    │  - clientes          │
│  - facturas         │    │  - facturas          │
│  - detalle_facturas │    │  - detalle_facturas  │
│  - carrito_compras  │    │  - carrito_compras   │
└─────────────────────┘    └──────────────────────┘
```

---

## 🔄 Flujos Completos

### Flujo 1: Onboarding de Nueva Empresa
```
1. POST /api/auth/empresa/registro
   → Crea empresa en public.empresas
   → Genera tenantKey único
   → Crea schema empresa_N
   → Clona estructura desde template_schema
   → Crea suscripción

2. Admin verifica empresa (manual o email)
   → UPDATE empresas SET verificada = true

3. POST /api/auth/empresa/login
   → Retorna JWT con empresaId, tenantKey, schemaName

4. POST /api/empresas/empleados (con JWT de empresa)
   → TenantInterceptor configura schema desde JWT
   → Crea empleado en schema del tenant
```

### Flujo 2: Login de Empleado
```
1. POST /api/auth/login
   Body: { usuario, password, tenantKey }

2. AuthController:
   → Resetea a schema public
   → Busca empresa por tenantKey
   → Valida empresa activa y verificada
   → Configura schema: TenantContext.setCurrentTenant(schemaName)
   → Busca empleado en schema del tenant
   → Valida credenciales
   → Genera JWT con datos empleado + tenant

3. JWT retornado incluye:
   → empleadoId, rol (del empleado)
   → empresaId, schemaName, tenantKey (del tenant)
```

### Flujo 3: Operación Protegida
```
1. Cliente envía petición con JWT
   Authorization: Bearer {token}

2. TenantInterceptor:
   → Valida JWT
   → Extrae schemaName del claim
   → Configura TenantContext.setCurrentTenant(schemaName)

3. Controller ejecuta lógica:
   → JPA consulta automáticamente en schema correcto

4. TenantInterceptor (afterCompletion):
   → Limpia TenantContext.clear()
```

---

## 🐛 Códigos de Error

| Código | Descripción |
|--------|-------------|
| 400 | Bad Request - Datos inválidos |
| 401 | Unauthorized - JWT inválido o credenciales incorrectas |
| 403 | Forbidden - Operación no permitida |
| 404 | Not Found - Recurso no encontrado |
| 409 | Conflict - Email/usuario/tenantKey ya existe |
| 500 | Internal Server Error |

---

## 📝 Notas Importantes

1. **tenantKey** es ÚNICO por empresa y se genera automáticamente
2. **schemaName** sigue el patrón `empresa_{id}` (empresa_1, empresa_2, etc.)
3. Los **empleados** solo pueden ser creados por empresas autenticadas
4. El **login de empleados** REQUIERE el `tenantKey` para identificar el schema correcto
5. Cada **empresa tiene su propia base de datos aislada** (schema)
6. Los **JWT de empresa** y **JWT de empleado** tienen claims diferentes
7. El sistema valida que contraseñas cumplan requisitos de seguridad

---

## 🔐 Ejemplo de Colección Postman

```json
{
  "info": {
    "name": "Store Multi-Tenant API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Empresas",
      "item": [
        {
          "name": "Registrar Empresa",
          "request": {
            "method": "POST",
            "url": "http://localhost:8080/api/auth/empresa/registro",
            "body": { ... }
          }
        },
        {
          "name": "Login Empresa",
          "request": {
            "method": "POST",
            "url": "http://localhost:8080/api/auth/empresa/login",
            "body": { ... }
          }
        }
      ]
    },
    {
      "name": "Empleados",
      "item": [
        {
          "name": "Crear Empleado",
          "request": {
            "method": "POST",
            "url": "http://localhost:8080/api/empresas/empleados",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{empresaToken}}"
              }
            ],
            "body": { ... }
          }
        },
        {
          "name": "Login Empleado",
          "request": {
            "method": "POST",
            "url": "http://localhost:8080/api/auth/login",
            "body": {
              "usuario": "carlos.admin",
              "password": "Admin@2024Tech",
              "tenantKey": "{{tenantKey}}"
            }
          }
        }
      ]
    }
  ]
}
```

---

**Última actualización:** 25 de noviembre de 2025
