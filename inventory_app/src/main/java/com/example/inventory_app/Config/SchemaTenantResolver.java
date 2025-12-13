package com.example.inventory_app.Config;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.stereotype.Component;

/**
 * Resolvedor del Identificador de Tenant para Hibernate.
 * 
 * Esta clase implementa la interfaz de Hibernate que se encarga de identificar
 * qué schema de base de datos debe usarse para cada operación de persistencia.
 * 
 * FUNCIONAMIENTO:
 * - Hibernate llama a resolveCurrentTenantIdentifier() antes de cada operación DB
 * - Este método consulta TenantContext para obtener el schema actual
 * - Hibernate usa ese schema para ejecutar las consultas SQL
 * 
 * EJEMPLO DE FLUJO:
 * 1. Usuario de "empresa_abc" hace login
 * 2. TenantContext.setCurrentTenant("schema_empresa_abc")
 * 3. Usuario solicita sus productos: GET /api/productos
 * 4. Hibernate llama a resolveCurrentTenantIdentifier() → retorna "schema_empresa_abc"
 * 5. La query se ejecuta en: SELECT * FROM schema_empresa_abc.productos
 * 6. Solo ve los productos de su empresa (aislamiento de datos garantizado)
 * 
 * IMPORTANTE:
 * - Esta clase debe ser un @Component para que Spring la gestione
 * - Se registra automáticamente por la propiedad:
 *   spring.jpa.properties.hibernate.tenant_identifier_resolver
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Component
public class SchemaTenantResolver implements CurrentTenantIdentifierResolver<String> {

    /**
     * Resuelve el identificador del tenant (schema) actual.
     * 
     * Este método es llamado por Hibernate cada vez que necesita saber
     * en qué schema debe ejecutar una operación de base de datos.
     * 
     * @return El identificador del schema actual obtenido de TenantContext
     */
    @Override
    public String resolveCurrentTenantIdentifier() {
        String tenant = TenantContext.getCurrentTenant();
        
        // Log detallado para debugging
        System.out.println("╔════════════════════════════════════════════════════════════════════════");
        System.out.println("║ [TENANT-RESOLVER] resolveCurrentTenantIdentifier() LLAMADO");
        System.out.println("║ Thread: " + Thread.currentThread().getName());
        System.out.println("║ ThreadId: " + Thread.currentThread().getId());
        System.out.println("║ TenantContext.getCurrentTenant(): " + tenant);
        System.out.println("║ Schema que Hibernate usará: " + tenant);
        System.out.println("╚════════════════════════════════════════════════════════════════════════");
        
        return tenant;
    }

    /**
     * Indica si el tenant identifier resolver valida la existencia del tenant.
     * 
     * Si retorna true, Hibernate validará que el tenant existe antes de usarlo.
     * Si retorna false, Hibernate asume que el tenant es válido sin verificar.
     * 
     * En nuestro caso:
     * - Retornamos false porque nosotros manejamos la validación del schema
     * - La validación se hace en el interceptor/filter antes de establecer el tenant
     * - Evita overhead de validación en cada operación de Hibernate
     * 
     * @return false - No validamos en Hibernate, lo hacemos en nuestra lógica
     */
    @Override
    public boolean validateExistingCurrentSessions() {
        return false;
    }
}
