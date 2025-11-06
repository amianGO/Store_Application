package com.example.inventory_app.Controllers.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginDTO {
    @NotBlank
    private String usuario;
    
    @NotBlank
    private String password;

    // Getters and Setters
    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
