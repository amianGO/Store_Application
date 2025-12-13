package com.example.inventory_app.Controllers.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el login de empleados en sistema Multi-Tenant.
 * 
 * Requiere el tenantKey o schemaName de la empresa para saber
 * en qué schema buscar las credenciales.
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-25
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpleadoLoginDTO {

    /**
     * Usuario del empleado (único dentro del tenant).
     */
    @NotBlank(message = "El usuario es obligatorio")
    private String usuario;

    /**
     * Contraseña del empleado.
     */
    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    /**
     * Tenant Key de la empresa.
     * Se usa para identificar el schema donde buscar al empleado.
     * 
     * Ejemplo: "abc123def456"
     */
    @NotBlank(message = "El tenant key es obligatorio")
    private String tenantKey;
}
