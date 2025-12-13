package com.example.inventory_app.Controllers.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para el login de empresa.
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpresaLoginDTO {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email debe ser válido")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
