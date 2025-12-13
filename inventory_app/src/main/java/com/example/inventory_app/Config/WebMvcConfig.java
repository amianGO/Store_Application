package com.example.inventory_app.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuración de Interceptors para la aplicación.
 * 
 * Registra:
 * - TenantInterceptor: Configura automáticamente el TenantContext desde JWT
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TenantInterceptor tenantInterceptor;

    /**
     * Registra los interceptors de la aplicación.
     * 
     * NOTA: TenantInterceptor DESHABILITADO - ahora usamos TenantFilter
     * El Filter se ejecuta antes en la cadena, garantizando que el
     * TenantContext esté listo cuando Hibernate se active.
     * 
     * @deprecated Interceptor reemplazado por TenantFilter
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // DESHABILITADO - Ahora usamos TenantFilter
        /*
        registry.addInterceptor(tenantInterceptor)
                .addPathPatterns("/api/**")
                .order(1);
        */
        
        System.out.println("[WEB-CONFIG] ⚠ TenantInterceptor DESHABILITADO - usando TenantFilter");
    }
}
