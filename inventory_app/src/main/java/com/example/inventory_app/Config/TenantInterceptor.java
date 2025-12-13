package com.example.inventory_app.Config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor para configurar automáticamente el TenantContext desde el JWT.
 * 
 * Flujo:
 * 1. Extrae el token JWT del header "Authorization"
 * 2. Valida el token
 * 3. Extrae el schemaName del token
 * 4. Configura TenantContext.setCurrentTenant(schemaName)
 * 5. Limpia el TenantContext después del request (afterCompletion)
 * 
 * Endpoints Excluidos:
 * - /api/auth/registro - No requiere autenticación
 * - /api/auth/login - No requiere autenticación
 * - Otros endpoints públicos según necesidad
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Component
public class TenantInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtService jwtService;

    /**
     * Ejecutado ANTES de procesar el request.
     * Configura el TenantContext basándose en el JWT.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestUri = request.getRequestURI();
        
        System.out.println("[TENANT-INTERCEPTOR] Request URI: " + requestUri);

        // Endpoints públicos que NO requieren tenant (trabajan en schema public)
        if (isPublicEndpoint(requestUri)) {
            System.out.println("[TENANT-INTERCEPTOR] Endpoint público, usando schema PUBLIC");
            TenantContext.resetToDefault();
            return true;
        }

        // Extraer token JWT del header Authorization
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.err.println("[TENANT-INTERCEPTOR] No se encontró token JWT en el header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"UNAUTHORIZED\", \"mensaje\": \"Token JWT requerido\"}");
            return false;
        }

        // Extraer el token (remover "Bearer ")
        String token = authHeader.substring(7);

        try {
            // Validar token
            if (!jwtService.validateToken(token)) {
                System.err.println("[TENANT-INTERCEPTOR] Token inválido o expirado");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"INVALID_TOKEN\", \"mensaje\": \"Token inválido o expirado\"}");
                return false;
            }

            // Extraer schemaName del token
            String schemaName = jwtService.extractSchemaName(token);
            String empresaEmail = jwtService.extractUsername(token);
            Long empresaId = jwtService.extractEmpresaId(token);
            String tenantKey = jwtService.extractTenantKey(token);

            if (schemaName == null || schemaName.isEmpty()) {
                System.err.println("[TENANT-INTERCEPTOR] Schema name no encontrado en el token");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\": \"INVALID_TOKEN\", \"mensaje\": \"Schema name no encontrado en token\"}");
                return false;
            }

            // Configurar TenantContext
            TenantContext.setCurrentTenant(schemaName);
            
            // Agregar información del tenant al request (opcional, para uso en controllers)
            request.setAttribute("empresaId", empresaId);
            request.setAttribute("empresaEmail", empresaEmail);
            request.setAttribute("schemaName", schemaName);
            request.setAttribute("tenantKey", tenantKey);

            System.out.println("[TENANT-INTERCEPTOR] ✓ Tenant configurado: " + schemaName + " (Empresa: " + empresaEmail + ", TenantKey: " + tenantKey + ")");

            return true;

        } catch (Exception e) {
            System.err.println("[TENANT-INTERCEPTOR] Error al procesar token: " + e.getMessage());
            TenantContext.clear();
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"TOKEN_ERROR\", \"mensaje\": \"Error al procesar token\"}");
            return false;
        }
    }

    /**
     * Ejecutado DESPUÉS de completar el request.
     * Limpia el TenantContext para evitar contaminación entre requests.
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("[TENANT-INTERCEPTOR] Limpiando TenantContext");
        TenantContext.clear();
    }

    /**
     * Determina si un endpoint es público (no requiere autenticación previa con JWT).
     * 
     * IMPORTANTE:
     * - Endpoints de EMPRESA: Trabajan en schema PUBLIC (empresas, suscripciones)
     * - Endpoints de EMPLEADO login: El controller configura TenantContext basándose en tenantKey del body
     * - Endpoints de EMPLEADO register: DESHABILITADO, usar endpoint protegido
     * 
     * @param requestUri URI del request
     * @return true si es público (no requiere JWT en header)
     */
    private boolean isPublicEndpoint(String requestUri) {
        System.out.println("[TENANT-INTERCEPTOR] Evaluando si es público: " + requestUri);
        
        // Endpoints de autenticación de EMPLEADOS
        // NOTA: /api/auth/login configura TenantContext manualmente usando tenantKey del body
        // NOTA: /api/auth/register está DESHABILITADO, retorna 403
        if (requestUri.equals("/api/auth/login") || 
            requestUri.equals("/api/auth/register")) {
            System.out.println("[TENANT-INTERCEPTOR] ✓ Endpoint de autenticación de EMPLEADOS (sin JWT previo)");
            return true;
        }

        // Endpoints de autenticación de EMPRESAS (trabajan en schema PUBLIC)
        if (requestUri.equals("/api/auth/empresa/registro") ||
            requestUri.equals("/api/auth/empresa/login")) {
            System.out.println("[TENANT-INTERCEPTOR] ✓ Endpoint de autenticación de EMPRESAS");
            return true;
        }

        // Endpoints de verificación (trabajan en schema public)
        if (requestUri.contains("/verificar")) {
            System.out.println("[TENANT-INTERCEPTOR] ✓ Endpoint de verificación");
            return true;
        }

        // Productos públicos (consultas sin autenticación)
        if (requestUri.startsWith("/api/productos/publico/")) {
            System.out.println("[TENANT-INTERCEPTOR] ✓ Endpoint de productos públicos");
            return true;
        }

        // Endpoints de administración (trabajan en schema public)
        if (requestUri.startsWith("/api/admin/")) {
            System.out.println("[TENANT-INTERCEPTOR] ✓ Endpoint de administración");
            return true;
        }

        // Actuator endpoints (monitoreo)
        if (requestUri.startsWith("/actuator/")) {
            System.out.println("[TENANT-INTERCEPTOR] ✓ Actuator endpoint");
            return true;
        }

        System.out.println("[TENANT-INTERCEPTOR] ✗ NO es endpoint público");
        return false;
    }
}
