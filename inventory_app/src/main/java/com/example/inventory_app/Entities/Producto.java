package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * Entidad que representa un producto en el sistema.
 * Esta clase maneja la información de los productos disponibles para la venta.
 *
 * @author DamianG
 * @version 1.0
 */
@Entity
@Table(name = "productos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Producto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código es obligatorio")
    @Size(max = 50, message = "El código no puede exceder 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String codigo;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(nullable = false, length = 100)
    private String nombre;

    @Size(max = 500, message = "La descripción no puede exceder 500 caracteres")
    @Column(length = 500)
    private String descripcion;

    @NotNull(message = "El precio de compra es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de compra debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioCompra;

    @NotNull(message = "El precio de venta es obligatorio")
    @DecimalMin(value = "0.0", inclusive = false, message = "El precio de venta debe ser mayor a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal precioVenta;

    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock no puede ser negativo")
    @Column(nullable = false)
    private Integer stock;

    @Min(value = 0, message = "El stock mínimo no puede ser negativo")
    @Column(name = "stock_minimo")
    private Integer stockMinimo;

    @Column(name = "fecha_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date fechaRegistro;

    @Column(name = "estado_activo")
    private boolean estadoActivo = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "categoria", nullable = false)
    private CategoriaProducto categoria;
}
