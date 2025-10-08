package com.example.inventory_app.Entities;

import jakarta.persistence.*;
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

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @Column(nullable = false, unique = true, length = 50)
    private String usuario;

    @Column(nullable = false)
    private String password;

    @Column(length = 15)
    private String telefono;

    @Column(length = 100)
    private String email;

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
