package com.example.inventory_app.Entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Entidad DetalleFactura (schema: tenant).
 * Representa cada línea/item de una factura.
 * 
 * @author Sistema Multi-Tenant
 * @version 2.0
 */
@Entity
@Table(name = "detalle_facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFactura implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "factura_id", nullable = false)
    @JsonIgnore
    private Factura factura;

    @Column(name = "producto_id")
    private Long productoId;

    @Column(name = "producto_codigo", length = 50)
    private String productoCodigo;

    @NotBlank(message = "El nombre del producto es obligatorio")
    @Column(name = "producto_nombre", nullable = false, length = 100)
    private String productoNombre;

    @Column(name = "producto_categoria", length = 50)
    private String productoCategoria;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 1, message = "La cantidad debe ser al menos 1")
    @Column(nullable = false)
    private Integer cantidad;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio debe ser mayor a 0")
    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;

    @DecimalMin(value = "0.0", message = "El descuento debe ser mayor o igual a 0")
    @Column(precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    /**
     * Calcula el subtotal: (precio * cantidad) - descuento
     */
    public void calcularSubtotal() {
        if (precioUnitario != null && cantidad != null) {
            BigDecimal totalSinDescuento = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            BigDecimal descuentoAplicado = descuento != null ? descuento : BigDecimal.ZERO;
            
            // Validar que el descuento no sea mayor al subtotal
            if (descuentoAplicado.compareTo(totalSinDescuento) > 0) {
                throw new RuntimeException(
                    "El descuento (" + descuentoAplicado + ") no puede ser mayor al subtotal del producto (" + totalSinDescuento + ")"
                );
            }
            
            this.subtotal = totalSinDescuento.subtract(descuentoAplicado);
        } else {
            this.subtotal = BigDecimal.ZERO;
        }
    }

    @PrePersist
    protected void onSave() {
        calcularSubtotal();
    }
    
    @PreUpdate
    protected void onUpdate() {
        calcularSubtotal();
    }
}
