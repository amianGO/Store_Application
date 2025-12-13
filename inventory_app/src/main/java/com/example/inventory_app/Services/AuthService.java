package com.example.inventory_app.Services;

import com.example.inventory_app.Entities.Empleado;
import com.example.inventory_app.Entities.Empresa;

import java.util.Optional;

/**
 * Servicio de autenticación para sistema Multi-Tenant.
 * 
 * @author DamianG
 * @version 1.0
 */
public interface AuthService {
    
    /**
     * Busca una empresa por su tenant key en el schema public.
     * 
     * @param tenantKey Clave única del tenant
     * @return Optional con la empresa si existe
     */
    Optional<Empresa> findEmpresaByTenantKey(String tenantKey);
    
    /**
     * Autentica un empleado en el schema de su empresa.
     * 
     * Este método ejecuta en una transacción separada configurada
     * para el schema del tenant especificado.
     * 
     * @param schemaName Schema de la empresa
     * @param usuario Usuario del empleado
     * @param password Contraseña del empleado
     * @return Optional con el empleado si las credenciales son válidas
     */
    Optional<Empleado> autenticarEmpleadoEnTenant(String schemaName, String usuario, String password);
}
