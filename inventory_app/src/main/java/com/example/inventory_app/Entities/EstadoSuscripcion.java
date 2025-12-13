package com.example.inventory_app.Entities;

/**
 * Estados posibles de una suscripción.
 * 
 * Define el ciclo de vida de una suscripción en el sistema.
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
public enum EstadoSuscripcion {
    
    /**
     * Suscripción activa y funcionando.
     * La empresa tiene acceso completo al sistema.
     */
    ACTIVA("Activa"),
    
    /**
     * Suscripción en período de prueba.
     * Acceso limitado según el plan de prueba.
     */
    PRUEBA("Prueba"),
    
    /**
     * Suscripción suspendida temporalmente.
     * Posibles razones:
     * - Falta de pago
     * - Solicitud del cliente
     * - Mantenimiento
     */
    SUSPENDIDA("Suspendida"),
    
    /**
     * Suscripción expirada.
     * El período de validez ha terminado y no se ha renovado.
     */
    EXPIRADA("Expirada"),
    
    /**
     * Suscripción cancelada permanentemente.
     * Por solicitud del cliente o decisión administrativa.
     */
    CANCELADA("Cancelada"),
    
    /**
     * Suscripción en proceso de renovación.
     * Esperando confirmación de pago.
     */
    PENDIENTE_RENOVACION("Pendiente de Renovación");

    private final String descripcion;

    EstadoSuscripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }

    /**
     * Verifica si el estado permite acceso al sistema.
     * 
     * @return true si el estado permite acceso
     */
    public boolean permiteAcceso() {
        return this == ACTIVA || this == PRUEBA;
    }

    /**
     * Verifica si la suscripción puede ser renovada.
     * 
     * @return true si puede renovarse
     */
    public boolean puedeRenovarse() {
        return this == EXPIRADA || this == SUSPENDIDA || this == ACTIVA;
    }
}
