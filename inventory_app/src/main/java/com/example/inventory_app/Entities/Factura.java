package com.example.inventory_app.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad que representa una factura de venta en el sistema.
 * Esta clase maneja la información de las ventas realizadas a los clientes.
 *
 * @author DamianG
 * @version 1.0
 */
@Entity
@Table(name = "facturas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String numeroFactura;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(name = "fecha_emision")
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date fechaEmision;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal impuesto = BigDecimal.ZERO;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(length = 20)
    private String estado;

    @OneToMany(mappedBy = "factura", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleFactura> detalles = new ArrayList<>();

    /**
     * Genera un número de factura único al crear la factura.
     */
    @PrePersist
    public void prePersist() {
        if (this.numeroFactura == null) {
            this.numeroFactura = "FAC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
        if (this.fechaEmision == null) {
            this.fechaEmision = new java.util.Date();
        }
        if (this.estado == null) {
            this.estado = "PENDIENTE";
        }
    }

    /**
     * Método helper para agregar detalles a la factura.
     */
    public void addDetalle(DetalleFactura detalle) {
        detalles.add(detalle);
        detalle.setFactura(this);
    }

    /**
     * Calcula los totales de la factura.
     */
    public void calcularTotales() {
        this.subtotal = detalles.stream()
                .map(DetalleFactura::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Calcula el impuesto (por ejemplo, 19%)
        this.impuesto = this.subtotal.multiply(new BigDecimal("0.19"));
        this.total = this.subtotal.add(this.impuesto);
    }
}
