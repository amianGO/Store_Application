package com.example.inventory_app.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import io.jsonwebtoken.security.Keys;
import java.util.Base64;

import java.util.Arrays;



/**
 * Configuración de seguridad para la aplicación.
 * Define la configuración de Spring Security y los beans necesarios.
 *
 * @author DamianG
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true) // ✅ Habilitar @PreAuthorize
public class SecurityConfig {

    @Autowired
    private JwtService jwtService;

    /**
     * Configura el decodificador JWT con HS256
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        byte[] keyBytes = Base64.getDecoder().decode(jwtService.getSecretKey());
        javax.crypto.SecretKey secretKey = Keys.hmacShaKeyFor(keyBytes);
        
        return NimbusJwtDecoder
                .withSecretKey(secretKey)
                .macAlgorithm(org.springframework.security.oauth2.jose.jws.MacAlgorithm.HS256)
                .build();
    }

    /**
     * Convierte los claims del JWT en authorities de Spring Security
     */
    @Bean
    public org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter jwtAuthenticationConverter() {
        org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter converter = 
            new org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter();
        
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            java.util.List<org.springframework.security.core.GrantedAuthority> authorities = new java.util.ArrayList<>();
            
            // Para empleados: leer el claim "rol"
            String rol = jwt.getClaimAsString("rol");
            if (rol != null) {
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + rol));
                System.out.println("✅ Authority agregada desde claim 'rol': ROLE_" + rol);
            }
            
            // Para empresas: leer el claim "tipo"
            String tipo = jwt.getClaimAsString("tipo");
            if (tipo != null && tipo.equals("empresa_login")) {
                authorities.add(new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_EMPRESA"));
                System.out.println("✅ Authority agregada desde claim 'tipo': ROLE_EMPRESA");
            }
            
            System.out.println("🔐 Total authorities: " + authorities);
            return authorities;
        });
        
        return converter;
    }

    /**
     * Configura el codificador de contraseñas BCrypt.
     * @return BCryptPasswordEncoder
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configura CORS para permitir peticiones desde el frontend.
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(
            "http://localhost:5173",
            "http://localhost:5174",
            "https://store-repository.vercel.app",
            "https://store-application-liard.vercel.app"
        ));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * Configura la cadena de filtros de seguridad.
     * Define las reglas de seguridad para las diferentes rutas de la aplicación.
     * 
     * Rutas públicas (NO requieren JWT):
     * - /api/auth/login - Login de empleados
     * - /api/auth/register - Registro de empleados
     * - /api/auth/empresa/registro - Registro de empresas (Multi-Tenant)
     * - /api/auth/empresa/login - Login de empresas
     * - /api/auth/empresa/{id}/verificar - Verificación de email
     * - /api/productos/publico/** - Consultas públicas de productos
     * - /api/suscripciones/planes - Listar planes (NUEVO)
     * 
     * @param http Configuración de seguridad HTTP
     * @return SecurityFilterChain
     * @throws Exception si hay un error en la configuración
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para APIs
            .authorizeHttpRequests(auth -> auth
                // Rutas públicas de autenticación (NO requieren JWT)
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/auth/register").permitAll()
                .requestMatchers("/api/auth/empresa/registro").permitAll()
                .requestMatchers("/api/auth/empresa/login").permitAll()
                .requestMatchers("/api/auth/empresa/*/verificar").permitAll()
                .requestMatchers("/api/auth/empresa/*/tiene-empleados").permitAll() // Verificar empleados (público)
                .requestMatchers("/api/productos/publico/**").permitAll()
                .requestMatchers("/api/suscripciones/planes").permitAll() // Nuevo endpoint público
                .requestMatchers("/api/auth/verificar-email").permitAll() // Verificación de email
                .requestMatchers("/api/auth/reenviar-verificacion").permitAll() // Reenviar verificación
                
                // Rutas protegidas por roles
                .requestMatchers("/api/empleados").authenticated()
                .requestMatchers("/api/empleados/**").hasRole("ADMIN")
                .requestMatchers("/api/estadisticas/**").hasAnyRole("ADMIN", "GERENTE")
                
                // Todas las demás rutas requieren autenticación
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .decoder(jwtDecoder())
                    .jwtAuthenticationConverter(jwtAuthenticationConverter())
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}