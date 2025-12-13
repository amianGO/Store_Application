# 🚀 Guía de Despliegue - Render + Vercel

Esta guía te llevará paso a paso para desplegar tu aplicación multi-tenant en producción.

---

## 📋 Pre-requisitos

- [ ] Cuenta en [Render.com](https://render.com) (gratis)
- [ ] Cuenta en [Vercel](https://vercel.com) (gratis)
- [ ] Repositorio en GitHub con el código
- [ ] App Password de Gmail para envío de emails

---

## 🗄️ FASE 1: Base de Datos PostgreSQL en Render

### Paso 1.1: Crear Base de Datos

1. Ve a [Render Dashboard](https://dashboard.render.com)
2. Click en **"New +"** → **"PostgreSQL"**
3. Configura:
   - **Name**: `store-database` (o el nombre que prefieras)
   - **Database**: `store_db`
   - **User**: (genera automáticamente)
   - **Region**: Elige la más cercana a tus usuarios
   - **Plan**: **Free** (500MB, suficiente para testing)
4. Click **"Create Database"**

### Paso 1.2: Obtener Credenciales

Una vez creada, ve a la sección **"Info"** en Render y verás dos URLs:

**Internal Database URL** (para Render backend):
```
postgresql://store_db_iqbp_user:eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI@dpg-d4ud120gjchc73c6mr6g-a/store_db_iqbp
```
**Usar esta en:** Variable `DATABASE_URL` del backend en Render (Paso 2.3)

**External Database URL** (para tu terminal local):
```
postgresql://store_db_iqbp_user:eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI@dpg-d4ud120gjchc73c6mr6g-a.oregon-postgres.render.com/store_db_iqbp
```
**Usar esta para:** Conectarte desde tu Mac con psql (Paso 1.3)

**Diferencia clave:**
- Internal: Solo funciona dentro de Render (red interna)
- External: Funciona desde cualquier lugar (tiene el dominio completo .oregon-postgres.render.com)

### Paso 1.3: Configurar Schemas Multi-Tenant (OPCIONAL)

**IMPORTANTE:** Este paso es OPCIONAL. Los schemas se crean automáticamente cuando registras empresas.

Solo hazlo si quieres verificar la conexión o crear el schema template manualmente.

**Desde tu Mac (no Docker, solo terminal):**

```bash
# 1. Instalar psql si no lo tienes
brew install postgresql

# 2. Conectar usando la External URL (copia exactamente la tuya)
psql "postgresql://store_db_iqbp_user:eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI@dpg-d4ud120gjchc73c6mr6g-a.oregon-postgres.render.com/store_db_iqbp"

# 3. Si conecta exitosamente, verás:
# store_db_iqbp=>

# 4. (OPCIONAL) Crear schema template
CREATE SCHEMA IF NOT EXISTS template_schema;

# 5. Salir
\q
```

**NOTA:** Los schemas de empresas (empresa_1, empresa_2, etc.) se crean automáticamente cuando registras una nueva empresa desde la aplicación.

---

## ☕ FASE 2: Backend en Render

### Paso 2.1: Preparar Repositorio

Asegúrate de que tu repositorio tenga:
- ✅ `Dockerfile` en la raíz de `inventory_app/`
- ✅ `application-prod.properties` en `src/main/resources/`
- ✅ Código commiteado y pusheado a GitHub

### Paso 2.2: Crear Web Service

1. En Render Dashboard, click **"New +"** → **"Web Service"**
2. Conecta tu repositorio de GitHub
3. Configura:
   - **Name**: `store-backend`
   - **Region**: La misma que la base de datos
   - **Root Directory**: `inventory_app` (importante!)
   - **Environment**: **Docker**
   - **Plan**: **Free**

### Paso 2.3: Configurar Variables de Entorno

En la sección **"Environment Variables"**, agrega:

```bash
# Base de Datos (USA LA INTERNAL URL - sin .oregon-postgres.render.com)
DATABASE_URL=postgresql://store_db_iqbp_user:eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI@dpg-d4ud120gjchc73c6mr6g-a/store_db_iqbp

# JWT Secret (genera uno seguro con: openssl rand -base64 32)
JWT_SECRET_KEY=AQUI_VA_EL_RESULTADO_DEL_COMANDO_OPENSSL

# Email (Gmail App Password - 16 caracteres sin espacios)
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=abcdefghijklmnop

# Frontend URL (actualizarás esto después de desplegar el frontend)
FRONTEND_URL=https://placeholder.vercel.app

# Spring Profile
SPRING_PROFILES_ACTIVE=prod
```

**PASO A PASO para configurar:**

1. **DATABASE_URL:** Copia la INTERNAL URL de Render (Paso 1.2)
   ```
   La que NO tiene .oregon-postgres.render.com al final
   ```

2. **JWT_SECRET_KEY:** Genera en tu Mac:
   ```bash
   openssl rand -base64 32
   # Copia el resultado (ejemplo: "x7k9mP2nQ5rT8wV1yC3eF6hJ4lM7oR0s...")
   ```

3. **MAIL_USERNAME y MAIL_PASSWORD:** 
   - Ve a https://myaccount.google.com/security
   - Activa "Verificacion en 2 pasos"
   - Ve a https://myaccount.google.com/apppasswords
   - Genera password para "Mail" → "Otra app"
   - Copia el password de 16 caracteres (sin espacios)

4. **FRONTEND_URL:** Deja como placeholder por ahora
   - Lo actualizarás después de desplegar en Vercel
4. Genera un password para "Mail" → "Other (Custom name)"
5. Usa ese password de 16 caracteres en `MAIL_PASSWORD`

### Paso 2.4: Desplegar

1. Click **"Create Web Service"**
2. Espera 5-10 minutos mientras se construye
3. Una vez que veas **"Live"**, copia la URL: `https://store-backend.onrender.com`

### Paso 2.5: Verificar Logs

En la pestaña **"Logs"**, deberías ver:
```
Started InventoryAppApplication in X.XX seconds
```

Si hay errores, revisa las variables de entorno.

---

## ⚛️ FASE 3: Frontend en Vercel

### Paso 3.1: Preparar Variables de Entorno

En tu proyecto local, actualiza `frontend/.env.production`:

```bash
VITE_API_URL=https://store-backend.onrender.com
```

**NO** hagas commit de este archivo si tiene datos reales, solo úsalo como referencia.

### Paso 3.2: Desplegar en Vercel

1. Ve a [Vercel Dashboard](https://vercel.com/dashboard)
2. Click **"Add New..."** → **"Project"**
3. Importa tu repositorio de GitHub
4. Configura:
   - **Framework Preset**: Vite
   - **Root Directory**: `frontend`
   - **Build Command**: `npm run build` (autodetectado)
   - **Output Directory**: `dist` (autodetectado)

### Paso 3.3: Variables de Entorno en Vercel

En **"Environment Variables"**, agrega:

```bash
VITE_API_URL=https://store-backend.onrender.com
```

(Usa la URL de tu backend de Render)

### Paso 3.4: Desplegar

1. Click **"Deploy"**
2. Espera 2-3 minutos
3. Una vez completado, copia la URL: `https://tu-app.vercel.app`

### Paso 3.5: Actualizar FRONTEND_URL en Backend

1. Ve a Render → Tu Web Service → Environment
2. Actualiza `FRONTEND_URL` con la URL de Vercel
3. Click **"Save Changes"** (esto redesplegará el backend)

---

## 🔒 FASE 4: Configurar CORS

### Paso 4.1: Actualizar SecurityConfig.java

Necesitas permitir requests desde tu dominio de Vercel:

```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList(
        "http://localhost:5173",
        "http://localhost:5174",
        "https://tu-app.vercel.app"  // ← Agrega esto
    ));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("*"));
    configuration.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
}
```

Commit y push los cambios. Render redesplegaré automáticamente.

---

## ✅ FASE 5: Pruebas de Integración

### Paso 5.1: Verificar Conexión Backend

Abre en el navegador:
```
https://store-backend.onrender.com/actuator/health
```

Deberías ver:
```json
{"status":"UP"}
```

Si no hay endpoint `/actuator/health`, prueba con cualquier endpoint público.

### Paso 5.2: Probar Registro de Empresa

1. Ve a tu frontend: `https://tu-app.vercel.app`
2. Registra una nueva empresa
3. Revisa los logs en Render para ver si se creó el schema
4. Verifica el email de verificación

### Paso 5.3: Probar Login y Operaciones

1. Verifica el email de la empresa
2. Crea el primer empleado ADMIN
3. Inicia sesión como empleado
4. Crea productos, clientes, y realiza una venta
5. Genera un PDF de factura

---

## 📊 FASE 6: Migración de Datos (Opcional)

Si ya tienes datos en tu BD local que quieres migrar:

### Paso 6.1: Exportar desde Local

```bash
# Exportar solo el schema public (empresas)
pg_dump -U tu_usuario -h localhost -d store_local -n public --data-only > public_data.sql

# Exportar un schema de empresa específico
pg_dump -U tu_usuario -h localhost -d store_local -n empresa_1 > empresa_1.sql
```

### Paso 6.2: Importar a Render

```bash
# Conectar a Render (usa External Database URL)
psql "postgresql://usuario:password@hostname:5432/database" < public_data.sql

# Importar schema de empresa
psql "postgresql://usuario:password@hostname:5432/database" < empresa_1.sql
```

---

## 🐛 Troubleshooting

### Error: "Connection refused"
- Verifica que DATABASE_URL sea la **Internal Database URL** en Render
- Verifica que VITE_API_URL apunte al backend correcto

### Error: "CORS policy"
- Verifica que agregaste la URL de Vercel en `SecurityConfig.java`
- Verifica que el backend se redespleló después del cambio

### Error: "JWT signature does not match"
- Verifica que JWT_SECRET_KEY sea el mismo en todas partes
- Verifica que tenga al menos 32 caracteres

### Error: "Schema does not exist"
- Verifica que el registro de empresa se completó correctamente
- Revisa los logs de Render para ver si hubo errores al crear el schema

### Backend se suspende después de 15 minutos (Plan Free)
- Render suspende servicios inactivos en el plan gratuito
- Primera request después de suspensión puede tardar 30-60 segundos
- Considera usar cron job para mantenerlo activo, o upgrade a plan de pago

---

## 📝 Siguiente Fase: Sistema de Terminales

Una vez que todo esté funcionando en producción, el siguiente paso es implementar:

1. **Registro de Terminales Activas**: Cada vez que un empleado inicia sesión, registrar la terminal
2. **Contador de Terminales**: Mostrar en dashboard cuántas terminales están activas
3. **Validación de Límites**: Comparar terminales activas vs. límite de suscripción
4. **Cierre de Sesión Automático**: Después de inactividad

---

## 🎯 Checklist Final

- [ ] Base de datos PostgreSQL creada en Render
- [ ] Backend desplegado y "Live" en Render
- [ ] Variables de entorno configuradas (DATABASE_URL, JWT_SECRET_KEY, MAIL_*)
- [ ] Frontend desplegado en Vercel
- [ ] VITE_API_URL configurada en Vercel
- [ ] CORS configurado para permitir dominio de Vercel
- [ ] FRONTEND_URL actualizada en backend
- [ ] Registro de empresa funcional
- [ ] Login de empleado funcional
- [ ] CRUD de productos/clientes funcional
- [ ] Generación de facturas PDF funcional
- [ ] Multi-tenancy validado (schemas aislados)

---

## 📚 URLs de Referencia

- **Backend**: `https://store-backend.onrender.com`
- **Frontend**: `https://tu-app.vercel.app`
- **Base de Datos**: (desde Render Dashboard)
- **Render Docs**: https://render.com/docs
- **Vercel Docs**: https://vercel.com/docs

---

¡Listo para producción! 🎉
