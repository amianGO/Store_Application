# 📋 Entidades Multi-Tenant - Documentación

## ✅ Entidades Creadas

### 1. **Empresa** 
**Ubicación:** `Entities/Empresa.java`  
**Schema:** `public` (global)

**Propósito:**
- Registro central de todas las empresas del sistema
- Gestión de acceso y autenticación a nivel empresa
- Vinculación con suscripciones y schemas de base de datos

**Campos Principales:**
- `id` - Identificador único
- `nombre` - Razón social
- `nombreComercial` - Marca o nombre comercial
- `nit` - NIT único (identificación tributaria)
- `email` - Email corporativo único
- `password` - Contraseña encriptada (BCrypt)
- `telefono` - Teléfono de contacto
- `direccion`, `ciudad`, `pais` - Ubicación
- `schemaName` - Nombre del schema en DB (ej: "empresa_1")
- `tenantKey` - Clave única para URLs (ej: "mi-empresa")
- `activa` - Boolean: empresa habilitada/deshabilitada
- `verificada` - Boolean: empresa verificada
- `fechaRegistro` - Timestamp de registro
- `ultimoAcceso` - Timestamp del último login
- `logo` - URL del logo
- `sitioWeb` - Sitio web corporativo
- `suscripcionActiva` - Relación @OneToOne con Suscripcion

**Métodos Útiles:**
- `tieneAcceso()` - Verifica si puede acceder al sistema
- `generarSchemaName()` - Genera nombre del schema (empresa_{id})
- `generarTenantKey()` - Genera clave única basada en el nombre

---

### 2. **Suscripcion**
**Ubicación:** `Entities/Suscripcion.java`  
**Schema:** `public` (global)

**Propósito:**
- Gestión de planes y períodos de suscripción
- Control de acceso basado en estado
- Límites de uso por plan
- Facturación y renovaciones

**Campos Principales:**
- `id` - Identificador único
- `empresa` - Relación @OneToOne con Empresa
- `tipoPlan` - Enum: PRUEBA, BASICO, PROFESIONAL, EMPRESARIAL, PERSONALIZADO
- `estado` - Enum: ACTIVA, PRUEBA, SUSPENDIDA, EXPIRADA, CANCELADA
- `fechaInicio` - Inicio de vigencia
- `fechaVencimiento` - Fin de vigencia
- `licenseKey` - Clave única (formato: XXXX-XXXX-XXXX-XXXX)
- `maxTerminales` - Límite de sesiones concurrentes (-1 = ilimitado)
- `maxProductos` - Límite de productos (-1 = ilimitado)
- `maxEmpleados` - Límite de empleados (-1 = ilimitado)
- `terminalesActivas` - Contador en tiempo real
- `precioPagado` - Precio de la suscripción
- `metodoPago` - Método usado (Tarjeta, PayPal, etc.)
- `renovacionAutomatica` - Boolean: auto-renovación

**Métodos Útiles:**
- `estaActiva()` - Verifica si está activa y no expirada
- `puedeAgregarTerminal()` - Verifica si hay cupo disponible
- `agregarTerminalActiva()` - Incrementa contador
- `removerTerminalActiva()` - Decrementa contador
- `diasRestantes()` - Calcula días hasta vencimiento
- `renovar(meses)` - Extiende la suscripción

---

### 3. **TipoPlan** (Enum)
**Ubicación:** `Entities/TipoPlan.java`

**Planes Disponibles:**

| Plan | Precio/Mes | Terminales | Productos | Empleados |
|------|------------|------------|-----------|-----------|
| PRUEBA | $0 | 1 | 100 | 10 |
| BASICO | $29.99 | 3 | 1,000 | 50 |
| PROFESIONAL | $79.99 | 10 | 5,000 | 200 |
| EMPRESARIAL | $199.99 | Ilimitado | Ilimitado | Ilimitado |
| PERSONALIZADO | Variable | Variable | Variable | Variable |

**Métodos Estáticos:**
- `dentroDelLimite(cantidad, limite)` - Verifica límites
- `esIlimitado(limite)` - Verifica si es -1 (ilimitado)

---

### 4. **EstadoSuscripcion** (Enum)
**Ubicación:** `Entities/EstadoSuscripcion.java`

**Estados:**
- `ACTIVA` - Suscripción funcionando normalmente
- `PRUEBA` - Período de prueba
- `SUSPENDIDA` - Temporalmente deshabilitada
- `EXPIRADA` - Período venció sin renovación
- `CANCELADA` - Cancelación permanente
- `PENDIENTE_RENOVACION` - Esperando confirmación de pago

**Métodos:**
- `permiteAcceso()` - true si es ACTIVA o PRUEBA
- `puedeRenovarse()` - true si puede renovarse

---

## 🔄 Flujo de Registro y Suscripción

```
1. REGISTRO DE EMPRESA
   ↓
   - Se crea Empresa (schema public)
   - Se genera tenantKey único
   - Estado: activa=true, verificada=false
   
2. CREACIÓN DE SUSCRIPCIÓN INICIAL
   ↓
   - Se crea Suscripcion con plan PRUEBA
   - Se genera licenseKey única
   - Duración: 15-30 días
   - Estado: PRUEBA
   
3. CREACIÓN DE SCHEMA DEDICADO
   ↓
   - Se ejecuta: CREATE SCHEMA empresa_{id}
   - Se clonan tablas desde template_schema
   - Se registra schemaName en Empresa
   
4. VERIFICACIÓN (Opcional)
   ↓
   - Email de verificación
   - Empresa.verificada = true
   
5. ACTIVACIÓN DE PLAN PAGADO
   ↓
   - Cliente selecciona plan (BASICO, PROFESIONAL, etc.)
   - Se procesa pago
   - Se actualiza Suscripcion:
     * tipoPlan = seleccionado
     * estado = ACTIVA
     * fechaVencimiento = +1 mes
     * precioPagado, metodoPago, referenciaPago
   
6. USO DEL SISTEMA
   ↓
   - Login → TenantContext.setCurrentTenant(schemaName)
   - Verificar: empresa.tieneAcceso()
   - Verificar: suscripcion.estaActiva()
   - Verificar: suscripcion.puedeAgregarTerminal()
   - Incrementar: suscripcion.agregarTerminalActiva()
   - Todas las queries usan: schema_{empresa_id}
   
7. LOGOUT
   ↓
   - Decrementar: suscripcion.removerTerminalActiva()
   - TenantContext.clear()
   
8. RENOVACIÓN
   ↓
   - Automática: si renovacionAutomatica=true
   - Manual: cliente renueva antes de vencimiento
   - Se ejecuta: suscripcion.renovar(meses)
   
9. EXPIRACIÓN
   ↓
   - Si no renueva antes de fechaVencimiento
   - Estado cambia a: EXPIRADA
   - tieneAcceso() → false
   - Se bloquea el acceso
```

---

## 📊 Relaciones Entre Entidades

```
┌─────────────┐
│   Empresa   │
│  (public)   │
│             │
│ - id        │
│ - nombre    │
│ - nit       │
│ - email     │
│ - schemaName│
└──────┬──────┘
       │ @OneToOne
       │ (mappedBy="empresa")
       ↓
┌─────────────┐
│ Suscripcion │
│  (public)   │
│             │
│ - tipoPlan  │──→ TipoPlan (ENUM)
│ - estado    │──→ EstadoSuscripcion (ENUM)
│ - licenseKey│
│ - maxLimites│
└─────────────┘
```

---

## 🎯 Próximos Pasos

1. ✅ **Entidades creadas** (Completado)
2. ⏳ **Crear Repositories**
   - EmpresaRepository
   - SuscripcionRepository
3. ⏳ **Crear Services**
   - EmpresaService
   - SuscripcionService
   - SchemaManagementService (crear schemas dinámicamente)
4. ⏳ **Crear Controllers/APIs**
   - EmpresaController (registro, login)
   - SuscripcionController (activar, renovar)
5. ⏳ **Crear Interceptor/Filter**
   - TenantInterceptor (establecer schema según empresa logueada)
6. ⏳ **Scripts de Base de Datos**
   - template_schema (plantilla con estructura de tablas)
   - Procedimiento para clonar schema
7. ⏳ **Sistema de Autenticación**
   - JWT con información de empresa y schema
   - Validación de suscripción en cada request

---

## 💡 Notas Importantes

### Almacenamiento de Suscripciones
**¿Por qué en schema público?**
- ✅ Control centralizado de todas las empresas
- ✅ Validación de acceso antes de conectar a schema tenant
- ✅ Reportes globales de suscripciones
- ✅ Gestión de pagos y renovaciones
- ✅ No depende del schema del tenant (puede estar inactivo)

### License Key
- **Formato:** XXXX-XXXX-XXXX-XXXX
- **Propósito:** 
  - Activar terminales/aplicaciones
  - Soporte técnico
  - Verificación de autenticidad

### Límites (-1 = Ilimitado)
- Se usa `-1` para indicar "sin límite"
- Facilita validaciones: `if (limite == -1 || actual < limite)`

### Terminales Activas
- Contador en tiempo real
- Se incrementa en login
- Se decrementa en logout
- Previene exceder límite del plan

---

## 🔐 Seguridad

### Password
- **NUNCA** almacenar en texto plano
- Usar BCrypt para encriptación
- Complejidad mínima requerida

### Tenant Isolation
- Cada empresa en su propio schema
- Sin posibilidad de acceso cruzado
- Validación en cada request

### License Key
- Única por suscripción
- No reutilizable
- Almacenada de forma segura

---

**Creado por:** DamianG  
**Fecha:** 23 de noviembre de 2025  
**Versión:** 1.0
