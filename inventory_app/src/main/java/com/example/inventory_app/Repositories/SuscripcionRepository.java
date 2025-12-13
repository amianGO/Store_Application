package com.example.inventory_app.Repositories;

import com.example.inventory_app.Entities.Empresa;
import com.example.inventory_app.Entities.EstadoSuscripcion;
import com.example.inventory_app.Entities.Suscripcion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Suscripcion.
 * 
 * IMPORTANTE: Este repositorio trabaja SIEMPRE en el schema PUBLIC
 * porque Suscripcion es una entidad global del sistema multi-tenant.
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Repository
public interface SuscripcionRepository extends JpaRepository<Suscripcion, Long> {

    /**
     * Busca la suscripción de una empresa.
     * 
     * @param empresa Empresa a buscar
     * @return Optional con la suscripción si existe
     */
    Optional<Suscripcion> findByEmpresa(Empresa empresa);

    /**
     * Busca una suscripción por su license key.
     * 
     * @param licenseKey License key a buscar
     * @return Optional con la suscripción si existe
     */
    Optional<Suscripcion> findByLicenseKey(String licenseKey);

    /**
     * Busca todas las suscripciones por estado.
     * 
     * @param estado Estado a filtrar
     * @return Lista de suscripciones
     */
    List<Suscripcion> findByEstado(EstadoSuscripcion estado);

    /**
     * Busca suscripciones que vencen próximamente.
     * Útil para enviar notificaciones de renovación.
     * 
     * @param fechaInicio Fecha desde
     * @param fechaFin Fecha hasta
     * @return Lista de suscripciones próximas a vencer
     */
    @Query("SELECT s FROM Suscripcion s WHERE s.fechaVencimiento BETWEEN :fechaInicio AND :fechaFin AND s.estado = 'ACTIVA'")
    List<Suscripcion> findProximasAVencer(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca suscripciones expiradas que no han sido renovadas.
     * 
     * @param ahora Fecha actual
     * @return Lista de suscripciones expiradas
     */
    @Query("SELECT s FROM Suscripcion s WHERE s.fechaVencimiento < :ahora AND s.estado IN ('ACTIVA', 'PRUEBA')")
    List<Suscripcion> findExpiradas(LocalDateTime ahora);

    /**
     * Cuenta las terminales activas de una empresa.
     * 
     * @param empresaId ID de la empresa
     * @return Cantidad de terminales activas
     */
    @Query("SELECT s.terminalesActivas FROM Suscripcion s WHERE s.empresa.id = :empresaId")
    Integer countTerminalesActivas(Long empresaId);
}
