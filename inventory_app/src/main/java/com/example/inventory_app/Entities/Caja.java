package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Entidad que representa la caja o punto de venta en el sistema.
 * Esta clase maneja la información de las operaciones de caja durante un turno.
 *
 * @author DamianG
 * @version 1.0
 */
@Entity
@Table(name = "cajas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Caja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numeroCaja;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "fecha_apertura")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date fechaApertura;

    @Column(name = "fecha_cierre")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date fechaCierre;

    @Column(name = "monto_inicial", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoInicial;

    @Column(name = "monto_final", precision = 10, scale = 2)
    private BigDecimal montoFinal;

    @Column(name = "total_ventas", precision = 10, scale = 2)
    private BigDecimal totalVentas;

    @Column(length = 20)
    private String estado;

    @Column(length = 500)
    private String observaciones;
}
