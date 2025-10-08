package com.example.inventory_app.Config;

import com.example.inventory_app.Entities.Empleado;
import com.example.inventory_app.Services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

/**
 * Servicio personalizado para la autenticación de usuarios.
 * Implementa UserDetailsService de Spring Security.
 *
 * @author DamianG
 * @version 1.0
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private EmpleadoService empleadoService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Empleado empleado = empleadoService.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        if (!empleado.isEstadoActivo()) {
            throw new UsernameNotFoundException("Usuario inactivo: " + username);
        }

        return new User(
            empleado.getUsuario(),
            empleado.getPassword(),
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + empleado.getRol().name()))
        );
    }
}
