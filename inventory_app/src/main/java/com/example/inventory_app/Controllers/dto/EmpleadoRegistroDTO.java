package com.example.inventory_app.Controllers.dto;

import com.example.inventory_app.Config.Rol;
import lombok.Data;

/**
 * DTO para el registro de empleados.
 * Esta clase maneja los datos necesarios para crear un nuevo empleado.
 *
 * @author DamianG
 * @version 1.0
 */
@Data
public class EmpleadoRegistroDTO {
    private String nombre;
    private String apellido;
    private String documento;
    private String usuario;
    private String password;
    private String telefono;
    private String email;
    private String cargo;
    private Rol rol;
}
