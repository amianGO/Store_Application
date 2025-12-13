package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entidad Factura (schema: tenant).
 * Representa una venta/factura de la empresa.
 * 
 * @author Sistema Multi-Tenant
 * @version 2.0
 */
@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de factura es obligatorio")
    @Column(name = "numero_factura", unique = true, nullable = false, length = 50)
    private String numeroFactura;

    @NotNull(message = "El cliente es obligatorio")
    @Column(name = "cliente_id", nullable = false)
    private Long clienteId;

    @NotNull(message = "El empleado es obligatorio")
    @Column(name = "empleado_id", nullable = false)
    private Long empleadoId;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date fecha;

    @DecimalMin(value = "0.0", message = "El subtotal debe ser mayor o igual a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El impuesto debe ser mayor o igual a 0")
    @Column(precision = 10, scale = 2)
    private BigDecimal impuesto = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El descuento debe ser mayor o igual a 0")
    @Column(precision = 10, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @DecimalMin(value = "0.0", message = "El total debe ser mayor o igual a 0")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago;

    @Column(length = 20)
    private String estado = "COMPLETADA";

    @Column(columnDefinition = "TEXT")
    private String notas;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<DetalleFactura> detalles = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fecha = new Date();
        this.createdAt = new Date();
        this.updatedAt = new Date();
        
        // Generar número de factura si no existe
        if (this.numeroFactura == null || this.numeroFactura.isEmpty()) {
            this.numeroFactura = generarNumeroFactura();
        }
        
        // IMPORTANTE: Calcular totales ANTES de validar
        calcularTotales();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = new Date();
    }

    /**
     * Agregar detalle a la factura y establecer la relación bidireccional.
     */
    public void addDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
    }

    /**
     * Calcular totales de la factura basándose en los detalles.
     */
    public void calcularTotales() {
        System.out.println("========== CALCULANDO TOTALES ==========");
        System.out.println("Cantidad de detalles: " + (detalles != null ? detalles.size() : 0));
        
        if (detalles != null && !detalles.isEmpty()) {
            for (DetalleFactura detalle : detalles) {
                System.out.println("  - Detalle: " + detalle.getProductoNombre());
                System.out.println("    Cantidad: " + detalle.getCantidad());
                System.out.println("    Precio unitario: " + detalle.getPrecioUnitario());
                System.out.println("    Descuento: " + detalle.getDescuento());
                System.out.println("    Subtotal detalle: " + detalle.getSubtotal());
            }
        }
        
        this.subtotal = detalles != null ? detalles.stream()
            .map(DetalleFactura::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add) : BigDecimal.ZERO;
        
        System.out.println("Subtotal calculado: " + this.subtotal);
        System.out.println("Impuesto: " + this.impuesto);
        System.out.println("Descuento: " + this.descuento);
        
        // Total = Subtotal + Impuesto - Descuento
        this.total = subtotal.add(impuesto != null ? impuesto : BigDecimal.ZERO)
                             .subtract(descuento != null ? descuento : BigDecimal.ZERO);
        
        System.out.println("Total calculado: " + this.total);
        System.out.println("========================================");
    }

    /**
     * Genera un número de factura único basado en timestamp.
     */
    private String generarNumeroFactura() {
        return "FAC-" + System.currentTimeMillis();
    }
}
