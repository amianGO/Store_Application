package com.example.inventory_app.Entities;

/**
 * Tipos de planes de suscripción disponibles.
 * 
 * Define los diferentes niveles de servicio que puede contratar una empresa.
 * Cada plan tiene diferentes límites y características.
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
public enum TipoPlan {
    
    /**
     * Plan de prueba gratuito.
     * - Duración: 15-30 días
     * - Funcionalidades limitadas
     * - Ideal para evaluar el sistema
     */
    PRUEBA("Plan de Prueba", 0.0, 1, 100, 10),
    
    /**
     * Plan básico para pequeños negocios.
     * - Funcionalidades esenciales
     * - 1-3 terminales activas
     * - Hasta 1000 productos
     */
    BASICO("Plan Básico", 29.99, 3, 1000, 50),
    
    /**
     * Plan profesional para negocios en crecimiento.
     * - Funcionalidades completas
     * - 5-10 terminales activas
     * - Hasta 5000 productos
     * - Reportes avanzados
     */
    PROFESIONAL("Plan Profesional", 79.99, 10, 5000, 200),
    
    /**
     * Plan empresarial para grandes organizaciones.
     * - Todas las funcionalidades
     * - Terminales ilimitadas
     * - Productos ilimitados
     * - Soporte prioritario
     * - Integraciones avanzadas
     */
    EMPRESARIAL("Plan Empresarial", 199.99, -1, -1, -1),
    
    /**
     * Plan personalizado.
     * - Características a medida
     * - Precio negociado
     */
    PERSONALIZADO("Plan Personalizado", 0.0, -1, -1, -1);

    private final String nombre;
    private final Double precioMensual;
    private final Integer maxTerminales;  // -1 = ilimitado
    private final Integer maxProductos;   // -1 = ilimitado
    private final Integer maxEmpleados;   // -1 = ilimitado

    /**
     * Constructor del enum.
     * 
     * @param nombre Nombre descriptivo del plan
     * @param precioMensual Precio mensual en USD (o moneda base)
     * @param maxTerminales Máximo de terminales/sesiones concurrentes (-1 = ilimitado)
     * @param maxProductos Máximo de productos en inventario (-1 = ilimitado)
     * @param maxEmpleados Máximo de empleados registrados (-1 = ilimitado)
     */
    TipoPlan(String nombre, Double precioMensual, Integer maxTerminales, 
             Integer maxProductos, Integer maxEmpleados) {
        this.nombre = nombre;
        this.precioMensual = precioMensual;
        this.maxTerminales = maxTerminales;
        this.maxProductos = maxProductos;
        this.maxEmpleados = maxEmpleados;
    }

    public String getNombre() {
        return nombre;
    }

    public Double getPrecioMensual() {
        return precioMensual;
    }

    public Integer getMaxTerminales() {
        return maxTerminales;
    }

    public Integer getMaxProductos() {
        return maxProductos;
    }

    public Integer getMaxEmpleados() {
        return maxEmpleados;
    }

    /**
     * Verifica si un recurso está dentro del límite del plan.
     * 
     * @param cantidadActual Cantidad actual del recurso
     * @param limite Límite del plan (-1 = ilimitado)
     * @return true si está dentro del límite
     */
    public static boolean dentroDelLimite(Integer cantidadActual, Integer limite) {
        if (limite == -1) {
            return true; // Ilimitado
        }
        return cantidadActual < limite;
    }

    /**
     * Verifica si el plan es ilimitado para un recurso.
     * 
     * @param limite Límite a verificar
     * @return true si es ilimitado
     */
    public static boolean esIlimitado(Integer limite) {
        return limite == -1;
    }
}
