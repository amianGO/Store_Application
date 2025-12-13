-- =====================================================
-- TEMPLATE SCHEMA - Multi-Tenant SaaS
-- Este schema se clonará para cada nueva empresa
-- =====================================================
-- IMPORTANTE: Este archivo define la estructura base
-- que se replicará en cada schema de empresa (empresa_1, empresa_2, etc.)
-- =====================================================

-- Crear schema template (ejecutar una sola vez)
CREATE SCHEMA IF NOT EXISTS template_schema;

-- Establecer search_path
SET search_path TO template_schema;

-- =====================================================
-- TABLA: empleados
-- Empleados de cada empresa (aislados por schema)
-- =====================================================
CREATE TABLE IF NOT EXISTS empleados (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    documento VARCHAR(20) NOT NULL UNIQUE,
    usuario VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    telefono VARCHAR(15),
    email VARCHAR(100),
    cargo VARCHAR(50) NOT NULL,
    fecha_contratacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado_activo BOOLEAN DEFAULT true,
    rol VARCHAR(20) NOT NULL CHECK (rol IN ('ADMIN', 'GERENTE', 'VENDEDOR')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_empleados_usuario ON empleados(usuario);
CREATE INDEX idx_empleados_documento ON empleados(documento);
CREATE INDEX idx_empleados_rol ON empleados(rol);
CREATE INDEX idx_empleados_estado ON empleados(estado_activo);

-- =====================================================
-- TABLA: productos
-- Catálogo de productos de cada empresa
-- =====================================================
CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    codigo VARCHAR(50) UNIQUE,
    descripcion TEXT,
    categoria VARCHAR(50),
    precio_venta NUMERIC(10,2) NOT NULL,
    precio_compra NUMERIC(10,2),
    stock INTEGER DEFAULT 0,
    stock_minimo INTEGER DEFAULT 0,
    activo BOOLEAN DEFAULT true,
    imagen_url VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_productos_codigo ON productos(codigo);
CREATE INDEX idx_productos_categoria ON productos(categoria);
CREATE INDEX idx_productos_activo ON productos(activo);
CREATE INDEX idx_productos_stock ON productos(stock);

-- =====================================================
-- TABLA: clientes
-- Clientes de cada empresa
-- =====================================================
CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    ciudad VARCHAR(50),
    documento VARCHAR(50),
    tipo_documento VARCHAR(20),
    activo BOOLEAN DEFAULT true,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_clientes_email ON clientes(email);
CREATE INDEX idx_clientes_documento ON clientes(documento);
CREATE INDEX idx_clientes_activo ON clientes(activo);

-- =====================================================
-- TABLA: facturas
-- Facturas de ventas de cada empresa
-- =====================================================
CREATE TABLE IF NOT EXISTS facturas (
    id BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) UNIQUE NOT NULL,
    cliente_id BIGINT REFERENCES clientes(id),
    empleado_id BIGINT REFERENCES empleados(id),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    subtotal NUMERIC(10,2) NOT NULL,
    impuesto NUMERIC(10,2) DEFAULT 0,
    descuento NUMERIC(10,2) DEFAULT 0,
    total NUMERIC(10,2) NOT NULL,
    metodo_pago VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'COMPLETADA',
    notas TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_facturas_numero ON facturas(numero_factura);
CREATE INDEX idx_facturas_cliente ON facturas(cliente_id);
CREATE INDEX idx_facturas_empleado ON facturas(empleado_id);
CREATE INDEX idx_facturas_fecha ON facturas(fecha);
CREATE INDEX idx_facturas_estado ON facturas(estado);

-- =====================================================
-- TABLA: detalle_facturas
-- Líneas de detalle de cada factura
-- =====================================================
CREATE TABLE IF NOT EXISTS detalle_facturas (
    id BIGSERIAL PRIMARY KEY,
    factura_id BIGINT NOT NULL REFERENCES facturas(id) ON DELETE CASCADE,
    producto_id BIGINT,
    producto_codigo VARCHAR(50),
    producto_nombre VARCHAR(100) NOT NULL,
    producto_categoria VARCHAR(50),
    cantidad INTEGER NOT NULL,
    precio_unitario NUMERIC(10,2) NOT NULL,
    descuento NUMERIC(10,2) DEFAULT 0,
    subtotal NUMERIC(10,2) NOT NULL
);

CREATE INDEX idx_detalle_factura ON detalle_facturas(factura_id);
CREATE INDEX idx_detalle_producto ON detalle_facturas(producto_id);

-- =====================================================
-- TABLA: carrito_compras
-- Carrito temporal para empleados
-- =====================================================
CREATE TABLE IF NOT EXISTS carrito_compras (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT REFERENCES empleados(id) ON DELETE CASCADE,
    producto_id BIGINT REFERENCES productos(id) ON DELETE CASCADE,
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario NUMERIC(10,2) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(empleado_id, producto_id)
);

CREATE INDEX idx_carrito_empleado ON carrito_compras(empleado_id);
CREATE INDEX idx_carrito_producto ON carrito_compras(producto_id);

-- =====================================================
-- TABLA: cajas (opcional)
-- Control de caja para cada empresa
-- =====================================================
CREATE TABLE IF NOT EXISTS cajas (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    empleado_id BIGINT REFERENCES empleados(id),
    saldo_inicial NUMERIC(12,2) DEFAULT 0,
    saldo_actual NUMERIC(12,2) DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'ABIERTA',
    fecha_apertura TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_cajas_estado ON cajas(estado);
CREATE INDEX idx_cajas_empleado ON cajas(empleado_id);

-- =====================================================
-- DATOS INICIALES (opcional)
-- =====================================================
-- Puedes descomentar esto para crear un empleado admin por defecto
-- en cada nuevo schema de empresa

-- INSERT INTO empleados (nombre, apellido, documento, usuario, password, cargo, rol, estado_activo)
-- VALUES (
--     'Administrador',
--     'Sistema',
--     'ADMIN001',
--     'admin',
--     '$2a$10$YourBcryptHashHere',  -- Cambiar por hash real
--     'Administrador',
--     'ADMIN',
--     true
-- );

-- =====================================================
-- FIN DEL TEMPLATE SCHEMA
-- =====================================================
-- NOTA: Este script se ejecutará automáticamente cuando
-- se cree un nuevo schema de empresa mediante el servicio
-- SchemaManagementService.clonarEstructuraDesdeTemplate()
-- =====================================================
