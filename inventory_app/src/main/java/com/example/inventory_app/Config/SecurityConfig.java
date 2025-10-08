package com.example.inventory_app.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
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
            .csrf(csrf -> csrf.disable()) // Deshabilitamos CSRF para APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll() // Endpoints públicos
                .requestMatchers("/api/empleados/registro").permitAll() // Permitir registro sin autenticación
                .requestMatchers("/api/admin/**").hasRole("ADMIN") // Solo admin
                .requestMatchers("/api/ventas/**").hasAnyRole("VENDEDOR", "ADMIN") // Vendedores y admin
                .anyRequest().authenticated() // Resto requiere autenticación
            )
            .httpBasic(); // Autenticación básica para pruebas

        return http.build();
    }
}
