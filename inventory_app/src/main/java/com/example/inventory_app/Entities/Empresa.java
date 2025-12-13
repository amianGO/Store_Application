package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entidad global que representa una Empresa en el sistema Multi-Tenant.
 * 
 * IMPORTANTE: Esta entidad se almacena en el SCHEMA PÚBLICO (public)
 * y NO en schemas de tenants individuales.
 * 
 * PROPÓSITO:
 * - Registro central de todas las empresas del sistema
 * - Gestión de acceso y autenticación a nivel empresa
 * - Vinculación con suscripciones y schemas de base de datos
 * 
 * FLUJO DE REGISTRO:
 * 1. Empresa se registra → Se crea registro en tabla empresas (schema public)
 * 2. Se crea suscripción inicial → Tabla suscripciones (schema public)
 * 3. Se crea schema dedicado → CREATE SCHEMA empresa_{id}
 * 4. Se clonan tablas desde template_schema → empresa_{id}
 * 5. Empresa puede operar en su schema aislado
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Entity
@Table(name = "empresas", schema = "public")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre legal o razón social de la empresa.
     * Ejemplo: "Distribuidora XYZ S.A.S"
     */
    @Column(nullable = false, length = 200)
    private String nombre;

    /**
     * Nombre comercial o marca de la empresa.
     * Ejemplo: "XYZ Store"
     */
    @Column(name = "nombre_comercial", length = 150)
    private String nombreComercial;

    /**
     * NIT (Número de Identificación Tributaria) o equivalente.
     * Debe ser único en el sistema.
     */
    @Column(nullable = false, unique = true, length = 20)
    private String nit;

    /**
     * Email corporativo de la empresa.
     * Se usa para:
     * - Comunicaciones oficiales
     * - Recuperación de contraseña
     * - Notificaciones de suscripción
     */
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    /**
     * Contraseña encriptada de la empresa (para login inicial).
     * 
     * IMPORTANTE: Debe almacenarse encriptada usando BCrypt.
     * Esta contraseña es para el acceso administrativo de la empresa.
     */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Teléfono de contacto principal.
     */
    @Column(length = 20)
    private String telefono;

    /**
     * Dirección física de la empresa.
     */
    @Column(length = 255)
    private String direccion;

    /**
     * Ciudad donde opera la empresa.
     */
    @Column(length = 100)
    private String ciudad;

    /**
     * País donde opera la empresa.
     */
    @Column(length = 100)
    private String pais;

    /**
     * Industria o sector económico de la empresa.
     * Ejemplo: "Retail", "Tecnología", "Alimentos", etc.
     */
    @Column(length = 100)
    private String industria;

    /**
     * Número aproximado de empleados de la empresa.
     * Útil para estadísticas y dimensionamiento del servicio.
     */
    @Column(name = "numero_empleados")
    private Integer numeroEmpleados;

    /**
     * Nombre del schema de base de datos asignado a esta empresa.
     * 
     * Formato recomendado: "empresa_{id}" o "tenant_{id}"
     * Ejemplo: "empresa_1", "empresa_2"
     * 
     * Este schema se crea automáticamente al activar la suscripción.
     */
    @Column(name = "schema_name", unique = true, length = 50)
    private String schemaName;

    /**
     * Clave única de la empresa para identificación en URLs/APIs.
     * 
     * Ejemplo: "xyz-store", "mi-empresa-123"
     * Útil para subdominios multi-tenant: xyz-store.tuapp.com
     */
    @Column(name = "tenant_key", unique = true, length = 50)
    private String tenantKey;

    /**
     * Indica si la empresa está activa en el sistema.
     * 
     * false = Empresa deshabilitada (no puede acceder)
     * true = Empresa activa
     * 
     * Se puede desactivar por:
     * - Impago de suscripción
     * - Violación de términos
     * - Solicitud del cliente
     */
    @Column(name = "activa", nullable = false)
    private boolean activa = true;

    // ========================================
    // CAMPOS DE VERIFICACIÓN DE EMAIL
    // ========================================
    
    @Column(name = "email_verificado", nullable = false)
    private boolean emailVerificado = false;

    @Column(name = "token_verificacion", length = 100)
    private String tokenVerificacion;

    @Column(name = "fecha_verificacion")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date fechaVerificacion;

    /**
     * Fecha y hora de registro de la empresa.
     */
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    private LocalDateTime fechaRegistro;

    /**
     * Fecha y hora de última actualización de datos.
     */
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    /**
     * Fecha y hora del último acceso/login de la empresa.
     */
    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    /**
     * Logo de la empresa (URL o Base64).
     * Opcional, para personalización de la interfaz.
     */
    @Column(length = 500)
    private String logo;

    /**
     * Sitio web de la empresa.
     */
    @Column(name = "sitio_web", length = 200)
    private String sitioWeb;

    /**
     * Notas o comentarios administrativos sobre la empresa.
     * Uso interno del sistema.
     */
    @Column(length = 500)
    private String notas;

    /**
     * Relación con la suscripción activa.
     * Una empresa tiene una suscripción activa en un momento dado.
     */
    @OneToOne(mappedBy = "empresa", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Suscripcion suscripcionActiva;

    /**
     * Se ejecuta antes de persistir la entidad por primera vez.
     */
    @PrePersist
    protected void onCreate() {
        this.fechaRegistro = LocalDateTime.now();
        this.fechaActualizacion = LocalDateTime.now();
        
        // Generar schema name si no existe
        if (this.schemaName == null || this.schemaName.isEmpty()) {
            // Se generará después de obtener el ID
            // Formato: "empresa_{id}"
        }
        
        // Generar tenant key si no existe
        if (this.tenantKey == null || this.tenantKey.isEmpty()) {
            // Se puede generar basado en el nombre de la empresa
            this.tenantKey = generarTenantKey(this.nombre);
        }
        
        // Generar token de verificación si no existe
        if (this.tokenVerificacion == null || this.tokenVerificacion.isEmpty()) {
            this.tokenVerificacion = generarTokenVerificacion();
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
     * Genera un tenant key único basado en el nombre de la empresa.
     * 
     * @param nombre Nombre de la empresa
     * @return Tenant key generado
     */
    private String generarTenantKey(String nombre) {
        if (nombre == null || nombre.isEmpty()) {
            return "empresa-" + System.currentTimeMillis();
        }
        
        // Convertir a minúsculas, eliminar acentos, reemplazar espacios
        String key = nombre.toLowerCase()
                          .replaceAll("[áàäâ]", "a")
                          .replaceAll("[éèëê]", "e")
                          .replaceAll("[íìïî]", "i")
                          .replaceAll("[óòöô]", "o")
                          .replaceAll("[úùüû]", "u")
                          .replaceAll("[^a-z0-9\\s-]", "")
                          .replaceAll("\\s+", "-")
                          .replaceAll("-+", "-");
        
        // Limitar longitud
        if (key.length() > 40) {
            key = key.substring(0, 40);
        }
        
        return key;
    }

    /**
     * Genera un token único para verificación de email.
     */
    private String generarTokenVerificacion() {
        return java.util.UUID.randomUUID().toString();
    }
    
    /**
     * Marca el email como verificado.
     */
    public void verificarEmail() {
        this.emailVerificado = true;
        this.fechaVerificacion = new java.util.Date();
        this.tokenVerificacion = null; // Limpiar token después de usar
    }
    
    /**
     * Verifica si el token proporcionado es válido.
     */
    public boolean validarToken(String token) {
        return this.tokenVerificacion != null && this.tokenVerificacion.equals(token);
    }
    
    /**
     * Regenera el token de verificación.
     */
    public void regenerarTokenVerificacion() {
        this.tokenVerificacion = generarTokenVerificacion();
    }

    /**
     * Valida si la empresa tiene acceso al sistema.
     * Requisitos: activa, email verificado, suscripción activa.
     */
    public boolean tieneAcceso() {
        return this.activa && 
               this.emailVerificado && 
               this.suscripcionActiva != null && 
               this.suscripcionActiva.estaActiva();
    }

    /**
     * Genera el nombre del schema para esta empresa.
     * 
     * @return Nombre del schema (ej: "empresa_123")
     */
    public String generarSchemaName() {
        if (this.id == null) {
            throw new IllegalStateException("No se puede generar schema name sin ID");
        }
        return "empresa_" + this.id;
    }
}
