package com.example.inventory_app.Config;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Filtro CORS simple que se ejecuta ANTES de Spring Security.
 * Maneja preflight OPTIONS directamente sin pasar por la cadena de seguridad.
 */
@Component
@Order(-200) // MUY ANTES de Spring Security y TenantFilter
public class SimpleCorsFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;
        
        String origin = request.getHeader("Origin");
        
        // Agregar headers CORS a TODAS las respuestas
        response.setHeader("Access-Control-Allow-Origin", origin != null ? origin : "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, PATCH, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        response.setHeader("Access-Control-Max-Age", "3600");
        
        // Si es OPTIONS, responder inmediatamente con 200 OK
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            System.out.println("✓ [SIMPLE-CORS-FILTER] OPTIONS request desde: " + origin);
            response.setStatus(HttpServletResponse.SC_OK);
            return; // NO continuar con la cadena de filtros
        }
        
        // Para otros métodos, continuar con la cadena
        chain.doFilter(req, res);
    }
}
