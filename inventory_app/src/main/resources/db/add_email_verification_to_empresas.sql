-- =========================================
-- Script: Agregar campos de verificación de email a empresas
-- Fecha: 2025-12-06
-- Propósito: Permitir verificación de email en el registro
-- =========================================

-- Cambiar al schema public (donde están las empresas)
SET search_path TO public;

-- Agregar campos de verificación
ALTER TABLE empresas 
ADD COLUMN IF NOT EXISTS email_verificado BOOLEAN DEFAULT false NOT NULL;

ALTER TABLE empresas 
ADD COLUMN IF NOT EXISTS token_verificacion VARCHAR(100);

ALTER TABLE empresas 
ADD COLUMN IF NOT EXISTS fecha_verificacion TIMESTAMP;

-- Crear índice para búsqueda rápida por token
CREATE INDEX IF NOT EXISTS idx_empresas_token_verificacion 
ON empresas(token_verificacion);

-- Verificar columnas agregadas
\d empresas

-- =========================================
-- NOTA: Las empresas existentes tendrán:
-- - email_verificado = false (por defecto)
-- - token_verificacion = NULL
-- - fecha_verificacion = NULL
-- 
-- Para empresas de prueba, puedes actualizarlas manualmente:
-- UPDATE empresas SET email_verificado = true WHERE id = 1;
-- =========================================