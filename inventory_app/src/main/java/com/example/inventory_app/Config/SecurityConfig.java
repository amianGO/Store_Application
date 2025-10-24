package com.example.inventory_app.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

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
                .requestMatchers("/api/auth/**").permitAll() // Endpoints de autenticación públicos
                .requestMatchers("/api/productos/**").permitAll() // Productos públicos para consulta
                .requestMatchers("/api/clientes/**").permitAll() // Clientes públicos
                .requestMatchers("/api/carritos/**").permitAll() // Carritos públicos
                .requestMatchers("/api/facturas/**").permitAll() // Facturas públicas
                .requestMatchers("/api/cajas/**").permitAll() // Cajas públicas
                .requestMatchers("/api/estadisticas/**").permitAll() // Estadísticas públicas
                .requestMatchers("/api/empleados/**").hasRole("ADMIN") // Solo admin para empleados
                .anyRequest().authenticated() // Resto requiere autenticación
            )
            .httpBasic(withDefaults()); // Autenticación básica para pruebas

        return http.build();
    }
}