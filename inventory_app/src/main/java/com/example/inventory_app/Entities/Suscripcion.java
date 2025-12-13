package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad que representa una Suscripción de una empresa.
 * 
 * IMPORTANTE: Esta entidad se almacena en el SCHEMA PÚBLICO (public)
 * para control centralizado de todas las suscripciones.
 * 
 * PROPÓSITO:
 * - Gestión de planes y períodos de suscripción
 * - Control de acceso basado en estado de suscripción
 * - Límites de uso por plan
 * - Facturación y renovaciones
 * 
 * FLUJO DE SUSCRIPCIÓN:
 * 1. Empresa se registra → Suscripción PRUEBA creada automáticamente
 * 2. Empresa activa plan pagado → Suscripción ACTIVA
 * 3. Llega fecha de vencimiento → Suscripción EXPIRADA
 * 4. Empresa renueva → Nueva Suscripción ACTIVA
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Entity
@Table(name = "suscripciones", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Suscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Empresa propietaria de esta suscripción.
     * Relación uno a uno: Una empresa tiene una suscripción activa.
     */
    @OneToOne
    @JoinColumn(name = "empresa_id", nullable = false, unique = true)
    private Empresa empresa;

    /**
     * Tipo de plan contratado.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_plan", nullable = false, length = 50)
    private TipoPlan tipoPlan;

    /**
     * Estado actual de la suscripción.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private EstadoSuscripcion estado;

    /**
     * Fecha de inicio de la suscripción.
     */
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    /**
     * Fecha de vencimiento de la suscripción.
     * null = suscripción sin fecha de fin (raro, solo para planes especiales)
     */
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDateTime fechaVencimiento;

    /**
     * Fecha de última renovación.
     */
    @Column(name = "fecha_ultima_renovacion")
    private LocalDateTime fechaUltimaRenovacion;

    /**
     * Clave única de licencia para esta suscripción.
     * 
     * Formato sugerido: XXXX-XXXX-XXXX-XXXX
     * Se genera automáticamente al crear la suscripción.
     * 
     * Usos:
     * - Activación de terminales
     * - Verificación de autenticidad
     * - Soporte técnico
     */
    @Column(name = "license_key", unique = true, nullable = false, length = 50)
    private String licenseKey;

    /**
     * Máximo de terminales/sesiones concurrentes permitidas.
     * -1 = ilimitado
     * Se copia del plan al crear la suscripción (puede personalizarse)
     */
    @Column(name = "max_terminales", nullable = false)
    private Integer maxTerminales;

    /**
     * Máximo de productos en inventario.
     * -1 = ilimitado
     */
    @Column(name = "max_productos", nullable = false)
    private Integer maxProductos;

    /**
     * Máximo de empleados registrados.
     * -1 = ilimitado
     */
    @Column(name = "max_empleados", nullable = false)
    private Integer maxEmpleados;

    /**
     * Cantidad de terminales actualmente en uso.
     * Se actualiza dinámicamente al hacer login/logout.
     */
    @Column(name = "terminales_activas", nullable = false)
    private Integer terminalesActivas = 0;

    /**
     * Precio pagado por esta suscripción.
     * Puede diferir del precio del plan (descuentos, promociones).
     */
    @Column(name = "precio_pagado")
    private Double precioPagado;

    /**
     * Método de pago utilizado.
     * Ejemplos: "Tarjeta", "Transferencia", "PayPal", etc.
     */
    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    /**
     * Referencia de la transacción de pago.
     * ID del pago en pasarela de pago externa.
     */
    @Column(name = "referencia_pago", length = 100)
    private String referenciaPago;

    /**
     * Indica si la renovación automática está habilitada.
     */
    @Column(name = "renovacion_automatica", nullable = false)
    private Boolean renovacionAutomatica = false;

    /**
     * Notas o comentarios administrativos sobre la suscripción.
     */
    @Column(length = 500)
    private String notas;

    /**
     * Fecha de creación del registro.
     */
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    /**
     * Fecha de última actualización.
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Se ejecuta antes de persistir la entidad.
     */
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        
        // Generar license key si no existe
        if (this.licenseKey == null || this.licenseKey.isEmpty()) {
            this.licenseKey = generarLicenseKey();
        }
        
        // Copiar límites del plan si no están establecidos
        if (this.maxTerminales == null && this.tipoPlan != null) {
            this.maxTerminales = this.tipoPlan.getMaxTerminales();
        }
        if (this.maxProductos == null && this.tipoPlan != null) {
            this.maxProductos = this.tipoPlan.getMaxProductos();
        }
        if (this.maxEmpleados == null && this.tipoPlan != null) {
            this.maxEmpleados = this.tipoPlan.getMaxEmpleados();
        }
        
        // Inicializar terminales activas
        if (this.terminalesActivas == null) {
            this.terminalesActivas = 0;
        }
    }

    /**
     * Se ejecuta antes de actualizar la entidad.
     */
    @PreUpdate
    protected void onUpdate() {
        this.fechaActualizacion = LocalDateTime.now();
    }

    /**
     * Verifica si la suscripción está activa y no ha expirado.
     * 
     * @return true si está activa
     */
    public boolean estaActiva() {
        if (this.estado == null) {
            return false;
        }
        
        // Verificar estado
        if (!this.estado.permiteAcceso()) {
            return false;
        }
        
        // Verificar fecha de vencimiento
        if (this.fechaVencimiento != null && LocalDateTime.now().isAfter(this.fechaVencimiento)) {
            return false;
        }
        
        return true;
    }

    /**
     * Verifica si se puede agregar una nueva terminal.
     * 
     * @return true si hay espacio disponible
     */
    public boolean puedeAgregarTerminal() {
        if (this.maxTerminales == -1) {
            return true; // Ilimitado
        }
        return this.terminalesActivas < this.maxTerminales;
    }

    /**
     * Incrementa el contador de terminales activas.
     * 
     * @throws IllegalStateException si se alcanzó el límite
     */
    public void agregarTerminalActiva() {
        if (!puedeAgregarTerminal()) {
            throw new IllegalStateException(
                "Límite de terminales alcanzado: " + this.maxTerminales
            );
        }
        this.terminalesActivas++;
    }

    /**
     * Decrementa el contador de terminales activas.
     */
    public void removerTerminalActiva() {
        if (this.terminalesActivas > 0) {
            this.terminalesActivas--;
        }
    }

    /**
     * Calcula los días restantes de suscripción.
     * 
     * @return Días restantes, 0 si expiró
     */
    public long diasRestantes() {
        if (this.fechaVencimiento == null) {
            return Long.MAX_VALUE; // Suscripción sin vencimiento
        }
        
        LocalDateTime ahora = LocalDateTime.now();
        if (ahora.isAfter(this.fechaVencimiento)) {
            return 0; // Expirada
        }
        
        return java.time.Duration.between(ahora, this.fechaVencimiento).toDays();
    }

    /**
     * Genera una license key única.
     * 
     * Formato: XXXX-XXXX-XXXX-XXXX
     * 
     * @return License key generada
     */
    private String generarLicenseKey() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder key = new StringBuilder();
        
        for (int i = 0; i < 4; i++) {
            if (i > 0) {
                key.append("-");
            }
            for (int j = 0; j < 4; j++) {
                int index = (int) (Math.random() * chars.length());
                key.append(chars.charAt(index));
            }
        }
        
        return key.toString();
    }

    /**
     * Renueva la suscripción extendiendo la fecha de vencimiento.
     * 
     * @param meses Meses a extender
     */
    public void renovar(int meses) {
        LocalDateTime nuevaFechaVencimiento;
        
        if (this.fechaVencimiento != null && LocalDateTime.now().isBefore(this.fechaVencimiento)) {
            // Si aún no ha expirado, extender desde la fecha actual de vencimiento
            nuevaFechaVencimiento = this.fechaVencimiento.plusMonths(meses);
        } else {
            // Si ya expiró, extender desde ahora
            nuevaFechaVencimiento = LocalDateTime.now().plusMonths(meses);
        }
        
        this.fechaVencimiento = nuevaFechaVencimiento;
        this.fechaUltimaRenovacion = LocalDateTime.now();
        this.estado = EstadoSuscripcion.ACTIVA;
    }
}
