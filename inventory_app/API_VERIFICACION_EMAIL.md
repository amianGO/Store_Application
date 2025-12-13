# 📧 **VERIFICACIÓN DE EMAIL**

## 1️⃣ Verificar Email con Token

**Endpoint:** `GET /api/auth/verificar-email?token={token}`

**Método:** `GET`

**Autenticación:** No requiere (público)

**Query Parameters:**
- `token` - Token de verificación (UUID generado automáticamente al registrar empresa)

**Descripción:** Verifica el email de una empresa usando el token único enviado por correo.

### **Ejemplo Request:**

```http
GET http://localhost:8080/api/auth/verificar-email?token=550e8400-e29b-41d4-a716-446655440000
```

### **Respuesta Exitosa (200 OK):**

```json
{
  "success": true,
  "message": "Email verificado exitosamente. Ahora puedes iniciar sesión.",
  "empresa": {
    "id": 8,
    "nombre": "TechStore Colombia SAS",
    "email": "contacto@techstore.com",
    "emailVerificado": true,
    "fechaVerificacion": "2025-12-07T12:30:45.123+00:00",
    "tenantKey": "techstore-abc123",
    "schemaName": "empresa_8",
    "activa": true
  }
}
```

### **Respuesta Error (400 Bad Request):**

```json
{
  "success": false,
  "message": "Token de verificación inválido o expirado"
}
```

---

## 2️⃣ Reenviar Email de Verificación

**Endpoint:** `POST /api/auth/reenviar-verificacion`

**Método:** `POST`

**Autenticación:** No requiere (público)

**Headers:**
```
Content-Type: application/json
```

**Body:**
```json
{
  "email": "contacto@techstore.com"
}
```

**Descripción:** Genera un nuevo token de verificación y lo reenvía al email (en desarrollo, se muestra en consola).

### **Respuesta Exitosa (200 OK):**

```json
{
  "success": true,
  "message": "Se ha enviado un nuevo email de verificación",
  "tokenVerificacion": 8
}
```

**⚠️ NOTA:** En producción, el `tokenVerificacion` NO debería enviarse en la respuesta, solo enviarse por email.

### **Respuesta Error (400 Bad Request):**

```json
{
  "success": false,
  "message": "El email ya está verificado"
}
```

---

## 📝 **FLUJO COMPLETO DE VERIFICACIÓN:**

### **1. Registro de Empresa**
```http
POST /api/auth/empresa/registro
Content-Type: application/json

{
  "nombre": "TechStore Colombia SAS",
  "nombreComercial": "TechStore",
  "nit": "900123456-7",
  "email": "contacto@techstore.com",
  "password": "Admin@2024",
  "confirmPassword": "Admin@2024",
  "telefono": "3001234567",
  "direccion": "Calle 100 #15-20",
  "ciudad": "Bogotá",
  "pais": "Colombia"
}
```

**Respuesta:**
```json
{
  "id": 8,
  "nombre": "TechStore Colombia SAS",
  "email": "contacto@techstore.com",
  "emailVerificado": false,  // ⬅ Pendiente de verificar
  "tokenVerificacion": null,  // No se envía al cliente
  "tenantKey": "techstore-abc123",
  "schemaName": "empresa_8"
}
```

### **2. Obtener Token de Verificación (solo para desarrollo)**

Para pruebas en Postman, consulta la base de datos:

```sql
SELECT id, email, token_verificacion 
FROM public.empresas 
WHERE email = 'contacto@techstore.com';
```

**Resultado:**
```
id | email                    | token_verificacion
8  | contacto@techstore.com   | 550e8400-e29b-41d4-a716-446655440000
```

### **3. Verificar Email**

```http
GET http://localhost:8080/api/auth/verificar-email?token=550e8400-e29b-41d4-a716-446655440000
```

**Respuesta:**
```json
{
  "success": true,
  "message": "Email verificado exitosamente. Ahora puedes iniciar sesión.",
  "empresa": {
    "emailVerificado": true,
    "fechaVerificacion": "2025-12-07T12:30:45.123+00:00"
  }
}
```

### **4. Login de Empresa (ahora permitido)**

```http
POST /api/auth/empresa/login
Content-Type: application/json

{
  "email": "contacto@techstore.com",
  "password": "Admin@2024"
}
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "empresa": {
    "id": 8,
    "nombre": "TechStore Colombia SAS",
    "emailVerificado": true
  }
}
```

---

## 🔒 **CAMBIOS EN EL LOGIN:**

### **Login de Empresa SIN Email Verificado:**

```http
POST /api/auth/empresa/login
{
  "email": "contacto@techstore.com",
  "password": "Admin@2024"
}
```

**Respuesta 401 Unauthorized:**
```json
{
  "error": "INVALID_CREDENTIALS",
  "mensaje": "Acceso denegado: Email no verificado. Revise su correo.",
  "timestamp": "2025-12-07T12:00:00"
}
```

### **Login de Empresa CON Email Verificado:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "empresa": { ... }
}
```

---

## 🧪 **COLECCIÓN POSTMAN - Verificación de Email:**

```json
{
  "name": "Verificación de Email",
  "item": [
    {
      "name": "1. Registrar Empresa",
      "request": {
        "method": "POST",
        "url": "{{baseUrl}}/api/auth/empresa/registro",
        "body": {
          "mode": "raw",
          "raw": "{\n  \"nombre\": \"TechStore Colombia SAS\",\n  \"email\": \"contacto@techstore.com\",\n  \"password\": \"Admin@2024\",\n  \"confirmPassword\": \"Admin@2024\",\n  ...\n}"
        }
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "// Guardar ID de empresa",
              "pm.environment.set('empresaId', pm.response.json().id);"
            ]
          }
        }
      ]
    },
    {
      "name": "2. Verificar Email",
      "request": {
        "method": "GET",
        "url": "{{baseUrl}}/api/auth/verificar-email?token={{tokenVerificacion}}"
      }
    },
    {
      "name": "3. Login Empresa",
      "request": {
        "method": "POST",
        "url": "{{baseUrl}}/api/auth/empresa/login",
        "body": {
          "mode": "raw",
          "raw": "{\n  \"email\": \"contacto@techstore.com\",\n  \"password\": \"Admin@2024\"\n}"
        }
      },
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "// Guardar token JWT",
              "pm.environment.set('tokenEmpresa', pm.response.json().token);"
            ]
          }
        }
      ]
    }
  ]
}
```

---

## 📌 **NOTAS IMPORTANTES:**

1. **Token de Verificación:**
   - Se genera automáticamente al crear la empresa
   - Es un UUID único (ej: `550e8400-e29b-41d4-a716-446655440000`)
   - Se almacena en `public.empresas.token_verificacion`

2. **Validación en Login:**
   - Si `emailVerificado = false`, el login será rechazado
   - Si `emailVerificado = true`, el login es permitido

3. **Regenerar Token:**
   - Use `POST /api/auth/reenviar-verificacion` para generar nuevo token
   - Solo funciona si el email aún no está verificado

4. **Después de Verificar:**
   - El campo `tokenVerificacion` se limpia (se pone en `null`)
   - El campo `fechaVerificacion` se actualiza con la fecha/hora actual
   - El campo `emailVerificado` se pone en `true`

---

**Última actualización:** 2025-12-07
