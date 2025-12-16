-- ============================================
-- CREAR TEMPLATE_SCHEMA PARA MULTI-TENANCY
-- Script basado en las entidades JPA reales del proyecto
-- ============================================

-- 1. Crear el schema template
CREATE SCHEMA IF NOT EXISTS template_schema;

-- 2. Establecer el schema para las siguientes operaciones
SET search_path TO template_schema;

-- ============================================
-- TABLA: productos
-- ============================================
CREATE TABLE IF NOT EXISTS productos (
    id BIGSERIAL PRIMARY KEY,
    codigo VARCHAR(50) NOT NULL UNIQUE,
    nombre VARCHAR(200) NOT NULL,
    descripcion TEXT,
    precio_compra DECIMAL(10, 2) NOT NULL CHECK (precio_compra > 0),
    precio_venta DECIMAL(10, 2) NOT NULL CHECK (precio_venta > 0),
    stock INTEGER NOT NULL CHECK (stock >= 0),
    stock_minimo INTEGER NOT NULL CHECK (stock_minimo >= 0),
    categoria VARCHAR(50) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT true
);

-- ============================================
-- TABLA: clientes
-- ============================================
CREATE TABLE IF NOT EXISTS clientes (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    documento VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    direccion VARCHAR(200),
    ciudad VARCHAR(100),
    pais VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT true
);

-- ============================================
-- TABLA: empleados
-- ============================================
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
    fecha_contratacion DATE,
    estado_activo BOOLEAN NOT NULL DEFAULT true,
    rol VARCHAR(20) NOT NULL
);

-- ============================================
-- TABLA: facturas
-- ============================================
CREATE TABLE IF NOT EXISTS facturas (
    id BIGSERIAL PRIMARY KEY,
    numero_factura VARCHAR(50) NOT NULL UNIQUE,
    cliente_id BIGINT NOT NULL,
    empleado_id BIGINT NOT NULL,
    fecha TIMESTAMP NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (subtotal >= 0),
    impuesto DECIMAL(10, 2) DEFAULT 0 CHECK (impuesto >= 0),
    descuento DECIMAL(10, 2) DEFAULT 0 CHECK (descuento >= 0),
    total DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (total >= 0),
    metodo_pago VARCHAR(50),
    estado VARCHAR(20) DEFAULT 'COMPLETADA',
    notas TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,
    CONSTRAINT fk_factura_cliente FOREIGN KEY (cliente_id) REFERENCES clientes(id),
    CONSTRAINT fk_factura_empleado FOREIGN KEY (empleado_id) REFERENCES empleados(id)
);

-- ============================================
-- TABLA: detalle_facturas
-- ============================================
CREATE TABLE IF NOT EXISTS detalle_facturas (
    id BIGSERIAL PRIMARY KEY,
    factura_id BIGINT NOT NULL,
    producto_id BIGINT,
    producto_codigo VARCHAR(50),
    producto_nombre VARCHAR(100) NOT NULL,
    producto_categoria VARCHAR(50),
    cantidad INTEGER NOT NULL CHECK (cantidad >= 1),
    precio_unitario DECIMAL(10, 2) NOT NULL CHECK (precio_unitario > 0),
    descuento DECIMAL(10, 2) DEFAULT 0 CHECK (descuento >= 0),
    subtotal DECIMAL(10, 2) NOT NULL DEFAULT 0,
    CONSTRAINT fk_detalle_factura FOREIGN KEY (factura_id) REFERENCES facturas(id) ON DELETE CASCADE
);

-- ============================================
-- TABLA: carrito_compras
-- ============================================
CREATE TABLE IF NOT EXISTS carrito_compras (
    id BIGSERIAL PRIMARY KEY,
    empleado_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    cantidad INTEGER NOT NULL DEFAULT 1 CHECK (cantidad >= 1),
    precio_unitario DECIMAL(10, 2) NOT NULL CHECK (precio_unitario > 0),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_carrito_empleado_producto UNIQUE (empleado_id, producto_id)
);

-- ============================================
-- TABLA: detalle_carritos
-- ============================================
CREATE TABLE IF NOT EXISTS detalle_carritos (
    id BIGSERIAL PRIMARY KEY,
    carrito_id BIGINT NOT NULL,
    producto_codigo VARCHAR(50) NOT NULL,
    producto_nombre VARCHAR(100) NOT NULL,
    producto_categoria VARCHAR(50),
    cantidad INTEGER NOT NULL,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    subtotal DECIMAL(10, 2) NOT NULL,
    CONSTRAINT fk_detalle_carrito FOREIGN KEY (carrito_id) REFERENCES carrito_compras(id) ON DELETE CASCADE
);

-- ============================================
-- TABLA: cajas
-- ============================================
CREATE TABLE IF NOT EXISTS cajas (
    id BIGSERIAL PRIMARY KEY,
    numero_caja VARCHAR(20) NOT NULL UNIQUE,
    empleado_id BIGINT NOT NULL,
    fecha_apertura TIMESTAMP,
    fecha_cierre TIMESTAMP,
    monto_inicial DECIMAL(10, 2) NOT NULL,
    monto_final DECIMAL(10, 2),
    total_ventas DECIMAL(10, 2),
    estado VARCHAR(20),
    observaciones VARCHAR(500),
    CONSTRAINT fk_caja_empleado FOREIGN KEY (empleado_id) REFERENCES empleados(id)
);

-- ============================================
-- ÍNDICES PARA MEJORAR RENDIMIENTO
-- ============================================

-- Productos
CREATE INDEX idx_productos_codigo ON productos(codigo);
CREATE INDEX idx_productos_categoria ON productos(categoria);
CREATE INDEX idx_productos_activo ON productos(activo);
CREATE INDEX idx_productos_stock ON productos(stock);

-- Clientes
CREATE INDEX idx_clientes_documento ON clientes(documento);
CREATE INDEX idx_clientes_email ON clientes(email);
CREATE INDEX idx_clientes_activo ON clientes(activo);

-- Empleados
CREATE INDEX idx_empleados_documento ON empleados(documento);
CREATE INDEX idx_empleados_usuario ON empleados(usuario);
CREATE INDEX idx_empleados_email ON empleados(email);
CREATE INDEX idx_empleados_activo ON empleados(estado_activo);

-- Facturas
CREATE INDEX idx_facturas_numero ON facturas(numero_factura);
CREATE INDEX idx_facturas_cliente ON facturas(cliente_id);
CREATE INDEX idx_facturas_empleado ON facturas(empleado_id);
CREATE INDEX idx_facturas_fecha ON facturas(fecha);
CREATE INDEX idx_facturas_estado ON facturas(estado);

-- Detalle Facturas
CREATE INDEX idx_detalle_facturas_factura ON detalle_facturas(factura_id);
CREATE INDEX idx_detalle_facturas_producto ON detalle_facturas(producto_id);

-- Carrito Compras
CREATE INDEX idx_carrito_empleado ON carrito_compras(empleado_id);
CREATE INDEX idx_carrito_producto ON carrito_compras(producto_id);

-- Cajas
CREATE INDEX idx_cajas_empleado ON cajas(empleado_id);
CREATE INDEX idx_cajas_numero ON cajas(numero_caja);
CREATE INDEX idx_cajas_estado ON cajas(estado);

-- ============================================
-- RESTAURAR search_path
-- ============================================
SET search_path TO public;

-- ============================================
-- VERIFICACIÓN
-- ============================================
SELECT 
    'template_schema' as schema,
    COUNT(*) as total_tablas,
    string_agg(table_name, ', ' ORDER BY table_name) as tablas
FROM information_schema.tables 
WHERE table_schema = 'template_schema';

SELECT 
    'Se creó template_schema exitosamente' as mensaje,
    'Ahora las nuevas empresas clonarán esta estructura automáticamente' as siguiente_paso;
