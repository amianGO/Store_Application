-- Migración para eliminar restricciones FK de detalle_facturas
-- Eliminar todas las restricciones de clave foránea

-- Primero, verificar qué constraints existen
SELECT conname, contype
FROM pg_constraint 
WHERE conrelid = 'detalle_facturas'::regclass;

-- Eliminar la restricción FK específica que causa el problema
DO $$
BEGIN
    -- Buscar y eliminar todas las FK constraints en detalle_facturas
    DECLARE
        constraint_rec RECORD;
    BEGIN
        FOR constraint_rec IN 
            SELECT constraint_name 
            FROM information_schema.table_constraints 
            WHERE table_name = 'detalle_facturas' 
            AND constraint_type = 'FOREIGN KEY'
        LOOP
            EXECUTE 'ALTER TABLE detalle_facturas DROP CONSTRAINT IF EXISTS ' || constraint_rec.constraint_name || ' CASCADE';
        END LOOP;
    END;
END $$;

-- También eliminar constraint específica si existe
ALTER TABLE detalle_facturas DROP CONSTRAINT IF EXISTS fk2ju3oy13vxkxeqsqaoajgflpl CASCADE;

-- Verificar que se eliminaron todas las FK
SELECT conname, contype
FROM pg_constraint 
WHERE conrelid = 'detalle_facturas'::regclass
AND contype = 'f';

-- Mostrar resultado final
\d detalle_facturas;
