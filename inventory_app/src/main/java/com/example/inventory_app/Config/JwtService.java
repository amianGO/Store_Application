package com.example.inventory_app.Config;

import com.example.inventory_app.Entities.Empresa;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Servicio para generación y validación de JWT (JSON Web Tokens).
 * 
 * Soporte Multi-Tenant:
 * - Genera tokens con información de la empresa (empresaId, tenantKey, schemaName)
 * - Extrae datos del tenant para configurar TenantContext
 * - Validación de tokens con expiración
 * 
 * Claims incluidos en el token:
 * - subject: email de la empresa
 * - empresaId: ID de la empresa
 * - tenantKey: Clave única del tenant
 * - schemaName: Nombre del schema PostgreSQL
 * - rol: Rol de la empresa (siempre "EMPRESA")
 * 
 * @author DamianG
 * @version 2.0 - Multi-Tenant
 * @since 2025-11-23
 */
@Service
public class JwtService {
    private static final String SECRET_KEY = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
    private static final long EXPIRATION_TIME = 1000 * 60 * 60 * 24; // 24 horas

    public String getSecretKey() {
        return SECRET_KEY;
    }

    /**
     * Extrae el email (username) del token.
     * 
     * @param token JWT token
     * @return Email de la empresa
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae el empresaId del token.
     * 
     * @param token JWT token
     * @return ID de la empresa
     */
    public Long extractEmpresaId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("empresaId", Long.class);
    }

    /**
     * Extrae el tenantKey del token.
     * 
     * @param token JWT token
     * @return Tenant Key de la empresa
     */
    public String extractTenantKey(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("tenantKey", String.class);
    }

    /**
     * Extrae el schemaName del token.
     * 
     * @param token JWT token
     * @return Nombre del schema PostgreSQL
     */
    public String extractSchemaName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("schemaName", String.class);
    }

    /**
     * Extrae el rol del token.
     * 
     * @param token JWT token
     * @return Rol de la empresa
     */
    public String extractRol(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("rol", String.class);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Genera un token JWT para una empresa con todos los claims necesarios.
     * 
     * @param empresaId ID de la empresa
     * @param email Email de la empresa
     * @param tenantKey Clave única del tenant
     * @param schemaName Nombre del schema PostgreSQL
     * @return Token JWT generado
     */
    public String generateTokenForEmpresa(Long empresaId, String email, String tenantKey, String schemaName) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("empresaId", empresaId);
        claims.put("tenantKey", tenantKey);
        claims.put("schemaName", schemaName);
        claims.put("rol", "EMPRESA");
        claims.put("tipo", "empresa_login");
        
        System.out.println("[JWT-SERVICE] Generando token para empresa: " + email);
        System.out.println("[JWT-SERVICE] TenantKey: " + tenantKey + ", Schema: " + schemaName);
        
        return createToken(claims, email);
    }

    /**
     * Genera token para un empleado autenticado (Multi-Tenant).
     * 
     * Incluye información del empleado y de la empresa (tenant) a la que pertenece.
     * 
     * @param empresaId ID de la empresa (tenant)
     * @param schemaName Nombre del schema PostgreSQL del tenant
     * @param tenantKey Tenant Key de la empresa
     * @param empleadoId ID del empleado
     * @param usuario Usuario del empleado
     * @param rol Rol del empleado (ADMIN, GERENTE, VENDEDOR)
     * @return Token JWT generado
     */
    public String generateTokenForEmpleado(
            Long empresaId, 
            String schemaName, 
            String tenantKey, 
            Long empleadoId, 
            String usuario, 
            String rol) {
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("empresaId", empresaId);
        claims.put("schemaName", schemaName);
        claims.put("tenantKey", tenantKey);
        claims.put("empleadoId", empleadoId);
        claims.put("rol", rol);
        claims.put("tipo", "empleado_login");
        
        System.out.println("[JWT-SERVICE] Generando token para empleado: " + usuario);
        System.out.println("[JWT-SERVICE] Empresa ID: " + empresaId + ", Schema: " + schemaName + ", Rol: " + rol);
        
        return createToken(claims, usuario);
    }

    /**
     * Genera token con username y rol (método legacy para empleados).
     * 
     * @param username Username del usuario
     * @param rol Rol del usuario
     * @return Token JWT generado
     */
    public String generateToken(String username, String rol) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("rol", rol);
        return createToken(claims, username);
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts
                .builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSignInKey(), Jwts.SIG.HS256) // Especificar explícitamente HS256
                .compact();
    }

    public Boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Valida un token JWT completo (usuario y expiración).
     * 
     * @param token JWT token a validar
     * @return true si el token es válido
     */
    public Boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            System.err.println("[JWT-SERVICE] Token inválido: " + e.getMessage());
            return false;
        }
    }

    /**
     * Extrae todos los claims del token para debugging.
     * 
     * @param token JWT token
     * @return Map con todos los claims
     */
    public Map<String, Object> extractAllClaimsAsMap(String token) {
        Claims claims = extractAllClaims(token);
        Map<String, Object> claimsMap = new HashMap<>();
        
        claimsMap.put("subject", claims.getSubject());
        claimsMap.put("empresaId", claims.get("empresaId"));
        claimsMap.put("tenantKey", claims.get("tenantKey"));
        claimsMap.put("schemaName", claims.get("schemaName"));
        claimsMap.put("rol", claims.get("rol"));
        claimsMap.put("issuedAt", claims.getIssuedAt());
        claimsMap.put("expiration", claims.getExpiration());
        
        return claimsMap;
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = java.util.Base64.getDecoder().decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
