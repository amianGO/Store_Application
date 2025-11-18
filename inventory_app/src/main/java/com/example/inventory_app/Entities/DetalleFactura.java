package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Entidad que representa el detalle de una factura en el sistema.
 * Esta clase maneja la información de los productos incluidos en una factura.
 *
 * @author DamianG
 * @version 1.0
 */
@Entity
@Table(name = "detalle_facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleFactura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Almacenar solo el ID de la factura, sin referencia FK
    @Column(name = "factura_id", nullable = false)
    private Long facturaId;

    // ID del producto (requerido por la base de datos)
    @Column(name = "producto_id", nullable = false)
    private Long productoId;

    // Datos del producto almacenados directamente (no referencia)
    @Column(name = "producto_codigo", nullable = false, length = 50)
    private String productoCodigo;
    
    @Column(name = "producto_nombre", nullable = false, length = 100)
    private String productoNombre;
    
    @Column(name = "producto_categoria", length = 50)
    private String productoCategoria;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(name = "precio_unitario", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioUnitario;
    
    @Column(name = "descuento", precision = 5, scale = 2, columnDefinition = "DECIMAL(5,2) DEFAULT 0.00")
    private BigDecimal descuento = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    /**
     * Calcula el subtotal del detalle aplicando descuento.
     */
    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (this.cantidad != null && this.precioUnitario != null) {
            BigDecimal subtotalSinDescuento = this.precioUnitario.multiply(BigDecimal.valueOf(this.cantidad));
            if (this.descuento != null && this.descuento.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal valorDescuento = subtotalSinDescuento.multiply(this.descuento.divide(BigDecimal.valueOf(100)));
                this.subtotal = subtotalSinDescuento.subtract(valorDescuento);
            } else {
                this.subtotal = subtotalSinDescuento;
            }
        }
    }
    
    /**
     * Constructor para crear detalle desde un producto
     */
    public DetalleFactura(Producto producto, Integer cantidad, BigDecimal descuento) {
        this.productoId = producto.getId();
        this.productoCodigo = producto.getCodigo();
        this.productoNombre = producto.getNombre();
        this.productoCategoria = producto.getCategoria() != null ? producto.getCategoria().toString() : "";
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioVenta();
        this.descuento = descuento != null ? descuento : BigDecimal.ZERO;
        calcularSubtotal();
    }
}
