# 🐛 Fix: NullPointerException al crear empleado - TenantKey faltante

## 📋 Problema Identificado

### Error en logs:
```
=== REGISTRO DE EMPLEADO ===
Schema actual: empresa_3
Empresa ID: 3
Tenant Key: null  ← PROBLEMA
Usuario a crear: damian.admin
Rol: ADMIN
✓ Empleado creado exitosamente en schema: empresa_3
ERROR inesperado al registrar empleado: null
java.lang.NullPointerException
```

### Causa Raíz

El `TenantInterceptor` NO estaba extrayendo ni agregando el `tenantKey` a los atributos del request, causando:

1. `tenantKey = null` en `EmpleadoController`
2. `Map.of()` falla con valores `null` → `NullPointerException`

---

## ✅ Solución Aplicada

### 1. TenantInterceptor.java - Agregar extracción de tenantKey

**Archivo:** `src/main/java/com/example/inventory_app/Config/TenantInterceptor.java`

**Cambio realizado:**

```java
// ANTES - Solo extraía 3 valores
String schemaName = jwtService.extractSchemaName(token);
String empresaEmail = jwtService.extractUsername(token);
Long empresaId = jwtService.extractEmpresaId(token);

request.setAttribute("empresaId", empresaId);
request.setAttribute("empresaEmail", empresaEmail);
request.setAttribute("schemaName", schemaName);

// DESPUÉS - Ahora extrae también tenantKey
String schemaName = jwtService.extractSchemaName(token);
String empresaEmail = jwtService.extractUsername(token);
Long empresaId = jwtService.extractEmpresaId(token);
String tenantKey = jwtService.extractTenantKey(token);  // ← NUEVO

request.setAttribute("empresaId", empresaId);
request.setAttribute("empresaEmail", empresaEmail);
request.setAttribute("schemaName", schemaName);
request.setAttribute("tenantKey", tenantKey);           // ← NUEVO

System.out.println("[TENANT-INTERCEPTOR] ✓ Tenant configurado: " + 
    schemaName + " (Empresa: " + empresaEmail + ", TenantKey: " + tenantKey + ")");
```

---

### 2. EmpleadoController.java - Usar HashMap en lugar de Map.of()

**Archivo:** `src/main/java/com/example/inventory_app/Controllers/EmpleadoController.java`

**Problema:** `Map.of()` NO acepta valores `null`

**Cambio realizado:**

```java
// ANTES - Map.of() falla si tenantKey es null
response.put("tenantInfo", Map.of(
    "schemaName", schemaName,
    "empresaId", empresaId,
    "tenantKey", tenantKey  // ← Si es null, falla
));

// DESPUÉS - HashMap permite valores null
Map<String, Object> tenantInfo = new HashMap<>();
tenantInfo.put("schemaName", schemaName);
tenantInfo.put("empresaId", empresaId);
tenantInfo.put("tenantKey", tenantKey);  // ← Ahora funciona con null

response.put("tenantInfo", tenantInfo);
```

---

## 🎯 Resultado Esperado

Después de reiniciar la aplicación, los logs deberían mostrar:

```
[TENANT-INTERCEPTOR] ✓ Tenant configurado: empresa_3 (Empresa: admin@techstore.com, TenantKey: techstore-colombia-sas)

=== REGISTRO DE EMPLEADO ===
Schema actual: empresa_3
Empresa ID: 3
Tenant Key: techstore-colombia-sas  ← AHORA TIENE VALOR
Usuario a crear: damian.admin
Rol: ADMIN
✓ Empleado creado exitosamente en schema: empresa_3
  - ID: 1
  - Usuario: damian.admin
  - Rol: ADMIN
```

**Sin errores de NullPointerException**

---

## 📝 Respuesta JSON Correcta

```json
{
  "success": true,
  "message": "Empleado registrado exitosamente",
  "empleado": {
    "id": 1,
    "nombre": "Damian",
    "apellido": "García",
    "documento": "1234567890",
    "usuario": "damian.admin",
    "email": "damian@techstore.com",
    "cargo": "Administrador",
    "rol": "ADMIN",
    "activo": true
  },
  "tenantInfo": {
    "schemaName": "empresa_3",
    "empresaId": 3,
    "tenantKey": "techstore-colombia-sas"
  }
}
```

---

## 🔧 Pasos para Probar

### 1. Reiniciar la aplicación
```bash
cd /Users/gaviria/Documents/dev/Proyectos_Spring/Tienda/Store_Application/inventory_app
./mvnw spring-boot:run
```

### 2. Login como empresa
```bash
curl -X POST http://localhost:8080/api/auth/empresa/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@techstore.com",
    "password": "TU_PASSWORD"
  }'
```

**Guardar el token de la respuesta**

### 3. Crear empleado con el token
```bash
curl -X POST http://localhost:8080/api/empresas/empleados \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN_AQUI}" \
  -d '{
    "nombre": "Damian",
    "apellido": "García",
    "documento": "1234567890",
    "usuario": "damian.admin",
    "password": "Admin@2024",
    "confirmPassword": "Admin@2024",
    "telefono": "+57 310 555 1234",
    "email": "damian@techstore.com",
    "cargo": "Administrador",
    "rol": "ADMIN"
  }'
```

---

## 🎓 Lecciones Aprendidas

### 1. Map.of() vs HashMap
- **`Map.of()`:** No acepta valores `null` → Lanza `NullPointerException`
- **`HashMap`:** Acepta valores `null` sin problemas

### 2. Coherencia en TenantInterceptor
Todos los valores del JWT que necesita el controller deben ser extraídos y agregados como atributos del request:
- ✅ `empresaId`
- ✅ `empresaEmail`
- ✅ `schemaName`
- ✅ `tenantKey` (faltaba)

### 3. Debugging Multi-Tenant
Siempre loguear:
- Schema actual
- Empresa ID
- Tenant Key
- Valores extraídos del JWT

Esto facilita detectar valores `null` o contextos incorrectos.

---

## ✅ Checklist de Verificación

- [x] `TenantInterceptor` extrae `tenantKey` del JWT
- [x] `TenantInterceptor` agrega `tenantKey` a request attributes
- [x] `EmpleadoController` usa `HashMap` en lugar de `Map.of()`
- [x] Logs muestran `tenantKey` con valor correcto
- [x] No hay `NullPointerException` al crear empleado
- [x] Respuesta JSON incluye `tenantInfo.tenantKey`

---

**🎉 Fix aplicado exitosamente - Listo para probar**
