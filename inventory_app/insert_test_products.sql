-- Script para insertar productos de prueba
-- Ejecutar después de la migración para tener datos de prueba

-- Limpiar productos existentes si es necesario
-- DELETE FROM productos;

-- Insertar productos de prueba
INSERT INTO productos (codigo, nombre, descripcion, categoria, precio_compra, precio_venta, stock, estado_activo, fecha_registro, stock_minimo) VALUES
('PROD001', 'Laptop Dell Inspiron 15', 'Laptop Dell Inspiron 15, 8GB RAM, 256GB SSD', 'ELECTRONICA', 2500000.00, 3200000.00, 15, true, NOW(), 5),
('PROD002', 'Mouse Logitech MX Master', 'Mouse inalámbrico ergonómico', 'ELECTRONICA', 180000.00, 250000.00, 25, true, NOW(), 10),
('PROD003', 'Camiseta Polo Classic', 'Camiseta polo de algodón, talla M', 'ROPA', 45000.00, 75000.00, 30, true, NOW(), 15),
('PROD004', 'Zapatos Nike Air Max', 'Zapatos deportivos Nike Air Max, talla 42', 'CALZADO', 320000.00, 450000.00, 12, true, NOW(), 8),
('PROD005', 'Arroz Diana x 5kg', 'Arroz blanco premium de 5 kilogramos', 'ALIMENTOS', 8500.00, 12000.00, 50, true, NOW(), 20),
('PROD006', 'Coca Cola 2L', 'Bebida gaseosa Coca Cola de 2 litros', 'BEBIDAS', 4500.00, 6500.00, 0, false, NOW(), 10),
('PROD007', 'Detergente Ariel 1kg', 'Detergente en polvo para ropa', 'LIMPIEZA', 12000.00, 18000.00, 8, true, NOW(), 15),
('PROD008', 'Cuaderno Norma 100 hojas', 'Cuaderno universitario de 100 hojas rayado', 'PAPELERIA', 3500.00, 5500.00, 40, true, NOW(), 25),
('PROD009', 'Martillo Stanley', 'Martillo de acero con mango de madera', 'FERRETERIA', 25000.00, 38000.00, 18, true, NOW(), 5),
('PROD010', 'Almohada Memory Foam', 'Almohada ergonómica de memory foam', 'HOGAR', 85000.00, 125000.00, 22, true, NOW(), 10);

-- Verificar la inserción
SELECT COUNT(*) as total_productos FROM productos;
SELECT codigo, nombre, categoria, precio_venta, stock, estado_activo FROM productos ORDER BY codigo;
