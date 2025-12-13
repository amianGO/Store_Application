package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Config.TenantContext;
import com.example.inventory_app.Entities.Empleado;
import com.example.inventory_app.Entities.Empresa;
import com.example.inventory_app.Repositories.EmpleadoRepository;
import com.example.inventory_app.Repositories.EmpresaRepository;
import com.example.inventory_app.Services.AuthService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Implementación del servicio de autenticación Multi-Tenant.
 * 
 * IMPORTANTE:
 * - findEmpresaByTenantKey: Se ejecuta en schema PUBLIC
 * - autenticarEmpleadoEnTenant: Se ejecuta en schema del TENANT
 * - Cada método tiene su propia transacción (REQUIRES_NEW)
 * 
 * @author DamianG
 * @version 1.0
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private EmpleadoRepository empleadoRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Busca empresa por tenantKey en schema PUBLIC.
     * 
     * NO transaccional - deja que el repository maneje su propia transacción.
     */
    @Override
    public Optional<Empresa> findEmpresaByTenantKey(String tenantKey) {
        System.out.println("[AUTH-SERVICE] Buscando empresa en schema PUBLIC");
        
        // Asegurar que estamos en public
        TenantContext.resetToDefault();
        
        Optional<Empresa> result = empresaRepository.findByTenantKey(tenantKey);
        
        // Limpiar el context después de la búsqueda
        TenantContext.clear();
        
        return result;
    }

    /**
     * Autentica empleado en el schema de su empresa.
     * 
     * CRÍTICO: Este método NO es transaccional. Primero configura el tenant
     * y luego delega al método transaccional interno.
     */
    @Override
    public Optional<Empleado> autenticarEmpleadoEnTenant(String schemaName, String usuario, String password) {
        System.out.println("[AUTH-SERVICE] ═══════════════════════════════════════");
        System.out.println("[AUTH-SERVICE] Autenticando empleado en schema: " + schemaName);
        System.out.println("[AUTH-SERVICE] Usuario: " + usuario);
        
        // CRÍTICO: Establecer el tenant ANTES de iniciar la transacción
        TenantContext.setCurrentTenant(schemaName);
        
        System.out.println("[AUTH-SERVICE] TenantContext configurado: " + TenantContext.getCurrentTenant());
        System.out.println("[AUTH-SERVICE] ═══════════════════════════════════════");
        
        try {
            // Llamar al método transaccional que ejecutará en el schema correcto
            return buscarYValidarEmpleado(usuario, password);
        } finally {
            // Limpiar el tenant context después de la operación
            System.out.println("[AUTH-SERVICE] 🧹 Limpiando TenantContext");
            TenantContext.clear();
        }
    }
    
    /**
     * Método interno transaccional que busca y valida el empleado.
     * 
     * CRÍTICO: Solo hace clear() para limpiar cache de Hibernate.
     * La nueva transacción consultará el TenantResolver automáticamente.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW, readOnly = true)
    protected Optional<Empleado> buscarYValidarEmpleado(String usuario, String password) {
        try {
            System.out.println("[AUTH-SERVICE] 🔍 Buscando empleado en Hibernate...");
            
            // Limpiar el cache de Hibernate para forzar nueva consulta
            entityManager.clear();
            
            System.out.println("[AUTH-SERVICE] EntityManager limpiado - próxima query consultará TenantResolver");
            
            // Buscar empleado en el schema del tenant
            Optional<Empleado> empleadoOpt = empleadoRepository.findByUsuario(usuario);
            
            if (empleadoOpt.isEmpty()) {
                System.out.println("[AUTH-SERVICE] ✗ Empleado no encontrado");
                return Optional.empty();
            }
            
            Empleado empleado = empleadoOpt.get();
            
            // Verificar contraseña
            if (!passwordEncoder.matches(password, empleado.getPassword())) {
                System.out.println("[AUTH-SERVICE] ✗ Contraseña incorrecta");
                return Optional.empty();
            }
            
            // Verificar que el empleado esté activo
            if (!empleado.isEstadoActivo()) {
                System.out.println("[AUTH-SERVICE] ✗ Empleado inactivo");
                return Optional.empty();
            }
            
            System.out.println("[AUTH-SERVICE] ✓ Empleado autenticado: " + empleado.getNombre() + " " + empleado.getApellido());
            return Optional.of(empleado);
            
        } catch (Exception e) {
            System.err.println("[AUTH-SERVICE] ✗ Error al autenticar: " + e.getMessage());
            e.printStackTrace();
            return Optional.empty();
        }
    }
}
