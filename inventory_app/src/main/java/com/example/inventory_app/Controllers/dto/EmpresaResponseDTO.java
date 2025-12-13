package com.example.inventory_app.Controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO para la respuesta de información de empresa.
 * 
 * NO incluye información sensible como contraseñas.
 * Usado en respuestas de API.
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaResponseDTO {

    private Long id;
    private String nombre;
    private String nombreComercial;
    private String nit;
    private String email;
    private String telefono;
    private String direccion;
    private String ciudad;
    private String pais;
    private String tenantKey;
    private String schemaName;
    private Boolean activa;
    private Boolean emailVerificado;
    private Date fechaVerificacion;
    private LocalDateTime fechaRegistro;
    private String logo;
    private String sitioWeb;

    // Información de la suscripción (anidada)
    private SuscripcionInfoDTO suscripcion;

    /**
     * DTO anidado para información básica de suscripción.
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuscripcionInfoDTO {
        private String tipoPlan;
        private String estado;
        private LocalDateTime fechaVencimiento;
        private Long diasRestantes;
        private String licenseKey;
        private Integer terminalesActivas;
        private Integer maxTerminales;
    }
}
