package com.example.inventory_app.Controllers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el registro de una nueva empresa.
 * 
 * Este DTO se usa en el endpoint de registro público.
 * Contiene las validaciones necesarias para crear una cuenta empresarial.
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaRegistroDTO {

    @NotBlank(message = "El nombre de la empresa es obligatorio")
    @Size(min = 3, max = 200, message = "El nombre debe tener entre 3 y 200 caracteres")
    private String nombre;

    @Size(max = 150, message = "El nombre comercial no puede exceder 150 caracteres")
    private String nombreComercial;

    @NotBlank(message = "El NIT es obligatorio")
    @Size(min = 5, max = 20, message = "El NIT debe tener entre 5 y 20 caracteres")
    @Pattern(regexp = "^[0-9\\-]+$", message = "El NIT solo puede contener números y guiones")
    private String nit;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, max = 50, message = "La contraseña debe tener entre 8 y 50 caracteres")
    @Pattern(
        regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).*$",
        message = "La contraseña debe contener al menos una mayúscula, una minúscula, un número y un carácter especial"
    )
    private String password;

    @NotBlank(message = "Debe confirmar la contraseña")
    private String confirmPassword;

    @Pattern(regexp = "^[+]?[0-9]{7,20}$", message = "El teléfono debe ser válido")
    private String telefono;

    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccion;

    @Size(max = 100, message = "La ciudad no puede exceder 100 caracteres")
    private String ciudad;

    @Size(max = 100, message = "El país no puede exceder 100 caracteres")
    private String pais;

    @Size(max = 100, message = "La industria no puede exceder 100 caracteres")
    private String industria;

    private Integer numeroEmpleados;

    /**
     * Verifica si las contraseñas coinciden.
     * 
     * @return true si coinciden
     */
    public boolean passwordsCoinciden() {
        return password != null && password.equals(confirmPassword);
    }
}
