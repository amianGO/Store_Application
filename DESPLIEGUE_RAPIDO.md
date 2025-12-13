# ⚡ Despliegue Rápido - Comandos Esenciales

## 🔑 Generar JWT Secret (ejecutar PRIMERO)
```bash
openssl rand -base64 32
```
**Copiar el resultado** - Lo necesitarás en Render como `JWT_SECRET_KEY`

---

## 🔐 Obtener Gmail App Password

1. Ve a: https://myaccount.google.com/security
2. Activa "2-Step Verification"
3. Ve a: https://myaccount.google.com/apppasswords
4. Genera password para "Mail" → "Other"
5. Copia el password de 16 caracteres

---

## 📦 Verificar Configuración Pre-Despliegue
```bash
cd /Users/gaviria/Documents/dev/Proyectos_Spring/Tienda/Store_Application
./verificar-despliegue.sh
```

---

## 🗄️ RENDER: Base de Datos PostgreSQL

### Crear Base de Datos
```
Render Dashboard → New + → PostgreSQL
  Name: store-database
  Database: store_db
  Region: Oregon (US West)
  Plan: Free
```

### Copiar URLs
```
Después de crear:
- Info → Internal Database URL (para backend en Render)
- Info → External Database URL (para acceso local/migraciones)
```

---

## ☕ RENDER: Backend (Spring Boot)

### Crear Web Service
```
Render Dashboard → New + → Web Service
  Repo: Tu repositorio GitHub
  Name: store-backend
  Region: Oregon (US West)
  Root Directory: inventory_app
  Environment: Docker
  Plan: Free
```

### Variables de Entorno (copiar exactamente)
```bash
DATABASE_URL=[pegar Internal Database URL aquí]
JWT_SECRET_KEY=[pegar resultado de openssl aquí]
MAIL_USERNAME=tu_email@gmail.com
MAIL_PASSWORD=[pegar Gmail App Password aquí]
FRONTEND_URL=https://placeholder.vercel.app
SPRING_PROFILES_ACTIVE=prod
```

**NOTA**: Actualizarás `FRONTEND_URL` después de desplegar el frontend.

### Verificar Deploy
```
Logs → Buscar: "Started InventoryAppApplication"
Si ves esto, ¡está funcionando! ✅
```

---

## ⚛️ VERCEL: Frontend (React)

### Crear Proyecto
```
Vercel Dashboard → Add New... → Project
  Repo: Tu repositorio GitHub
  Framework Preset: Vite
  Root Directory: frontend
  Build Command: npm run build (auto)
  Output Directory: dist (auto)
```

### Variables de Entorno
```bash
VITE_API_URL=https://tu-backend.onrender.com
```

**IMPORTANTE**: Reemplaza `tu-backend` con el nombre real de tu backend en Render.

### Verificar Deploy
```
Visita la URL de Vercel que te asignen
Deberías ver tu aplicación cargando
```

---

## 🔄 Actualizar Referencias Cruzadas

### 1. Actualizar FRONTEND_URL en Render
```
Render → store-backend → Environment
  Buscar: FRONTEND_URL
  Cambiar a: https://tu-app.vercel.app (tu URL real de Vercel)
  → Save Changes
```

Esto redespleará automáticamente el backend (toma 5-10 min).

### 2. Actualizar CORS en el código

Archivo: `inventory_app/src/main/java/com/example/inventory_app/config/SecurityConfig.java`

Buscar el método `corsConfigurationSource()` y agregar tu URL de Vercel:

```java
configuration.setAllowedOrigins(Arrays.asList(
    "http://localhost:5173",
    "http://localhost:5174",
    "https://tu-app.vercel.app"  // ← Agregar esta línea
));
```

Luego:
```bash
git add .
git commit -m "feat: add Vercel URL to CORS"
git push
```

Render redespleará automáticamente (5-10 min).

---

## ✅ Verificación Post-Despliegue

### Probar Backend
```bash
# Reemplaza con tu URL real de Render
curl https://tu-backend.onrender.com/actuator/health

# Deberías ver:
# {"status":"UP"}
```

### Probar Frontend
```
1. Abre: https://tu-app.vercel.app
2. Registra una nueva empresa
3. Verifica el email
4. Crea el primer empleado
5. Inicia sesión
6. Crea un producto
7. Realiza una venta
```

---

## 🗄️ Migrar Datos (OPCIONAL)

Si ya tienes datos en tu BD local:

### Exportar desde Local
```bash
# Empresas
pg_dump -U tu_usuario -h localhost -d store_local -n public --data-only > public_data.sql

# Un tenant específico (ejemplo: empresa_1)
pg_dump -U tu_usuario -h localhost -d store_local -n empresa_1 > empresa_1.sql
```

### Importar a Render
```bash
# Conectar con External Database URL de Render
psql "postgresql://user:pass@host:5432/db" < public_data.sql
psql "postgresql://user:pass@host:5432/db" < empresa_1.sql
```

---

## 🐛 Troubleshooting Rápido

### ❌ "Connection refused" en el frontend
```
Problema: VITE_API_URL mal configurado
Solución: Vercel → tu-proyecto → Settings → Environment Variables
         Verifica que VITE_API_URL tenga la URL correcta de Render
         Redeploy: Deployments → ... → Redeploy
```

### ❌ "CORS policy" en la consola del navegador
```
Problema: URL de Vercel no está en SecurityConfig.java
Solución: Agregar URL a allowedOrigins, commit, push
```

### ❌ Backend se tarda mucho en responder (primera request)
```
Problema: Plan Free suspende el servicio después de 15 min de inactividad
Solución: Normal. Primera request toma 30-60 segundos mientras "despierta"
```

### ❌ "JWT signature does not match"
```
Problema: JWT_SECRET_KEY diferente entre local y producción
Solución: Generar nuevo secret con openssl, actualizar en Render
```

---

## 📊 Monitoreo

### Logs del Backend
```
Render → store-backend → Logs (pestaña)
```

### Logs del Frontend
```
Vercel → tu-proyecto → Logs (pestaña)
```

### Métricas de Base de Datos
```
Render → store-database → Metrics
- Storage: Máximo 500MB en plan Free
- Connections: Monitorea conexiones activas
```

---

## 🎯 Checklist Mínimo

- [ ] JWT Secret generado
- [ ] Gmail App Password obtenido
- [ ] PostgreSQL creada en Render
- [ ] Backend desplegado en Render (status: Live)
- [ ] Frontend desplegado en Vercel
- [ ] FRONTEND_URL actualizada en Render
- [ ] CORS actualizado en código (commit + push)
- [ ] Registro de empresa funcional
- [ ] Login funcional

---

## 📚 Documentación Completa

Para guía detallada paso a paso:
→ `GUIA_DESPLIEGUE.md`

Para registrar URLs y credenciales:
→ `CONFIGURACION_URLS.md`

---

## 🚀 ¡Listo!

Una vez completado el checklist, tu aplicación estará en producción y lista para probar el sistema de terminales y suscripciones.

**URLs de Servicio:**
- Backend: `https://[tu-nombre].onrender.com`
- Frontend: `https://[tu-nombre].vercel.app`
- BD: Desde Render Dashboard

---

**Tiempo estimado de despliegue:** 30-45 minutos
**Costo:** $0 (planes gratuitos)
