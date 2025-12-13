package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Entidad CarritoCompra (schema: tenant).
 * Carrito temporal de compras por empleado.
 * Cada empresa gestiona sus propios carritos.
 * 
 * @author Sistema Multi-Tenant
 * @version 2.0
 */
@Entity
@Table(name = "carrito_compras", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"empleado_id", "producto_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarritoCompra implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "El empleado es obligatorio")
    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @NotNull(message = "El producto es obligatorio")
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad = 1;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    /**
     * Calcula el subtotal del item del carrito.
     */
    @Transient
    public BigDecimal getSubtotal() {
        if (precioUnitario == null || cantidad == null) {
            return BigDecimal.ZERO;
        }
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }
}
