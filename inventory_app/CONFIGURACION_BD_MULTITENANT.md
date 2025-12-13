# 🔧 Configuración Inicial de Base de Datos Multi-Tenant

## 📋 Resumen del Problema

Al crear nuevas empresas, el sistema crea schemas vacíos (`empresa_1`, `empresa_2`, etc.) porque **NO existía el `template_schema`** del cual clonar la estructura.

### ¿Qué es template_schema?

Es un schema especial que contiene la **estructura de tablas base** (sin datos) que se clona automáticamente cada vez que se registra una nueva empresa.

```
template_schema/
├── empleados
├── productos
├── clientes
├── facturas
├── detalle_facturas
├── carrito_compras
└── cajas
```

---

## ✅ Solución Aplicada

### 1. Crear template_schema (UNA SOLA VEZ)

Ejecutar el archivo SQL que contiene la estructura completa:

```bash
cd /Users/gaviria/Documents/dev/Proyectos_Spring/Tienda/Store_Application/inventory_app

psql -h localhost -p 5433 -U docker_admin -d app_main \
  -f src/main/resources/db/template_schema.sql
```

**Resultado:**
- ✅ Schema `template_schema` creado
- ✅ 7 tablas creadas con estructura completa
- ✅ Índices y constraints configurados

---

### 2. Clonar estructura a schemas existentes

Si ya tienes schemas de empresas creados ANTES de crear el template, debes clonarlos manualmente:

```sql
-- Conectar a la base de datos
psql -h localhost -p 5433 -U docker_admin -d app_main

-- Clonar a empresa_1
CREATE TABLE empresa_1.empleados (LIKE template_schema.empleados INCLUDING ALL);
CREATE TABLE empresa_1.productos (LIKE template_schema.productos INCLUDING ALL);
CREATE TABLE empresa_1.clientes (LIKE template_schema.clientes INCLUDING ALL);
CREATE TABLE empresa_1.facturas (LIKE template_schema.facturas INCLUDING ALL);
CREATE TABLE empresa_1.detalle_facturas (LIKE template_schema.detalle_facturas INCLUDING ALL);
CREATE TABLE empresa_1.carrito_compras (LIKE template_schema.carrito_compras INCLUDING ALL);
CREATE TABLE empresa_1.cajas (LIKE template_schema.cajas INCLUDING ALL);

-- Repetir para empresa_2, empresa_3, etc.
```

**O usar el script completo:**

```bash
psql -h localhost -p 5433 -U docker_admin -d app_main << 'EOF'
CREATE TABLE IF NOT EXISTS empresa_1.empleados (LIKE template_schema.empleados INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_1.productos (LIKE template_schema.productos INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_1.clientes (LIKE template_schema.clientes INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_1.facturas (LIKE template_schema.facturas INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_1.detalle_facturas (LIKE template_schema.detalle_facturas INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_1.carrito_compras (LIKE template_schema.carrito_compras INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_1.cajas (LIKE template_schema.cajas INCLUDING ALL);

CREATE TABLE IF NOT EXISTS empresa_2.empleados (LIKE template_schema.empleados INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_2.productos (LIKE template_schema.productos INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_2.clientes (LIKE template_schema.clientes INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_2.facturas (LIKE template_schema.facturas INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_2.detalle_facturas (LIKE template_schema.detalle_facturas INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_2.carrito_compras (LIKE template_schema.carrito_compras INCLUDING ALL);
CREATE TABLE IF NOT EXISTS empresa_2.cajas (LIKE template_schema.cajas INCLUDING ALL);

SELECT 'Tablas clonadas exitosamente' AS resultado;
EOF
```

---

## 🚀 Flujo Automático (Nuevas Empresas)

Una vez que `template_schema` existe, **las nuevas empresas automáticamente obtendrán las tablas**:

### Código en `SchemaManagementService.java`

```java
public void crearSchemaParaTenant(String schemaName) {
    // 1. Crear schema
    CREATE SCHEMA empresa_N;
    
    // 2. Si template_schema existe, clonar estructura
    if (schemaExiste("template_schema")) {
        clonarEstructuraDesdeTemplate(schemaName);
    }
}

private void clonarEstructuraDesdeTemplate(String targetSchema) {
    String[] tablas = {
        "productos", "clientes", "empleados", "facturas",
        "detalle_facturas", "carrito_compras", "cajas"
    };
    
    for (String tabla : tablas) {
        CREATE TABLE targetSchema.tabla 
        (LIKE template_schema.tabla INCLUDING ALL);
    }
}
```

---

## 📊 Verificación

### Listar todos los schemas
```sql
SELECT schema_name 
FROM information_schema.schemata 
WHERE schema_name LIKE 'empresa_%' OR schema_name = 'template_schema'
ORDER BY schema_name;
```

**Resultado esperado:**
```
 schema_name    
----------------
 empresa_1
 empresa_2
 template_schema
```

---

### Ver tablas de un schema
```sql
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'empresa_1' 
ORDER BY table_name;
```

**Resultado esperado:**
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

---

### Verificar estructura de tabla empleados
```sql
SET search_path TO empresa_1;

\d empleados
```

**O usando SQL:**
```sql
SELECT column_name, data_type, character_maximum_length, is_nullable
FROM information_schema.columns
WHERE table_schema = 'empresa_1' AND table_name = 'empleados'
ORDER BY ordinal_position;
```

---

## 🔐 Probar Creación de Empleado

Ahora que las tablas existen, puedes crear empleados:

```bash
# 1. Login como empresa
curl -X POST http://localhost:8080/api/auth/empresa/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@techstore.com",
    "password": "Tech@2024Store"
  }'

# Guardar el token de la respuesta

# 2. Crear empleado
curl -X POST http://localhost:8080/api/empresas/empleados \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN_EMPRESA}" \
  -d '{
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
  }'
```

---

## 📁 Ubicación del Archivo

El archivo `template_schema.sql` está en:
```
inventory_app/src/main/resources/db/template_schema.sql
```

---

## ⚠️ IMPORTANTE: Ejecutar ANTES de Registrar Empresas

### Orden Correcto de Configuración

1. ✅ **Crear `template_schema`** (este documento)
2. ✅ Iniciar aplicación Spring Boot
3. ✅ Registrar empresas (automáticamente obtendrán las tablas)

### Si ya registraste empresas antes

1. ✅ Crear `template_schema` (ejecutar SQL)
2. ✅ Clonar manualmente a schemas existentes (script arriba)
3. ✅ Listo para usar

---

## 🎯 Resumen de Comandos Rápidos

```bash
# 1. Crear template_schema
psql -h localhost -p 5433 -U docker_admin -d app_main \
  -f src/main/resources/db/template_schema.sql

# 2. Verificar template creado
psql -h localhost -p 5433 -U docker_admin -d app_main \
  -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'template_schema';"

# 3. Ver schemas de empresas
psql -h localhost -p 5433 -U docker_admin -d app_main \
  -c "SELECT schema_name FROM information_schema.schemata WHERE schema_name LIKE 'empresa_%';"

# 4. Ver tablas de una empresa
psql -h localhost -p 5433 -U docker_admin -d app_main \
  -c "SELECT table_name FROM information_schema.tables WHERE table_schema = 'empresa_1';"
```

---

## 📌 Estado Actual (Post-Configuración)

- ✅ `template_schema` existe con 7 tablas
- ✅ `empresa_1` tiene las 7 tablas clonadas
- ✅ `empresa_2` tiene las 7 tablas clonadas
- ✅ Nuevas empresas automáticamente obtendrán las tablas
- ✅ Sistema multi-tenant completamente funcional

**🎉 ¡Ahora puedes crear empleados en cada empresa!**
