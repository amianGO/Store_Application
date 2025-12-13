package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.Empleado;
import com.example.inventory_app.Repositories.EmpleadoRepository;
import com.example.inventory_app.Services.EmpleadoService;
import com.example.inventory_app.Config.TenantContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de Empleado.
 *
 * @author DamianG
 * @version 1.0
 */
@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    /**
     * Guarda un empleado en la base de datos.
     * IMPORTANTE: La contraseña debe venir SIN HASHEAR desde el controller.
     * Este método se encarga de hashearla con BCrypt.
     * 
     * @param empleado Entidad empleado a guardar (password en texto plano)
     * @return Empleado guardado con ID generado y password hasheada
     */
    @Override
    public Empleado save(Empleado empleado) {
        log.info("[EMPLEADO-SERVICE] Guardando empleado...");
        log.info("[EMPLEADO-SERVICE] TenantContext actual: {}", 
            com.example.inventory_app.Config.TenantContext.getCurrentTenant());
        
        if (empleado.getId() == null) {
            log.info("🔑 Nuevo empleado - Password length: {}", empleado.getPassword().length());
            log.info("🔑 Password bytes: {}", Arrays.toString(empleado.getPassword().getBytes(StandardCharsets.UTF_8)));
            
            // VALIDAR que la password NO esté ya hasheada
            if (empleado.getPassword().startsWith("$2a$") || empleado.getPassword().length() == 60) {
                log.error("❌ ERROR: La password YA está hasheada. NO se debe hashear dos veces.");
                log.error("   Controller debe enviar password en texto plano.");
                throw new RuntimeException("La password no debe estar pre-hasheada");
            }
            
            String hashedPassword = passwordEncoder.encode(empleado.getPassword());
            log.info("🔐 Hash generado: {}", hashedPassword);
            empleado.setPassword(hashedPassword);
            
            empleado.setEstadoActivo(true);
            empleado.setFechaContratacion(new java.util.Date());
        }
        
        Empleado guardado = empleadoRepository.save(empleado);
        
        log.info("[EMPLEADO-SERVICE] ✓ Empleado guardado con ID: {}", guardado.getId());
        
        return guardado;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findById(Long id) {
        return empleadoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findByUsuario(String usuario) {
        return empleadoRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findByDocumento(String documento) {
        return empleadoRepository.findByDocumento(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> findByCargo(String cargo) {
        return empleadoRepository.findByCargo(cargo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> findAllActive() {
        return empleadoRepository.findByEstadoActivoTrue();
    }

    @Override
    public void deactivate(Long id) {
        empleadoRepository.findById(id).ifPresent(empleado -> {
            empleado.setEstadoActivo(false);
            empleadoRepository.save(empleado);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> verificarCredenciales(String usuario, String password) {
        log.info("🔐 Verificando credenciales para: {}", usuario);
        log.info("🔑 Password recibida para login: '{}'", password);
        log.info("🔑 Password length: {}", password.length());
        log.info("🔑 Password bytes: {}", Arrays.toString(password.getBytes(StandardCharsets.UTF_8)));
        
        Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuario(usuario);
        
        if (empleadoOpt.isEmpty()) {
            log.error("✗ Usuario no encontrado");
            return Optional.empty();
        }
        
        Empleado empleado = empleadoOpt.get();
        log.info("✓ Usuario encontrado: {} {}", empleado.getNombre(), empleado.getApellido());
        log.info("Estado activo: {}", empleado.isEstadoActivo());
        log.info("Hash almacenado: {}...", empleado.getPassword().substring(0, 20));
        
        boolean passwordMatch = passwordEncoder.matches(password, empleado.getPassword());
        log.info("Password coincide: {}", passwordMatch);
        
        if (!empleado.isEstadoActivo()) {
            log.error("✗ Usuario inactivo");
            return Optional.empty();
        }
        
        if (!passwordMatch) {
            log.error("❌ DEBUGGING: Password NO coincide");
            log.error("   Raw password: '{}'", password);
            log.error("   Stored hash: '{}'", empleado.getPassword());
            return Optional.empty();
        }
        
        log.info("✓ Credenciales válidas");
        return Optional.of(empleado);
    }

    /**
     * Verifica si una empresa tiene empleados registrados.
     * Usa JdbcTemplate para evitar problemas de caché de Hibernate.
     * 
     * @param empresaId ID de la empresa
     * @param schemaName Schema de la empresa
     * @return true si tiene al menos 1 empleado
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public boolean empresaTieneEmpleados(Long empresaId, String schemaName) {
        System.out.println("[EMPLEADO-SERVICE] ═══ Verificando empleados ═══");
        System.out.println("[EMPLEADO-SERVICE] Schema: " + schemaName);
        
        try {
            // Usar JdbcTemplate con SET search_path
            jdbcTemplate.execute("SET search_path TO " + schemaName);
            
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM empleados", Long.class);
            
            System.out.println("[EMPLEADO-SERVICE] ✓ Schema " + schemaName + " tiene " + count + " empleados");
            
            return count != null && count > 0;
            
        } catch (Exception e) {
            System.err.println("[EMPLEADO-SERVICE] ✗ Error: " + e.getMessage());
            e.printStackTrace();
            return false;
        } finally {
            // Resetear a public
            try {
                jdbcTemplate.execute("SET search_path TO public");
            } catch (Exception e) {
                System.err.println("[EMPLEADO-SERVICE] Error al resetear search_path: " + e.getMessage());
            }
        }
    }
    
    /**
     * Obtiene la cantidad de empleados de una empresa.
     * Usa JdbcTemplate para evitar problemas de caché de Hibernate.
     * 
     * @param empresaId ID de la empresa
     * @param schemaName Schema de la empresa
     * @return Cantidad de empleados
     */
    @Transactional(propagation = org.springframework.transaction.annotation.Propagation.REQUIRES_NEW)
    public long contarEmpleados(Long empresaId, String schemaName) {
        System.out.println("[EMPLEADO-SERVICE] ═══ Contando empleados ═══");
        System.out.println("[EMPLEADO-SERVICE] Schema: " + schemaName);
        
        try {
            // Usar JdbcTemplate con SET search_path
            jdbcTemplate.execute("SET search_path TO " + schemaName);
            
            Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM empleados", Long.class);
            
            System.out.println("[EMPLEADO-SERVICE] ✓ Schema " + schemaName + " tiene " + count + " empleados");
            
            return count != null ? count : 0;
            
        } catch (Exception e) {
            System.err.println("[EMPLEADO-SERVICE] ✗ Error: " + e.getMessage());
            e.printStackTrace();
            return 0;
        } finally {
            // Resetear a public
            try {
                jdbcTemplate.execute("SET search_path TO public");
            } catch (Exception e) {
                System.err.println("[EMPLEADO-SERVICE] Error al resetear search_path: " + e.getMessage());
            }
        }
    }
}
