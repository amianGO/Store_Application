# 📧 Verificación de Email

## 1. Verificar Email con Token

**Endpoint:** `GET /api/auth/verificar-email?token={token}`

**Descripción:** Verifica el email de una empresa usando el token enviado por correo.

**Headers:** Ninguno (público)

**Query Params:**
- `token` ✅ **Requerido** - Token de verificación único

**Ejemplo Request:**
```
GET http://localhost:8080/api/auth/verificar-email?token=550e8400-e29b-41d4-a716-446655440000
```

**Respuesta Exitosa (200 OK):**
```json
{
  "success": true,
  "message": "Email verificado exitosamente. Ahora puedes iniciar sesión.",
  "empresa": {
    "id": 1,
    "nombre": "TechStore Colombia SAS",
    "email": "contacto@techstore.com",
    "emailVerificado": true,
    "fechaVerificacion": "2025-12-06T20:30:00.000+00:00",
    "activa": true
  }
}
```

**Respuesta Error (400 Bad Request):**
```json
{
  "success": false,
  "message": "Token de verificación inválido o expirado"
}
```

---

## 2. Reenviar Email de Verificación

**Endpoint:** `POST /api/auth/reenviar-verificacion`

**Descripción:** Genera un nuevo token y reenvía el email de verificación.

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

**Respuesta Exitosa (200 OK):**
```json
{
  "success": true,
  "message": "Se ha enviado un nuevo email de verificación"
}
```

**Respuesta Error (400 Bad Request):**
```json
{
  "success": false,
  "message": "El email ya está verificado"
}
```

---

## ⚠️ **Cambios Importantes en el Registro:**

### **Flujo Actualizado:**

```
1. POST /api/auth/empresa/registro
   ↓
2. Sistema genera token y lo guarda en BD
   ↓
3. Sistema envía email con link de verificación (TODO)
   ↓
4. Usuario hace clic en link → GET /api/auth/verificar-email?token=...
   ↓
5. Sistema marca emailVerificado = true
   ↓
6. Usuario puede hacer login → POST /api/auth/empresa/login
```

### **Campo Nuevo en Respuesta de Registro:**

```json
{
  "id": 1,
  "nombre": "TechStore Colombia SAS",
  "emailVerificado": false,  // ← NUEVO (antes era "verificada")
  "fechaVerificacion": null, // ← NUEVO
  "activa": true
}
```

### **Validación en Login:**

El login **SOLO funciona** si `emailVerificado = true`. Si no:

```json
{
  "error": "INVALID_CREDENTIALS",
  "mensaje": "Acceso denegado: Email no verificado. Revise su correo."
}
```

---

# 🔧 Cambios en DTOs

## EmpresaResponseDTO - Campos Actualizados:

```json
{
  "id": 1,
  "nombre": "TechStore Colombia SAS",
  "emailVerificado": true,        // ← Cambió de "verificada"
  "fechaVerificacion": "2025-12-06T20:30:00.000+00:00", // ← NUEVO
  "activa": true,
  "fechaRegistro": "2025-12-06T10:00:00.000+00:00"
}
```

---

