package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Entidad que representa el detalle de un carrito de compras en el sistema.
 * Esta clase maneja la información de los productos seleccionados en un carrito.
 *
 * @author DamianG
 * @version 1.0
 */
@Entity
@Table(name = "detalle_carritos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleCarrito {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "carrito_id", nullable = false)
    private CarritoCompra carritoCompra;

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

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @PrePersist
    @PreUpdate
    public void calcularSubtotal() {
        if (this.cantidad != null && this.precioUnitario != null) {
            this.subtotal = this.precioUnitario.multiply(new BigDecimal(this.cantidad));
        }
    }
    
    /**
     * Constructor para crear detalle desde un producto
     */
    public DetalleCarrito(Producto producto, Integer cantidad) {
        this.productoCodigo = producto.getCodigo();
        this.productoNombre = producto.getNombre();
        this.productoCategoria = producto.getCategoria() != null ? producto.getCategoria().toString() : "";
        this.cantidad = cantidad;
        this.precioUnitario = producto.getPrecioVenta();
        calcularSubtotal();
    }
}
