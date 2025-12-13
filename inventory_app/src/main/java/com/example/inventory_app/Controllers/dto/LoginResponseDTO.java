package com.example.inventory_app.Controllers.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de login exitoso.
 * 
 * Contiene el token JWT y la información básica de la empresa.
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponseDTO {

    private String token;
    private String tipo = "Bearer";
    private EmpresaResponseDTO empresa;

    public LoginResponseDTO(String token, EmpresaResponseDTO empresa) {
        this.token = token;
        this.empresa = empresa;
    }
}
