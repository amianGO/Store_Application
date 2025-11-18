package com.example.inventory_app.Controllers.dto;

import com.example.inventory_app.Entities.DetalleFactura;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para mostrar información de factura con fecha formateada.
 *
 * @author DamianG
 * @version 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponseDTO {
    
    private Long id;
    private String numeroFactura;
    private Long clienteId;
    private String clienteNombre;
    private Long empleadoId;
    private String empleadoNombre;
    private String fechaEmision; // Fecha formateada como String
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal total;
    private String estado;
    private List<DetalleFactura> detalles;
    
    /**
     * Constructor que toma una fecha LocalDateTime y la formatea
     */
    public static FacturaResponseDTO fromEntity(com.example.inventory_app.Entities.Factura factura) {
        FacturaResponseDTO dto = new FacturaResponseDTO();
        dto.setId(factura.getId());
        dto.setNumeroFactura(factura.getNumeroFactura());
        dto.setClienteId(factura.getClienteId());
        dto.setClienteNombre(factura.getClienteNombre());
        dto.setEmpleadoId(factura.getEmpleadoId());
        dto.setEmpleadoNombre(factura.getEmpleadoNombre());
        
        // Formatear fecha manualmente para evitar problemas de serialización
        if (factura.getFechaEmision() != null) {
            java.time.format.DateTimeFormatter formatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
            dto.setFechaEmision(factura.getFechaEmision().format(formatter));
        } else {
            dto.setFechaEmision(java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        }
        
        dto.setSubtotal(factura.getSubtotal());
        dto.setImpuesto(factura.getImpuesto());
        dto.setTotal(factura.getTotal());
        dto.setEstado(factura.getEstado());
        dto.setDetalles(factura.getDetalles());
        
        return dto;
    }
}
