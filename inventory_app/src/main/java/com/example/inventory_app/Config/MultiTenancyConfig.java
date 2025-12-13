package com.example.inventory_app.Config;

import org.hibernate.engine.jdbc.connections.spi.MultiTenantConnectionProvider;
import org.hibernate.context.spi.CurrentTenantIdentifierResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Map;

/**
 * Configuración de Multi-Tenancy para Hibernate.
 * 
 * Esta clase es CRÍTICA para el correcto funcionamiento del multi-tenancy.
 * Resuelve el problema de inyección del DataSource en el MultiTenantConnectionProvider.
 * 
 * PROBLEMA QUE RESUELVE:
 * - Hibernate crea el MultiTenantConnectionProvider ANTES que Spring inyecte dependencias
 * - Resultado: DataSource es null → NullPointerException
 * - Solución: Crear el provider manualmente y pasárselo a Hibernate como bean
 * 
 * FLUJO DE CONFIGURACIÓN:
 * 1. Spring crea el DataSource del application.properties
 * 2. Este @Configuration se ejecuta
 * 3. Creamos el SchemaMultiTenantConnectionProvider con el DataSource inyectado
 * 4. Registramos el provider en Hibernate mediante HibernatePropertiesCustomizer
 * 5. Hibernate usa nuestro provider ya configurado
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Configuration
public class MultiTenancyConfig {

    /**
     * DataSource principal de la aplicación.
     * Spring lo crea automáticamente desde application.properties:
     * - spring.datasource.url
     * - spring.datasource.username
     * - spring.datasource.password
     */
    @Autowired
    private DataSource dataSource;

    /**
     * Crea el MultiTenantConnectionProvider con el DataSource correctamente inyectado.
     * 
     * Este bean:
     * 1. Recibe el DataSource de Spring (ya configurado y listo)
     * 2. Crea una instancia de SchemaMultiTenantConnectionProvider
     * 3. Le pasa el DataSource mediante constructor
     * 4. Hibernate lo usará para obtener conexiones multi-tenant
     * 
     * @return Provider de conexiones multi-tenant configurado
     */
    @Bean
    public MultiTenantConnectionProvider<String> multiTenantConnectionProvider() {
        return new SchemaMultiTenantConnectionProvider(dataSource);
    }

    /**
     * Crea el CurrentTenantIdentifierResolver.
     * 
     * @return Resolver de tenant actual
     */
    @Bean
    public CurrentTenantIdentifierResolver<String> currentTenantIdentifierResolver() {
        return new SchemaTenantResolver();
    }

    /**
     * Customiza las propiedades de Hibernate para registrar nuestros beans.
     * 
     * HibernatePropertiesCustomizer permite modificar la configuración de Hibernate
     * en tiempo de ejecución, DESPUÉS de que Spring haya creado todos los beans.
     * 
     * Aquí registramos:
     * - El MultiTenantConnectionProvider (con DataSource ya inyectado)
     * - Cualquier otra propiedad custom que necesitemos
     * 
     * IMPORTANTE: Esto sobrescribe las propiedades del application.properties
     * para usar nuestros beans en lugar de que Hibernate los cree.
     * 
     * @return Customizer de propiedades de Hibernate
     */
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return (Map<String, Object> hibernateProperties) -> {
            
            System.out.println("[MULTI-TENANCY] ===================================");
            System.out.println("[MULTI-TENANCY] Configurando Multi-Tenancy...");
            
            // 1. Activar multi-tenancy por SCHEMA
            hibernateProperties.put("hibernate.multi_tenancy", "SCHEMA");
            System.out.println("[MULTI-TENANCY] - Mode: SCHEMA");
            
            // 2. Registrar el MultiTenantConnectionProvider
            hibernateProperties.put(
                "hibernate.multi_tenant_connection_provider",
                multiTenantConnectionProvider()
            );
            System.out.println("[MULTI-TENANCY] - Connection Provider: SchemaMultiTenantConnectionProvider");
            
            // 3. Registrar el CurrentTenantIdentifierResolver
            hibernateProperties.put(
                "hibernate.tenant_identifier_resolver",
                currentTenantIdentifierResolver()
            );
            System.out.println("[MULTI-TENANCY] - Tenant Resolver: SchemaTenantResolver");
            
            System.out.println("[MULTI-TENANCY] ===================================");
        };
    }
}
