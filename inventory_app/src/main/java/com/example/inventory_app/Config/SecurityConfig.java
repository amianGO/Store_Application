package com.example.inventory_app.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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

import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Configuración de seguridad para la aplicación.
 * Define la configuración de Spring Security y los beans necesarios.
 *
 * @author DamianG
 * @version 1.0
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtService jwtService;

    /**
     * Configura el decodificador JWT
     */
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(Keys.hmacShaKeyFor(
            Base64.getDecoder().decode(jwtService.getSecretKey())
        )).build();
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
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
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
                .requestMatchers("/api/auth/**").permitAll() // Solo los endpoints de autenticación son públicos
                .requestMatchers("/api/productos/publico/**").permitAll() // Solo consultas públicas de productos
                .requestMatchers("/api/empleados/**").hasRole("ADMIN")
                .requestMatchers("/api/estadisticas/**").hasAnyRole("ADMIN", "GERENTE")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}