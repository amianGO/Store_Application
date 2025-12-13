-- =========================================
-- Script: Crear tabla carrito_compras en schemas existentes
-- Fecha: 2025-12-06
-- Propósito: Agregar tabla carrito_compras a empresas existentes
-- =========================================

-- PASO 1: Crear en template_schema (para nuevas empresas)
SET search_path TO template_schema;

CREATE TABLE IF NOT EXISTS carrito_compras (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empleado_id, producto_id)
);

CREATE INDEX IF NOT EXISTS idx_carrito_empleado ON carrito_compras(empleado_id);
CREATE INDEX IF NOT EXISTS idx_carrito_producto ON carrito_compras(producto_id);

-- PASO 2: Crear en empresas existentes

-- Para empresa_1
SET search_path TO empresa_1;

CREATE TABLE IF NOT EXISTS carrito_compras (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empleado_id, producto_id)
);

CREATE INDEX IF NOT EXISTS idx_carrito_empleado ON carrito_compras(empleado_id);
CREATE INDEX IF NOT EXISTS idx_carrito_producto ON carrito_compras(producto_id);

-- Para empresa_2
SET search_path TO empresa_2;

CREATE TABLE IF NOT EXISTS carrito_compras (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empleado_id, producto_id)
);

CREATE INDEX IF NOT EXISTS idx_carrito_empleado ON carrito_compras(empleado_id);
CREATE INDEX IF NOT EXISTS idx_carrito_producto ON carrito_compras(producto_id);

-- Para empresa_3
SET search_path TO empresa_3;

CREATE TABLE IF NOT EXISTS carrito_compras (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empleado_id, producto_id)
);

CREATE INDEX IF NOT EXISTS idx_carrito_empleado ON carrito_compras(empleado_id);
CREATE INDEX IF NOT EXISTS idx_carrito_producto ON carrito_compras(producto_id);

-- Para empresa_4
SET search_path TO empresa_4;

CREATE TABLE IF NOT EXISTS carrito_compras (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empleado_id, producto_id)
);

CREATE INDEX IF NOT EXISTS idx_carrito_empleado ON carrito_compras(empleado_id);
CREATE INDEX IF NOT EXISTS idx_carrito_producto ON carrito_compras(producto_id);

-- =========================================
-- SCRIPT DINÁMICO (ejecutar TODO de una vez)
-- =========================================

-- Este script crea la tabla en TODOS los schemas automáticamente
DO $$
DECLARE
    schema_rec RECORD;
BEGIN
    -- Primero en template_schema
    EXECUTE 'SET search_path TO template_schema';
    EXECUTE '
        CREATE TABLE IF NOT EXISTS carrito_compras (
            id BIGSERIAL PRIMARY KEY,
            empleado_id BIGINT NOT NULL,
            producto_id BIGINT NOT NULL,
            cantidad INTEGER NOT NULL DEFAULT 1,
            precio_unitario NUMERIC(10,2) NOT NULL,
            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
            UNIQUE(empleado_id, producto_id)
        )';
    EXECUTE 'CREATE INDEX IF NOT EXISTS idx_carrito_empleado ON carrito_compras(empleado_id)';
    EXECUTE 'CREATE INDEX IF NOT EXISTS idx_carrito_producto ON carrito_compras(producto_id)';
    RAISE NOTICE 'Tabla carrito_compras creada en: template_schema';
    
    -- Luego en todos los schemas de empresas
    FOR schema_rec IN 
        SELECT schema_name 
        FROM information_schema.schemata 
        WHERE schema_name LIKE 'empresa_%'
    LOOP
        EXECUTE format('SET search_path TO %I', schema_rec.schema_name);
        EXECUTE '
            CREATE TABLE IF NOT EXISTS carrito_compras (
                id BIGSERIAL PRIMARY KEY,
                empleado_id BIGINT NOT NULL,
                producto_id BIGINT NOT NULL,
                cantidad INTEGER NOT NULL DEFAULT 1,
                precio_unitario NUMERIC(10,2) NOT NULL,
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(empleado_id, producto_id)
            )';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_carrito_empleado ON carrito_compras(empleado_id)';
        EXECUTE 'CREATE INDEX IF NOT EXISTS idx_carrito_producto ON carrito_compras(producto_id)';
        RAISE NOTICE 'Tabla carrito_compras creada en: %', schema_rec.schema_name;
    END LOOP;
    
    RAISE NOTICE 'Proceso completado exitosamente';
END $$;

-- =========================================
-- VERIFICACIÓN
-- =========================================

-- Verificar template_schema
SET search_path TO template_schema;
\d carrito_compras

-- Verificar empresa_4
SET search_path TO empresa_4;
\d carrito_compras