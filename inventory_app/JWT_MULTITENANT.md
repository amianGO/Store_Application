                                                                                    # 🔐 Sistema JWT Multi-Tenant - Documentación Completa

## Resumen

Se ha implementado un sistema completo de autenticación JWT integrado con Multi-Tenancy que permite:
- ✅ Autenticación segura de empresas
- ✅ Generación de tokens JWT con información del tenant
- ✅ Configuración automática del TenantContext desde el JWT
- ✅ Gestión de sesiones activas por tenant
- ✅ Validación de tokens en cada request

---

## 🔧 Componentes Implementados

### 1. JwtService (Actualizado)
**Ubicación:** `/Config/JwtService.java`

**Funcionalidades:**
- Generación de tokens JWT con claims multi-tenant
- Validación de tokens
- Extracción de claims (empresaId, tenantKey, schemaName)
- Soporte para sesiones de 24 horas

**Claims en el Token:**
```json
{
  "subject": "contacto@miempresa.com",
  "empresaId": 1,
  "tenantKey": "abcd1234efgh5678",
  "schemaName": "empresa_1",
  "rol": "EMPRESA",
  "tipo": "empresa_login",
  "iat": 1700776800,
  "exp": 1700863200
}
```

**Métodos Principales:**
```java
// Generar token para empresa
String token = jwtService.generateTokenForEmpresa(
    empresaId, email, tenantKey, schemaName
);

// Extraer información
Long empresaId = jwtService.extractEmpresaId(token);
String schemaName = jwtService.extractSchemaName(token);
String tenantKey = jwtService.extractTenantKey(token);
String email = jwtService.extractUsername(token);

// Validar token
boolean isValid = jwtService.validateToken(token);
```

---

### 2. TenantInterceptor (Nuevo)
**Ubicación:** `/Config/TenantInterceptor.java`

**Responsabilidades:**
- Intercepta TODOS los requests a `/api/**`
- Extrae y valida el token JWT del header `Authorization`
- Configura automáticamente el `TenantContext` con el schema correcto
- Limpia el `TenantContext` después del request

**Flujo de Ejecución:**
```
Request → TenantInterceptor.preHandle()
  ↓
¿Es endpoint público? (registro/login)
  ├─ SÍ → TenantContext.resetToDefault() → Continúa
  └─ NO → Valida JWT
      ↓
  Extrae schemaName del JWT
      ↓
  TenantContext.setCurrentTenant(schemaName)
      ↓
  Agrega empresaId, email al request
      ↓
Controller ejecuta en el schema correcto
      ↓
TenantInterceptor.afterCompletion()
      ↓
  TenantContext.clear()
```

**Endpoints Públicos (No requieren JWT):**
- `/api/auth/registro`
- `/api/auth/login`
- `/api/empresas/{id}/verificar`
- `/api/admin/**`
- `/actuator/**`

**Respuestas de Error:**
```json
// Sin token
{
  "error": "UNAUTHORIZED",
  "mensaje": "Token JWT requerido"
}

// Token inválido
{
  "error": "INVALID_TOKEN",
  "mensaje": "Token inválido o expirado"
}
```

---

### 3. WebMvcConfig (Nuevo)
**Ubicación:** `/Config/WebMvcConfig.java`

**Función:**
- Registra el `TenantInterceptor` en Spring
- Configura para aplicarse a todos los endpoints `/api/**`

```java
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**")
                .order(1);
    }
}
```

---

### 4. EmpresaService (Actualizado)
**Cambio Principal:** Genera JWT real en lugar de "TODO_JWT_TOKEN"

**Antes:**
```java
response.setToken("TODO_JWT_TOKEN");
```

**Ahora:**
```java
String token = jwtService.generateTokenForEmpresa(
    empresa.getId(),
    empresa.getEmail(),
    empresa.getTenantKey(),
    empresa.getSchemaName()
);
response.setToken(token);
```

---

### 5. Controllers Actualizados

#### EmpresaController
**Cambios:**
- `GET /api/empresas/perfil` - Ya NO recibe `empresaId` como parámetro
- `PUT /api/empresas/perfil` - Ya NO recibe `empresaId` como parámetro
- Ambos extraen `empresaId` del request attribute (configurado por TenantInterceptor)

**Antes:**
```java
@GetMapping("/empresas/perfil")
public ResponseEntity<?> obtenerPerfil(@RequestParam Long empresaId) {
    // ...
}
```

**Ahora:**
```java
@GetMapping("/empresas/perfil")
public ResponseEntity<?> obtenerPerfil(HttpServletRequest request) {
    Long empresaId = (Long) request.getAttribute("empresaId");
    // ...
}
```

---

## 🔄 Flujo Completo de Autenticación

### 1. Registro de Empresa
```
POST /api/auth/registro
  ↓
EmpresaService.registrarEmpresa()
  ↓
- Crear empresa en schema public
- Generar schemaName y tenantKey
- Crear suscripción de prueba
- Crear schema dedicado (empresa_1)
  ↓
Response: EmpresaResponseDTO (sin token)
```

### 2. Login
```
POST /api/auth/login
  ↓
EmpresaService.autenticarEmpresa()
  ↓
- Validar credenciales
- Verificar empresa activa
- Generar JWT con claims multi-tenant
  ↓
Response: LoginResponseDTO con token JWT
```

### 3. Requests Autenticados
```
GET /api/empresas/perfil
Headers: Authorization: Bearer {token}
  ↓
TenantInterceptor.preHandle()
  ↓
- Extrae token del header
- Valida token
- Extrae schemaName
- TenantContext.setCurrentTenant("empresa_1")
- request.setAttribute("empresaId", 1)
  ↓
EmpresaController.obtenerPerfil(request)
  ↓
- Long empresaId = request.getAttribute("empresaId")
- EmpresaService.obtenerEmpresaPorId(empresaId)
  ↓
Response: EmpresaResponseDTO
  ↓
TenantInterceptor.afterCompletion()
  ↓
- TenantContext.clear()
```

---

## 📋 Testing con Postman

### Paso 1: Registrar Empresa
```http
POST http://localhost:8080/api/auth/registro
Content-Type: application/json

{
  "nombre": "Test Empresa SAS",
  "nit": "900111222-3",
  "email": "test@empresa.com",
  "password": "Test123!",
  "confirmarPassword": "Test123!",
  "telefono": "3001234567",
  "direccion": "Calle 123",
  "ciudad": "Bogotá",
  "pais": "Colombia"
}
```

**Response 201:**
```json
{
  "id": 1,
  "nombre": "Test Empresa SAS",
  "email": "test@empresa.com",
  "schemaName": "empresa_1",
  "tenantKey": "abcd1234..."
}
```

### Paso 2: Login y Obtener Token
```http
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "email": "test@empresa.com",
  "password": "Test123!"
}
```

**Response 200:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJlbXByZXNhSWQiOjEsInRlbmFudEtleSI6ImFiY2QxMjM0...",
  "tipo": "Bearer",
  "empresa": {
    "id": 1,
    "nombre": "Test Empresa SAS",
    "email": "test@empresa.com",
    "schemaName": "empresa_1"
  }
}
```

**⚠️ IMPORTANTE:** Guardar el `token` en una variable de Postman

### Paso 3: Usar Token en Requests
```http
GET http://localhost:8080/api/empresas/perfil
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJlbXByZXNhSWQiOjEsInRlbmFudEtleSI6ImFiY2QxMjM0...
```

**Response 200:**
```json
{
  "id": 1,
  "nombre": "Test Empresa SAS",
  "email": "test@empresa.com",
  "schemaName": "empresa_1",
  "activa": true,
  "verificada": false
}
```

---

## 🔐 Seguridad Implementada

### ✅ Características de Seguridad
1. **Tokens firmados con HS256** - Imposible falsificar
2. **Secret Key de 256 bits** - Almacenada en JwtService
3. **Expiración de 24 horas** - Tokens tienen tiempo de vida limitado
4. **Validación en cada request** - TenantInterceptor valida automáticamente
5. **Aislamiento de datos** - Cada empresa solo accede a su schema
6. **Claims inmutables** - No se pueden modificar empresaId, schemaName en el token
7. **Limpieza de TenantContext** - Evita contaminación entre requests

### ⚠️ Consideraciones de Seguridad

**SECRET_KEY en Producción:**
```java
// CAMBIAR en producción - Usar variables de entorno
private static final String SECRET_KEY = System.getenv("JWT_SECRET_KEY");
```

**HTTPS Obligatorio:**
- Tokens deben transmitirse SOLO sobre HTTPS en producción
- Configurar SSL/TLS en el servidor

**Refresh Tokens (Futuro):**
- Implementar refresh tokens para renovar sesiones
- Tokens de corta duración + refresh tokens de larga duración

---

## 📊 Gestión de Sesiones Activas

### Tabla de Sesiones (Futuro)
```sql
CREATE TABLE sesiones_activas (
    id BIGSERIAL PRIMARY KEY,
    empresa_id BIGINT REFERENCES empresas(id),
    token_hash VARCHAR(255) UNIQUE,
    ip_address VARCHAR(45),
    user_agent TEXT,
    fecha_login TIMESTAMP,
    ultimo_acceso TIMESTAMP,
    activa BOOLEAN DEFAULT TRUE
);
```

### Control de Terminales Concurrentes
El sistema ya tiene control de terminales mediante `SuscripcionService`:
- `registrarTerminalActiva(empresaId)` - Al hacer login
- `liberarTerminalActiva(empresaId)` - Al hacer logout
- Valida límites según el plan de suscripción

---

## 🎯 Próximos Pasos

### 1. Implementar Logout
```java
POST /api/auth/logout
- Invalidar token (blacklist)
- Liberar terminal activa
- Limpiar sesión
```

### 2. Refresh Tokens
```java
POST /api/auth/refresh
Body: { "refreshToken": "..." }
Response: { "token": "...", "refreshToken": "..." }
```

### 3. Gestión de Sesiones
```java
GET /api/empresas/sesiones
- Listar sesiones activas
- Revocar sesiones específicas
```

### 4. Audit Log
```java
- Registrar todos los logins
- Registrar accesos fallidos
- Alertas de seguridad
```

---

## ✅ Estado Actual

**Completado (100%):**
- ✅ JwtService con soporte multi-tenant
- ✅ TenantInterceptor automático
- ✅ WebMvcConfig para registro de interceptors
- ✅ EmpresaService genera JWT real
- ✅ Controllers extraen empresaId del JWT
- ✅ Compilación exitosa (BUILD SUCCESS)
- ✅ Documentación completa

**Listo para:**
- Testing con Postman
- Desarrollo del frontend
- Implementación de logout y refresh tokens

---

**Fecha:** 2025-11-23  
**Versión:** 2.0 - Multi-Tenant JWT  
**Autor:** DamianG
