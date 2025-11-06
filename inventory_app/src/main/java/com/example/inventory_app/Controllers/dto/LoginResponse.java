package com.example.inventory_app.Controllers.dto;

import com.example.inventory_app.Config.Rol;

public class LoginResponse {
    private String token;
    private String usuario;
    private Rol rol;
    private String nombre;
    private String apellido;
    private String cargo;

    public LoginResponse(String token, String usuario, Rol rol, String nombre, String apellido, String cargo) {
        this.token = token;
        this.usuario = usuario;
        this.rol = rol;
        this.nombre = nombre;
        this.apellido = apellido;
        this.cargo = cargo;
    }

    // Getters and Setters
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getCargo() {
        return cargo;
    }

    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
}
