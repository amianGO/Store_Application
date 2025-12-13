# 🧪 Guía de Prueba - Sistema Multi-Tenant de Empleados

## 📋 Resumen del Flujo Multi-Tenant

```
┌─────────────────────────────────────────────────────────────┐
│                    ARQUITECTURA MULTI-TENANT                │
├─────────────────────────────────────────────────────────────┤
│                                                             │
│  Schema PUBLIC:                                             │
│  ├── empresas (tabla)                                       │
│  ├── suscripciones (tabla)                                  │
│  └── template_schema (schema con estructura base)          │
│                                                             │
│  Schema EMPRESA_1:                                          │
│  ├── empleados (tabla - aislada)                           │
│  ├── productos (tabla - aislada)                           │
│  ├── clientes (tabla - aislada)                            │
│  └── facturas (tabla - aislada)                            │
│                                                             │
│  Schema EMPRESA_2:                                          │
│  ├── empleados (tabla - aislada)                           │
│  ├── productos (tabla - aislada)                           │
│  ├── clientes (tabla - aislada)                            │
│  └── facturas (tabla - aislada)                            │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

---

## 🚀 PARTE 1: Configuración Inicial

### 1.1 Iniciar PostgreSQL (Docker)
```bash
# Si aún no está corriendo:
docker start postgres-tienda
```

### 1.2 ⚠️ IMPORTANTE: Crear template_schema (UNA SOLA VEZ)

**Este paso es OBLIGATORIO antes de registrar empresas.**

El `template_schema` contiene la estructura base de tablas que se clonará automáticamente para cada empresa.

```bash
cd /Users/gaviria/Documents/dev/Proyectos_Spring/Tienda/Store_Application/inventory_app

# Ejecutar el script de creación del template
psql -h localhost -p 5433 -U docker_admin -d app_main \
  -f src/main/resources/db/template_schema.sql
```

**Verificar que se creó correctamente:**
```bash
psql -h localhost -p 5433 -U docker_admin -d app_main \
  -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'template_schema' ORDER BY table_name;"
```

**Resultado esperado: 7 tablas**
```
    table_name    
------------------
 cajas
 carrito_compras
 clientes
 detalle_facturas
 empleados
 facturas
 productos
```

> 📝 **Nota:** Si ya registraste empresas ANTES de crear el template, consulta `CONFIGURACION_BD_MULTITENANT.md` para clonar las tablas manualmente.

### 1.3 Iniciar la Aplicación Spring Boot
```bash
cd /Users/gaviria/Documents/dev/Proyectos_Spring/Tienda/Store_Application/inventory_app
./mvnw spring-boot:run
```

Verifica que inicie en el puerto **8080** sin errores.

---

## 🏢 PARTE 2: Registro y Login de Empresa

### 2.1 Registrar Primera Empresa

**Endpoint:** `POST http://localhost:8080/api/auth/empresa/registro`

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "nombre": "TechStore Solutions",
  "nit": "900123456-7",
  "email": "admin@techstore.com",
  "password": "Tech@2024Store",
  "confirmPassword": "Tech@2024Store",
  "telefono": "+57 300 123 4567",
  "direccion": "Calle 100 #15-20, Bogotá",
  "ciudad": "Bogotá",
  "pais": "Colombia",
  "industria": "Tecnología",
  "numeroEmpleados": 25
}
```

**Respuesta Esperada (201 CREATED):**
```json
{
  "success": true,
  "message": "Empresa registrada exitosamente",
  "empresa": {
    "id": 1,
    "nombre": "TechStore Solutions",
    "email": "admin@techstore.com",
    "tenantKey": "techstore-abc123",
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

**✅ Puntos Clave:**
- Se creó la empresa en `public.empresas`
- Se generó un `tenantKey` único (ej: `techstore-abc123`)
- Se creó el schema `empresa_1` con todas las tablas
- `verificada = false` (necesita verificación)

---

### 2.2 Verificar la Empresa (Manual)

**Opción A: SQL Directo**
```sql
-- Conectar a PostgreSQL
psql -h localhost -p 5433 -U postgres -d tienda_db

-- Actualizar verificación
UPDATE public.empresas SET verificada = true WHERE id = 1;

-- Verificar
SELECT id, nombre, email, tenant_key, schema_name, verificada FROM public.empresas;
```

**Opción B: Endpoint de Verificación**
```
POST http://localhost:8080/api/auth/empresa/1/verificar
```

---

### 2.3 Login de Empresa

**Endpoint:** `POST http://localhost:8080/api/auth/empresa/login`

**Body:**
```json
{
  "email": "admin@techstore.com",
  "password": "Tech@2024Store"
}
```

**Respuesta Esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJlbXByZXNhSWQiOjEsInRlbmFudEtleSI6InRlY2hzdG9yZS1hYmMxMjMiLCJzY2hlbWFOYW1lIjoiZW1wcmVzYV8xIiwicm9sIjoiRU1QUkVTQSIsInRpcG8iOiJlbXByZXNhX2xvZ2luIiwiaWF0IjoxNzAxMDAwMDAwLCJleHAiOjE3MDEwODY0MDB9.signature",
  "email": "admin@techstore.com",
  "empresaId": 1,
  "tenantKey": "techstore-abc123",
  "schemaName": "empresa_1"
}
```

**🔑 Guarda el TOKEN - lo necesitarás para crear empleados**

---

## 👥 PARTE 3: Gestión de Empleados

### 3.1 Crear Primer Empleado (Admin)

**Endpoint:** `POST http://localhost:8080/api/empresas/empleados`

**Headers:**
```
Content-Type: application/json
Authorization: Bearer {TOKEN_DE_EMPRESA}
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
  "email": "carlos@techstore.com",
  "cargo": "Administrador General",
  "rol": "ADMIN"
}
```

**Respuesta Esperada (201 CREATED):**
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
    "email": "carlos@techstore.com",
    "cargo": "Administrador General",
    "rol": "ADMIN",
    "activo": true
  },
  "tenantInfo": {
    "schemaName": "empresa_1",
    "empresaId": 1,
    "tenantKey": "techstore-abc123"
  }
}
```

**✅ Verifica en la BD:**
```sql
-- Cambiar al schema de la empresa
SET search_path TO empresa_1;

-- Ver el empleado creado
SELECT id, nombre, apellido, usuario, rol, estado_activo FROM empleados;
```

---

### 3.2 Crear Segundo Empleado (Vendedor)

**Endpoint:** `POST http://localhost:8080/api/empresas/empleados`

**Headers:**
```
Authorization: Bearer {TOKEN_DE_EMPRESA}
```

**Body:**
```json
{
  "nombre": "María",
  "apellido": "López",
  "documento": "0987654321",
  "usuario": "maria.vendedor",
  "password": "Venta@2024Tech",
  "confirmPassword": "Venta@2024Tech",
  "telefono": "+57 320 555 9876",
  "email": "maria@techstore.com",
  "cargo": "Vendedora",
  "rol": "VENDEDOR"
}
```

---

### 3.3 Login de Empleado

**Endpoint:** `POST http://localhost:8080/api/auth/login`

**Body:**
```json
{
  "usuario": "carlos.admin",
  "password": "Admin@2024Tech",
  "tenantKey": "techstore-abc123"
}
```

**Respuesta Esperada (200 OK):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJlbXByZXNhSWQiOjEsInNjaGVtYU5hbWUiOiJlbXByZXNhXzEiLCJ0ZW5hbnRLZXkiOiJ0ZWNoc3RvcmUtYWJjMTIzIiwiZW1wbGVhZG9JZCI6MSwicm9sIjoiQURNSU4iLCJ0aXBvIjoiZW1wbGVhZG9fbG9naW4iLCJpYXQiOjE3MDEwMDAwMDAsImV4cCI6MTcwMTA4NjQwMH0.signature",
  "usuario": "carlos.admin",
  "empleadoId": 1,
  "rol": "ADMIN",
  "schemaName": "empresa_1",
  "empresaId": 1
}
```

**🔑 Este TOKEN identifica al empleado Y al tenant**

---

## 🧪 PARTE 4: Prueba de Aislamiento Multi-Tenant

### 4.1 Registrar Segunda Empresa

**Endpoint:** `POST http://localhost:8080/api/auth/empresa/registro`

**Body:**
```json
{
  "nombre": "FashionHub Store",
  "nit": "900987654-3",
  "email": "admin@fashionhub.com",
  "password": "Fashion@2024Hub",
  "confirmPassword": "Fashion@2024Hub",
  "telefono": "+57 300 987 6543",
  "direccion": "Carrera 7 #80-45, Medellín",
  "ciudad": "Medellín",
  "pais": "Colombia",
  "industria": "Moda",
  "numeroEmpleados": 15
}
```

**Respuesta:**
```json
{
  "empresa": {
    "id": 2,
    "tenantKey": "fashionhub-xyz789",
    "schemaName": "empresa_2"
  }
}
```

---

### 4.2 Verificar y Hacer Login

```sql
-- Verificar empresa
UPDATE public.empresas SET verificada = true WHERE id = 2;
```

**Login:**
```json
POST http://localhost:8080/api/auth/empresa/login
{
  "email": "admin@fashionhub.com",
  "password": "Fashion@2024Hub"
}
```

---

### 4.3 Crear Empleado en Segunda Empresa

**Endpoint:** `POST http://localhost:8080/api/empresas/empleados`

**Headers:**
```
Authorization: Bearer {TOKEN_DE_FASHIONHUB}
```

**Body:**
```json
{
  "nombre": "Ana",
  "apellido": "García",
  "documento": "5555666677",
  "usuario": "ana.admin",
  "password": "Fashion@2024Ana",
  "confirmPassword": "Fashion@2024Ana",
  "telefono": "+57 315 444 5555",
  "email": "ana@fashionhub.com",
  "cargo": "Gerente de Tienda",
  "rol": "GERENTE"
}
```

**✅ Verificar Aislamiento:**
```sql
-- Ver empleados de TechStore (empresa_1)
SET search_path TO empresa_1;
SELECT usuario, nombre, apellido FROM empleados;
-- Resultado: carlos.admin, maria.vendedor

-- Ver empleados de FashionHub (empresa_2)
SET search_path TO empresa_2;
SELECT usuario, nombre, apellido FROM empleados;
-- Resultado: ana.admin
```

**🎯 Los empleados están COMPLETAMENTE AISLADOS por schema**

---

### 4.4 Intentar Login de Empleado con TenantKey Incorrecto

**Prueba de Seguridad:**

```json
POST http://localhost:8080/api/auth/login
{
  "usuario": "carlos.admin",
  "password": "Admin@2024Tech",
  "tenantKey": "fashionhub-xyz789"
}
```

**Respuesta Esperada (401 UNAUTHORIZED):**
```json
{
  "success": false,
  "message": "Credenciales inválidas"
}
```

**✅ El sistema valida que el empleado exista en el schema correcto**

---

## 📊 PARTE 5: Verificación en Base de Datos

### 5.1 Verificar Estructura Multi-Tenant

```sql
-- Conectar a PostgreSQL
psql -h localhost -p 5433 -U postgres -d tienda_db

-- Ver todos los schemas
SELECT schema_name FROM information_schema.schemata 
WHERE schema_name LIKE 'empresa_%' OR schema_name = 'template_schema';

-- Ver empresas registradas
SELECT id, nombre, email, tenant_key, schema_name, verificada, activa 
FROM public.empresas;

-- Ver empleados por empresa
SET search_path TO empresa_1;
SELECT id, usuario, nombre, apellido, rol, estado_activo FROM empleados;

SET search_path TO empresa_2;
SELECT id, usuario, nombre, apellido, rol, estado_activo FROM empleados;
```

---

## 🔍 PARTE 6: Verificar Logs del Sistema

Busca en los logs estos mensajes clave:

```
=== REGISTRO DE EMPLEADO ===
Schema actual: empresa_1
Empresa ID: 1
Tenant Key: techstore-abc123
Usuario a crear: carlos.admin
Rol: ADMIN
✓ Empleado creado exitosamente en schema: empresa_1

=== LOGIN EMPLEADO ===
TenantKey recibido: techstore-abc123
Empresa encontrada: TechStore Solutions (ID: 1, Schema: empresa_1)
✓ Empresa ACTIVA y VERIFICADA
✓ Empleado autenticado: carlos.admin (Rol: ADMIN)
Generando token para empleado: carlos.admin
```

---

## ✅ Checklist de Verificación

- [ ] **Empresa 1 creada** con schema `empresa_1`
- [ ] **Empresa 1 verificada** (verificada = true)
- [ ] **Login de Empresa 1** exitoso (recibí JWT)
- [ ] **Empleado creado en Empresa 1** usando JWT de empresa
- [ ] **Login de Empleado 1** exitoso con `tenantKey`
- [ ] **Empresa 2 creada** con schema `empresa_2`
- [ ] **Empleado creado en Empresa 2**
- [ ] **Aislamiento verificado** (empleados no se cruzan entre schemas)
- [ ] **Login con tenantKey incorrecto** rechazado
- [ ] **Logs muestran** cambios de schema correctos

---

## 🎯 Resumen del Sistema Multi-Tenant

### JWT para Empresas
```json
{
  "empresaId": 1,
  "tenantKey": "techstore-abc123",
  "schemaName": "empresa_1",
  "rol": "EMPRESA",
  "tipo": "empresa_login"
}
```

### JWT para Empleados
```json
{
  "empresaId": 1,
  "schemaName": "empresa_1",
  "tenantKey": "techstore-abc123",
  "empleadoId": 1,
  "rol": "ADMIN",
  "tipo": "empleado_login"
}
```

---

## 🐛 Troubleshooting

### Error: "Credenciales inválidas"
- Verifica que la empresa esté **verificada** y **activa**
- Confirma que el `tenantKey` sea correcto
- Verifica que el usuario exista en el schema correcto

### Error: "Error al determinar el tenant"
- Verifica que el JWT de empresa sea válido
- Confirma que el schema exista en PostgreSQL

### Error: "Las contraseñas no coinciden"
- Verifica que `password` y `confirmPassword` sean idénticos

### Empleado no se crea
- Verifica que uses el JWT de **EMPRESA**, no de empleado
- Confirma que el schema del tenant exista
- Revisa los logs para ver el schema actual

---

## 📚 Próximos Pasos

1. ✅ Sistema multi-tenant funcionando
2. ⏳ Crear endpoints protegidos para productos, clientes, facturas
3. ⏳ Implementar roles y permisos (ADMIN vs VENDEDOR)
4. ⏳ Crear frontend React para gestión
5. ⏳ Implementar sistema de email verification

**🎉 ¡El sistema SaaS multi-tenant está listo para probar!**
