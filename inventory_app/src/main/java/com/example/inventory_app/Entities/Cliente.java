package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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

    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    @Column(length = 15)
    private String telefono;

    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(length = 100)
    private String email;

    @Size(max = 200, message = "La dirección no puede exceder 200 caracteres")
    @Column(length = 200)
    private String direccion;

    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date fechaRegistro;

    @Column(name = "estado_activo")
    private boolean estadoActivo = true;
}
