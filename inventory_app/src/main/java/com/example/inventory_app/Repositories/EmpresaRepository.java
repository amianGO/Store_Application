package com.example.inventory_app.Repositories;

import com.example.inventory_app.Entities.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para la entidad Empresa.
 * 
 * IMPORTANTE: Este repositorio trabaja SIEMPRE en el schema PUBLIC
 * porque Empresa es una entidad global del sistema multi-tenant.
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    /**
     * Busca una empresa por su email (único).
     * Útil para login y validaciones de registro.
     * 
     * @param email Email de la empresa
     * @return Optional con la empresa si existe
     */
    Optional<Empresa> findByEmail(String email);

    /**
     * Busca una empresa por su NIT (único).
     * Útil para validaciones y consultas.
     * 
     * @param nit NIT de la empresa
     * @return Optional con la empresa si existe
     */
    Optional<Empresa> findByNit(String nit);

    /**
     * Busca una empresa por su tenant key (único).
     * Útil para resolver tenant en requests basados en subdominio.
     * 
     * @param tenantKey Tenant key de la empresa
     * @return Optional con la empresa si existe
     */
    Optional<Empresa> findByTenantKey(String tenantKey);

    /**
     * Busca una empresa por su schema name.
     * 
     * @param schemaName Nombre del schema
     * @return Optional con la empresa si existe
     */
    Optional<Empresa> findBySchemaName(String schemaName);
    
    Optional<Empresa> findByTokenVerificacion(String tokenVerificacion);
    
    /**
     * Verifica si existe una empresa con el email dado.
     * 
     * @param email Email a verificar
     * @return true si existe
     */
    boolean existsByEmail(String email);

    /**
     * Verifica si existe una empresa con el NIT dado.
     * 
     * @param nit NIT a verificar
     * @return true si existe
     */
    boolean existsByNit(String nit);

    /**
     * Verifica si existe una empresa con el tenant key dado.
     * 
     * @param tenantKey Tenant key a verificar
     * @return true si existe
     */
    boolean existsByTenantKey(String tenantKey);
}
