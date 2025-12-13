# Explicacion de URLs de Base de Datos - Render

## TUS URLs GENERADAS

### Internal URL (para Render backend)
```
postgresql://store_db_iqbp_user:eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI@dpg-d4ud120gjchc73c6mr6g-a/store_db_iqbp
```

### External URL (para tu Mac)
```
postgresql://store_db_iqbp_user:eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI@dpg-d4ud120gjchc73c6mr6g-a.oregon-postgres.render.com/store_db_iqbp
```

---

## DIFERENCIAS Y CUANDO USAR CADA UNA

### Internal URL
**Que es:** Conexion interna dentro de Render (red privada)
**Cuando usar:** En la variable DATABASE_URL del backend en Render
**Donde:** Render Dashboard → tu Web Service → Environment Variables
**Por que:** Es mas rapida y segura (red privada de Render)

**Caracteristica:** NO tiene el dominio completo (.oregon-postgres.render.com)

### External URL
**Que es:** Conexion publica desde Internet
**Cuando usar:** Desde tu Mac con psql o para migraciones
**Donde:** Terminal de tu Mac (no Docker)
**Por que:** Permite conectarte desde fuera de Render

**Caracteristica:** Tiene el dominio completo (.oregon-postgres.render.com)

---

## DESGLOSE DE LAS URLs

### Estructura de una URL de PostgreSQL
```
postgresql://USUARIO:PASSWORD@HOST:PUERTO/DATABASE
```

### Tu Internal URL desglosada:
```
postgresql://
  store_db_iqbp_user          <- Usuario
  :
  eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI  <- Password
  @
  dpg-d4ud120gjchc73c6mr6g-a  <- Host (interno de Render)
  /
  store_db_iqbp               <- Nombre de la base de datos
```

### Tu External URL desglosada:
```
postgresql://
  store_db_iqbp_user          <- Usuario (igual)
  :
  eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI  <- Password (igual)
  @
  dpg-d4ud120gjchc73c6mr6g-a.oregon-postgres.render.com  <- Host completo
  /
  store_db_iqbp               <- Nombre de la base de datos (igual)
```

**DIFERENCIA:** Solo cambia el HOST
- Internal: dpg-d4ud120gjchc73c6mr6g-a
- External: dpg-d4ud120gjchc73c6mr6g-a.oregon-postgres.render.com

---

## GUIA PASO A PASO: DONDE USAR CADA URL

### PASO 1: Configurar Backend en Render

**Cuando:** Al crear el Web Service en Render (FASE 2, Paso 2.3)
**Donde:** Render Dashboard → store-backend → Environment Variables
**Variable:** DATABASE_URL
**Usar:** INTERNAL URL

```bash
DATABASE_URL=postgresql://store_db_iqbp_user:eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI@dpg-d4ud120gjchc73c6mr6g-a/store_db_iqbp
```

**Por que Internal:** Tu backend corre dentro de Render, puede usar la red privada (mas rapido)

---

### PASO 2: Conectarte desde tu Mac (OPCIONAL)

**Cuando:** Si quieres verificar la BD o hacer migraciones
**Donde:** Terminal de tu Mac (NO Docker, la terminal normal)
**Comando:** psql con EXTERNAL URL

```bash
# Asegurate de tener psql instalado
brew install postgresql

# Conectar usando External URL
psql "postgresql://store_db_iqbp_user:eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI@dpg-d4ud120gjchc73c6mr6g-a.oregon-postgres.render.com/store_db_iqbp"
```

**Por que External:** Tu Mac esta fuera de Render, necesita la URL publica

---

### PASO 3: Hacer Migraciones (OPCIONAL)

**Cuando:** Si quieres copiar datos de tu BD local a Render
**Donde:** Terminal de tu Mac
**Usar:** EXTERNAL URL

```bash
# Exportar desde tu BD local
pg_dump -U docker_admin -h localhost -p 5433 -d app_main -n public --data-only > datos_local.sql

# Importar a Render usando External URL
psql "postgresql://store_db_iqbp_user:eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI@dpg-d4ud120gjchc73c6mr6g-a.oregon-postgres.render.com/store_db_iqbp" < datos_local.sql
```

---

## RESUMEN RAPIDO

| Situacion | URL a Usar | Desde Donde |
|-----------|------------|-------------|
| Variable DATABASE_URL en Render | Internal | Render Dashboard |
| Conectar con psql desde tu Mac | External | Terminal Mac |
| Migrar datos desde tu Mac | External | Terminal Mac |
| Backend Spring Boot en Render | Internal | Automatico |

---

## PREGUNTAS FRECUENTES

### ¿Desde Docker?
**NO.** Ni Internal ni External se usan desde Docker.
- Docker es solo para tu BD local (localhost:5433)
- Las URLs de Render son para la BD en la nube

### ¿Cual pongo en application.properties local?
**NINGUNA.** En tu application.properties local usas:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5433/app_main
```
Las URLs de Render son SOLO para produccion.

### ¿Si no funciona la Internal en Render?
Intenta con la External (aunque sea mas lenta). Render deberia aceptar ambas.

### ¿Puedo usar la External en el backend de Render?
Si, funciona, pero es menos eficiente. Render recomienda usar Internal.

---

## VERIFICACION

### Probar conexion desde tu Mac:
```bash
psql "postgresql://store_db_iqbp_user:eQlqf4sZ3qN1bd0CEzJeeU5DMKDHPubI@dpg-d4ud120gjchc73c6mr6g-a.oregon-postgres.render.com/store_db_iqbp"

# Si conecta, veras:
# psql (15.x, server 15.x)
# SSL connection (protocol: TLSv1.3, cipher: TLS_AES_256_GCM_SHA384, bits: 256, compression: off)
# Type "help" for help.
# 
# store_db_iqbp=>

# Comandos utiles:
\l          # Listar bases de datos
\dn         # Listar schemas
\dt         # Listar tablas
\q          # Salir
```

---

## SIGUIENTE PASO

Una vez entiendas esto, continua con:
1. GUIA_DESPLIEGUE.md - FASE 2: Backend en Render
2. Usa la INTERNAL URL en la variable DATABASE_URL
3. Continua con el resto del despliegue
