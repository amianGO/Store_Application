-- =========================================
-- Script: Eliminar columna 'verificada' y ajustar empresas
-- Fecha: 2025-12-07
-- Propósito: Eliminar columna antigua 'verificada' que fue reemplazada por 'email_verificado'
-- =========================================

-- Cambiar al schema public
SET search_path TO public;

-- Eliminar la columna 'verificada' (antigua)
ALTER TABLE empresas 
DROP COLUMN IF EXISTS verificada;

-- Verificar estructura actualizada
\d empresas

-- =========================================
-- RESULTADO ESPERADO:
-- La tabla empresas ahora solo tiene:
-- - email_verificado (boolean)
-- - token_verificacion (varchar)
-- - fecha_verificacion (timestamp)
-- 
-- Y NO tiene:
-- - verificada (ELIMINADA)
-- =========================================