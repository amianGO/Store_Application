package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entidad que representa a un cliente en el sistema.
 * Esta clase maneja la información básica de los clientes que realizan compras.
 *
 * @author DamianG
 * @version 1.0
 */
@Entity
@Table(name = "clientes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(nullable = false, unique = true, length = 20)
    private String documento;

    @Column(length = 15)
    private String telefono;

    @Column(length = 100)
    private String email;

    @Column(length = 200)
    private String direccion;

    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date fechaRegistro;

    @Column(name = "estado_activo")
    private boolean estadoActivo = true;
}
