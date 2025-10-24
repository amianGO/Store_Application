package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.example.inventory_app.Config.Rol;

/**
 * Entidad que representa a un empleado en el sistema.
 * Esta clase maneja la información del personal que opera el sistema de ventas.
 *
 * @author DamianG
 * @version 1.0
 */
@Entity
@Table(name = "empleados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 100, message = "El apellido no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String apellido;

    @NotBlank(message = "El documento es obligatorio")
    @Size(max = 20, message = "El documento no puede exceder 20 caracteres")
    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @NotBlank(message = "El usuario es obligatorio")
    @Size(max = 50, message = "El usuario no puede exceder 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    @Column(nullable = false)
    private String password;

    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    @Column(length = 15)
    private String telefono;

    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(length = 100)
    private String email;

    @NotBlank(message = "El cargo es obligatorio")
    @Size(max = 50, message = "El cargo no puede exceder 50 caracteres")
    @Column(nullable = false, length = 50)
    private String cargo;

    @Column(name = "fecha_contratacion")
    @Temporal(TemporalType.DATE)
    private java.util.Date fechaContratacion;

    @Column(name = "estado_activo")
    private boolean estadoActivo = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;
}
