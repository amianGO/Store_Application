-- Migración para actualizar la estructura de facturas
-- Eliminar restricciones de clave foránea y cambiar estructura

-- Primero, crear las nuevas columnas
ALTER TABLE facturas 
ADD COLUMN cliente_nombre VARCHAR(255),
ADD COLUMN empleado_nombre VARCHAR(255);

-- Llenar las nuevas columnas con datos existentes
UPDATE facturas 
SET cliente_nombre = (
    SELECT CONCAT(c.nombre, ' ', c.apellido) 
    FROM clientes c 
    WHERE c.id = facturas.cliente_id
);

UPDATE facturas 
SET empleado_nombre = (
    SELECT CONCAT(e.nombre, ' ', e.apellido) 
    FROM empleados e 
    WHERE e.id = facturas.empleado_id
);

-- Hacer las columnas NOT NULL después de llenarlas
ALTER TABLE facturas 
ALTER COLUMN cliente_nombre SET NOT NULL,
ALTER COLUMN empleado_nombre SET NOT NULL;

-- Eliminar las restricciones de clave foránea si existen
ALTER TABLE facturas 
DROP CONSTRAINT IF EXISTS fk_facturas_cliente,
DROP CONSTRAINT IF EXISTS fk_facturas_empleado;

-- También eliminar las restricciones generadas automáticamente por JPA
DO $$
BEGIN
    -- Buscar y eliminar todas las FK constraints relacionadas con cliente_id y empleado_id
    DECLARE
        constraint_rec RECORD;
    BEGIN
        FOR constraint_rec IN 
            SELECT constraint_name 
            FROM information_schema.table_constraints 
            WHERE table_name = 'facturas' 
            AND constraint_type = 'FOREIGN KEY'
        LOOP
            EXECUTE 'ALTER TABLE facturas DROP CONSTRAINT ' || constraint_rec.constraint_name || ' CASCADE';
        END LOOP;
    END;
END $$;

-- Cambiar el tipo de fecha_emision a TIMESTAMP
ALTER TABLE facturas 
ALTER COLUMN fecha_emision TYPE TIMESTAMP;

-- Verificar la estructura final
SELECT column_name, data_type, is_nullable 
FROM information_schema.columns 
WHERE table_name = 'facturas' 
ORDER BY ordinal_position;
