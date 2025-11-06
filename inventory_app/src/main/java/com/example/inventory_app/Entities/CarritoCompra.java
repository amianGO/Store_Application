package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad que representa el carrito de compras en el sistema.
 * Esta clase maneja la información temporal de los productos seleccionados antes de generar una factura.
 *
 * @author DamianG
 * @version 1.0
 */
@Entity
@Table(name = "carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoCompra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "fecha_creacion")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date fechaCreacion;

    @Column(precision = 10, scale = 2)
    private BigDecimal totalEstimado;

    @Column(length = 20)
    private String estado;

    @OneToMany(mappedBy = "carritoCompra", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleCarrito> items = new ArrayList<>();

    public void addDetalle(DetalleCarrito detalle) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.add(detalle);
        detalle.setCarritoCompra(this);
        
        // Actualizar el total estimado
        if (totalEstimado == null) {
            totalEstimado = BigDecimal.ZERO;
        }
        totalEstimado = totalEstimado.add(detalle.getSubtotal());
    }
}
