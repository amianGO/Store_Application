# 🔧 Configuración Local del Proyecto

## 📋 Pre-requisitos

- Java 21 (JDK)
- PostgreSQL 15+ (corriendo en puerto 5433)
- Node.js 18+ y npm
- Git

---

## 🗄️ Paso 1: Configurar Base de Datos

### Opción A: PostgreSQL con Docker
```bash
docker run --name postgres-store \
  -e POSTGRES_USER=docker_admin \
  -e POSTGRES_PASSWORD=tu_password_seguro \
  -e POSTGRES_DB=app_main \
  -p 5433:5432 \
  -d postgres:15
```

### Opción B: PostgreSQL Local
```bash
# Crear base de datos
psql -U postgres
CREATE DATABASE app_main;
CREATE USER docker_admin WITH PASSWORD 'tu_password_seguro';
GRANT ALL PRIVILEGES ON DATABASE app_main TO docker_admin;
\q
```

---

## ☕ Paso 2: Configurar Backend

### 2.1 Crear archivo de configuración

```bash
cd inventory_app/src/main/resources
cp application.properties.example application.properties
```

### 2.2 Editar application.properties

Abre `application.properties` y configura:

```properties
# Base de Datos (ajusta según tu configuración)
spring.datasource.url=jdbc:postgresql://localhost:5433/app_main
spring.datasource.username=docker_admin
spring.datasource.password=tu_password_seguro

# Email (Gmail)
spring.mail.username=tu_email@gmail.com
spring.mail.password=tu_app_password_de_16_caracteres

# JWT Secret (genera uno nuevo)
jwt.secret=tu_jwt_secret_aqui
```

### 2.3 Generar JWT Secret

```bash
openssl rand -base64 32
# Copia el resultado a jwt.secret en application.properties
```

### 2.4 Obtener Gmail App Password

1. Ve a: https://myaccount.google.com/security
2. Activa "Verificación en 2 pasos"
3. Ve a: https://myaccount.google.com/apppasswords
4. Crea password para "Mail" → "Otra (nombre personalizado)"
5. Copia el password de 16 caracteres
6. Pégalo en `spring.mail.password` en application.properties

### 2.5 Ejecutar Backend

```bash
cd inventory_app
./mvnw spring-boot:run
```

O desde tu IDE (IntelliJ IDEA, Eclipse, VS Code):
- Run → Run 'InventoryAppApplication'

Deberías ver:
```
Started InventoryAppApplication in X.XXX seconds
```

---

## ⚛️ Paso 3: Configurar Frontend

### 3.1 Instalar dependencias

```bash
cd frontend
npm install
```

### 3.2 Crear archivo de configuración (OPCIONAL)

El frontend usa las variables de entorno por defecto:
- `VITE_API_URL=http://localhost:8080` (definido en código)

Si quieres cambiar la URL del backend:

```bash
cd frontend
cp .env.example .env
```

Edita `.env`:
```bash
VITE_API_URL=http://localhost:8080
```

### 3.3 Ejecutar Frontend

```bash
npm run dev
```

Deberías ver:
```
VITE v5.x.x  ready in XXX ms

➜  Local:   http://localhost:5173/
```

---

## 🧪 Paso 4: Verificar Instalación

### 4.1 Probar Backend

Abre en el navegador o usa curl:
```bash
curl http://localhost:8080/actuator/health
# Debería retornar: {"status":"UP"}
```

### 4.2 Probar Frontend

1. Abre: http://localhost:5173
2. Deberías ver la página de inicio
3. Intenta registrar una empresa

### 4.3 Probar Email

Después de registrar una empresa:
- Revisa tu bandeja de entrada
- Deberías recibir un email de verificación
- Si no llega, revisa spam

---

## 🛠️ Comandos Útiles

### Backend

```bash
# Compilar sin ejecutar tests
./mvnw clean package -DskipTests

# Ejecutar tests
./mvnw test

# Limpiar target/
./mvnw clean

# Actualizar dependencias
./mvnw dependency:resolve
```

### Frontend

```bash
# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm run dev

# Build para producción
npm run build

# Preview del build
npm run preview

# Linting
npm run lint
```

### Base de Datos

```bash
# Conectar a PostgreSQL
psql -U docker_admin -h localhost -p 5433 -d app_main

# Ver schemas
\dn

# Ver tablas de un schema
\dt public.*
\dt empresa_1.*

# Backup
pg_dump -U docker_admin -h localhost -p 5433 app_main > backup.sql

# Restaurar
psql -U docker_admin -h localhost -p 5433 app_main < backup.sql
```

---

## 🐛 Troubleshooting Común

### Error: "Connection refused" (Backend)

**Problema:** No puede conectar a PostgreSQL

**Soluciones:**
```bash
# Verificar que PostgreSQL esté corriendo
docker ps  # (si usas Docker)
# o
sudo systemctl status postgresql  # (si usas instalación local)

# Verificar puerto correcto (5433 vs 5432)
netstat -an | grep 5433

# Verificar credenciales en application.properties
```

### Error: "Authentication failed" (Backend)

**Problema:** Usuario/password de PostgreSQL incorrecto

**Solución:**
```sql
-- Cambiar password del usuario
psql -U postgres
ALTER USER docker_admin WITH PASSWORD 'nuevo_password';
\q

-- Actualizar application.properties con el nuevo password
```

### Error: "CORS policy" (Frontend)

**Problema:** Frontend no puede comunicarse con backend

**Solución:**
- Verifica que el backend esté corriendo en http://localhost:8080
- Verifica que `SecurityConfig.java` tenga `http://localhost:5173` en allowedOrigins
- Verifica la consola del navegador para ver el error exacto

### Error: "Email not sent" (Backend)

**Problema:** No se envían emails de verificación

**Soluciones:**
```properties
# 1. Verifica que el App Password sea correcto (16 caracteres sin espacios)
spring.mail.password=abcd efgh ijkl mnop  # ❌ MAL (con espacios)
spring.mail.password=abcdefghijklmnop     # ✅ BIEN

# 2. Verifica que la verificación en 2 pasos esté activa
# https://myaccount.google.com/security

# 3. Revisa los logs del backend para ver el error específico
```

### Error: "Schema does not exist" (Backend)

**Problema:** Se creó una empresa pero no se creó el schema

**Solución:**
```bash
# Conectar a la base de datos
psql -U docker_admin -h localhost -p 5433 -d app_main

# Verificar que existe el schema template
\dn

# Si no existe, ejecutar los scripts de migración
\i db/migration/create_template_schema.sql
```

---

## 📁 Estructura del Proyecto

```
Store_Application/
├── inventory_app/          # Backend (Spring Boot)
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   │       ├── application.properties        # ❌ NO COMMITEAR
│   │   │       ├── application.properties.example # ✅ Template
│   │   │       └── application-prod.properties   # Config producción
│   │   └── test/
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/               # Frontend (React + Vite)
│   ├── src/
│   ├── public/
│   ├── package.json
│   ├── .env                # ❌ NO COMMITEAR (si lo creas)
│   └── .env.example        # ✅ Template
│
└── docs/                   # Documentación
    ├── DESPLIEGUE_RAPIDO.md
    ├── GUIA_DESPLIEGUE.md
    └── SEGURIDAD_PRODUCCION.md
```

---

## 🔐 Archivos Sensibles (NUNCA COMMITEAR)

❌ `inventory_app/src/main/resources/application.properties`  
❌ `frontend/.env` (si lo creas)  
❌ Cualquier archivo con passwords, tokens, o API keys

✅ En su lugar, commitea archivos `.example` sin credenciales reales

---

## ✅ Checklist de Configuración

- [ ] PostgreSQL instalado y corriendo
- [ ] Base de datos `app_main` creada
- [ ] Usuario `docker_admin` configurado
- [ ] Java 21 JDK instalado
- [ ] Node.js 18+ instalado
- [ ] `application.properties` copiado de `.example` y configurado
- [ ] JWT Secret generado
- [ ] Gmail App Password obtenido y configurado
- [ ] Backend corriendo en http://localhost:8080
- [ ] Frontend corriendo en http://localhost:5173
- [ ] Registro de empresa funcional
- [ ] Email de verificación recibido

---

## 🚀 Próximos Pasos

Una vez que todo esté funcionando localmente:
1. Familiarízate con las funcionalidades
2. Prueba crear empresas, empleados, productos, clientes
3. Realiza algunas ventas y genera PDFs
4. Cuando estés listo, sigue `DESPLIEGUE_RAPIDO.md` para desplegar en Render + Vercel

---

## 📚 Documentación Adicional

- **Spring Boot**: https://spring.io/projects/spring-boot
- **Vite**: https://vitejs.dev
- **PostgreSQL**: https://www.postgresql.org/docs
- **Material-UI**: https://mui.com

---

**¿Problemas?** Revisa los logs del backend y frontend, y verifica que todas las configuraciones sean correctas.
