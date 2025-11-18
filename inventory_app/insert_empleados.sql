-- Insertar empleados de prueba para el sistema de inventario
-- Estos empleados son necesarios para el funcionamiento del sistema de ventas

INSERT INTO empleados (nombre, apellido, documento, usuario, password, telefono, email, cargo, fecha_contratacion, estado_activo, rol) 
VALUES 
    ('Juan', 'Pérez', '12345678', 'juan.perez', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Ci6d8cHBV0s9LVfrhANlM/wlSjFHGC', '3001234567', 'juan.perez@tienda.com', 'VENDEDOR', CURRENT_DATE, true, 'VENDEDOR'),
    ('María', 'González', '87654321', 'maria.gonzalez', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Ci6d8cHBV0s9LVfrhANlM/wlSjFHGC', '3007654321', 'maria.gonzalez@tienda.com', 'GERENTE', CURRENT_DATE, true, 'ADMIN'),
    ('Carlos', 'Rodríguez', '11223344', 'carlos.rodriguez', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Ci6d8cHBV0s9LVfrhANlM/wlSjFHGC', '3009876543', 'carlos.rodriguez@tienda.com', 'CAJERO', CURRENT_DATE, true, 'CAJERO'),
    ('Ana', 'Martínez', '44332211', 'ana.martinez', '$2a$10$N9qo8uLOickgx2ZMRZoMye/Ci6d8cHBV0s9LVfrhANlM/wlSjFHGC', '3005432167', 'ana.martinez@tienda.com', 'ADMINISTRADOR', CURRENT_DATE, true, 'ADMIN')
ON CONFLICT (documento) DO NOTHING;

-- Mensaje de confirmación
SELECT 'Empleados de prueba insertados correctamente' as mensaje;
