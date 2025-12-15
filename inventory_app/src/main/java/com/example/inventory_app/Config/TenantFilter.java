package com.example.inventory_app.Config;

import com.example.inventory_app.Entities.Empresa;
import com.example.inventory_app.Repositories.EmpresaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Filtro para configurar el contexto del Tenant (Multi-Tenancy).
 * 
 * ORDEN DE EJECUCIÓN:
 * - Se ejecuta DESPUÉS de Spring Security (order = 2)
 * - Pero ANTES de que llegue al Controller
 * - Esto garantiza que el TenantContext esté listo cuando Hibernate se active
 * 
 * FUNCIONAMIENTO:
 * 1. Extrae el JWT del header Authorization
 * 2. Extrae los claims: schemaName, empresaId, empresaEmail, tenantKey
 * 3. Establece TenantContext.setCurrentTenant(schemaName)
 * 4. Agrega los datos como request attributes para los controllers
 * 5. Al finalizar, limpia el TenantContext (finally)
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-27
 */
@Component
@Order(2) // Después de Spring Security (order 1)
public class TenantFilter extends OncePerRequestFilter {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmpresaRepository empresaRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Endpoints públicos que NO requieren JWT pero operan en schema 'public'.
     */
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/auth/register",
        "/api/auth/empresa/registro",
        "/api/auth/empresa/login",
        "/api/auth/empresa/",
        "/api/productos/publico"
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // PERMITIR TODAS LAS PETICIONES OPTIONS (preflight CORS)
        if ("OPTIONS".equalsIgnoreCase(method)) {
            System.out.println("▓ [TENANT-FILTER] ✓ OPTIONS request - permitiendo preflight CORS");
            filterChain.doFilter(request, response);
            return;
        }
        
        System.out.println("\n▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓");
        System.out.println("▓ [TENANT-FILTER] Request URI: " + requestURI);
        
        try {
            // CASO ESPECIAL: Login de empleado - necesita leer el body
            if ("/api/auth/login".equals(requestURI) && "POST".equalsIgnoreCase(method)) {
                System.out.println("▓ [TENANT-FILTER] ⚡ Login de empleado - extrayendo tenantKey del body");
                
                // Envolver request para poder leer el body múltiples veces
                CachedBodyHttpServletRequest wrappedRequest = new CachedBodyHttpServletRequest(request);
                handleEmpleadoLogin(wrappedRequest);
                
                System.out.println("▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓\n");
                
                // IMPORTANTE: Pasar el wrapped request al resto de la cadena
                filterChain.doFilter(wrappedRequest, response);
                return; // Salir después de procesar
            }
            // Endpoints públicos (schema public)
            else if (requestURI.matches(".*/api/auth/empresa/registro") ||
                     requestURI.matches(".*/api/auth/empresa/login") ||
                     requestURI.matches(".*/api/auth/empresa/[0-9]+/verificar") ||
                     requestURI.matches(".*/api/auth/empresa/[0-9]+/tiene-empleados") ||
                     requestURI.matches(".*/api/auth/verificar-email.*") ||
                     requestURI.matches(".*/api/auth/reenviar-verificacion") ||
                     requestURI.matches(".*/api/suscripciones/planes.*") ||
                     requestURI.matches(".*/api/auth/login") ||  // Login de empleados
                     requestURI.matches(".*/api/productos/publico/.*")) {
                System.out.println("▓ [TENANT-FILTER] ✓ Endpoint público - usando schema 'public'");
                TenantContext.resetToDefault();
            }
            // Endpoints protegidos con JWT
            else {
                handleJwtRequest(request);
            }
            
            System.out.println("▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓▓\n");
            
            // Continuar con la cadena de filtros
            filterChain.doFilter(request, response);
            
        } finally {
            // *** CRÍTICO: Limpiar el TenantContext al finalizar el request ***
            System.out.println("[TENANT-FILTER] 🧹 Limpiando TenantContext");
            TenantContext.clear();
        }
    }

    /**
     * Maneja el login de empleado extrayendo el tenantKey del body.
     * Busca la empresa y establece el schema correcto.
     */
    private void handleEmpleadoLogin(CachedBodyHttpServletRequest request) throws IOException {
        try {
            // Obtener el body cacheado
            byte[] bodyBytes = request.getCachedBody();
            String body = new String(bodyBytes, request.getCharacterEncoding() != null ? request.getCharacterEncoding() : "UTF-8");
            
            if (body != null && !body.isEmpty()) {
                JsonNode jsonNode = objectMapper.readTree(body);
                
                // Verificar que el campo tenantKey existe
                if (jsonNode.has("tenantKey")) {
                    String tenantKey = jsonNode.get("tenantKey").asText();
                    
                    System.out.println("▓ [TENANT-FILTER] TenantKey extraído: " + tenantKey);
                    
                    // Buscar empresa por tenantKey (en schema public)
                    TenantContext.resetToDefault();
                    Optional<Empresa> empresaOpt = empresaRepository.findByTenantKey(tenantKey);
                    
                    if (empresaOpt.isPresent()) {
                        Empresa empresa = empresaOpt.get();
                        String schemaName = empresa.getSchemaName();
                        
                        System.out.println("▓ [TENANT-FILTER] Empresa encontrada: " + empresa.getNombre());
                        System.out.println("▓ [TENANT-FILTER] ✓✓✓ Configurando schema: " + schemaName);
                        
                        // Establecer el schema del tenant
                        TenantContext.setCurrentTenant(schemaName);
                        
                        // Agregar datos como request attributes
                        request.setAttribute("schemaName", schemaName);
                        request.setAttribute("empresaId", empresa.getId());
                        request.setAttribute("tenantKey", tenantKey);
                    } else {
                        System.out.println("▓ [TENANT-FILTER] ✗ Empresa no encontrada - usando schema 'public'");
                        TenantContext.resetToDefault();
                    }
                } else {
                    System.out.println("▓ [TENANT-FILTER] ⚠ Campo 'tenantKey' no encontrado - usando schema 'public'");
                    TenantContext.resetToDefault();
                }
            } else {
                System.out.println("▓ [TENANT-FILTER] ⚠ Body vacío - usando schema 'public'");
                TenantContext.resetToDefault();
            }
        } catch (Exception e) {
            System.err.println("▓ [TENANT-FILTER] ✗ Error al procesar login: " + e.getMessage());
            e.printStackTrace();
            TenantContext.resetToDefault();
        }
    }

    /**
     * Maneja requests con JWT extrayendo el schema del token.
     */
    private void handleJwtRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            
            try {
                // Extraer claims usando métodos públicos de JwtService
                String schemaName = jwtService.extractClaim(token, claims -> claims.get("schemaName", String.class));
                Long empresaId = jwtService.extractClaim(token, claims -> claims.get("empresaId", Long.class));
                Long empleadoId = jwtService.extractClaim(token, claims -> claims.get("empleadoId", Long.class));
                String tipoUsuario = jwtService.extractClaim(token, claims -> claims.get("tipoUsuario", String.class));
                
                System.out.println("╔════════════════════════════════════════════════════════════════════════");
                System.out.println("║ [TENANT-FILTER] JWT Claims Extraídos:");
                System.out.println("║ - schemaName: " + schemaName);
                System.out.println("║ - empresaId: " + empresaId);
                System.out.println("║ - empleadoId: " + empleadoId);
                System.out.println("║ - tipoUsuario: " + tipoUsuario);
                System.out.println("╚════════════════════════════════════════════════════════════════════════");
                
                if (schemaName != null && !schemaName.equals("public")) {
                    TenantContext.setCurrentTenant(schemaName);
                    
                    // Agregar información al request
                    request.setAttribute("schemaName", schemaName);
                    request.setAttribute("empresaId", empresaId);
                    request.setAttribute("empleadoId", empleadoId);
                    request.setAttribute("tipoUsuario", tipoUsuario);
                }
            } catch (Exception e) {
                System.out.println("▓ [TENANT-FILTER] ✗ Error al extraer claims del JWT: " + e.getMessage());
                TenantContext.resetToDefault();
            }
        } else {
            System.out.println("▓ [TENANT-FILTER] ⚠ No hay JWT - usando schema 'public'");
            TenantContext.resetToDefault();
        }
    }
}
