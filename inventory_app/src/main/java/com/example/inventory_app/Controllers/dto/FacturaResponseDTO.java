package com.example.inventory_app.Controllers.dto;

import com.example.inventory_app.Entities.DetalleFactura;
import com.example.inventory_app.Entities.Factura;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * DTO para respuesta de facturas con información completa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaResponseDTO {

    private Long id;
    private String numeroFactura;
    private Long clienteId;
    private Long empleadoId;
    private Date fecha;
    private BigDecimal subtotal;
    private BigDecimal impuesto;
    private BigDecimal descuento;
    private BigDecimal total;
    private String metodoPago;
    private String estado;
    private String notas;
    private List<DetalleResponseDTO> detalles;
    private Date createdAt;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleResponseDTO {
        private Long id;
        private Long productoId;
        private String productoCodigo;
        private String productoNombre;
        private String productoCategoria;
        private Integer cantidad;
        private BigDecimal precioUnitario;
        private BigDecimal descuento;
        private BigDecimal subtotal;
    }

    /**
     * Convierte una entidad Factura a DTO.
     */
    public static FacturaResponseDTO fromEntity(Factura factura) {
        FacturaResponseDTO dto = new FacturaResponseDTO();
        dto.setId(factura.getId());
        dto.setNumeroFactura(factura.getNumeroFactura());
        dto.setClienteId(factura.getClienteId());
        dto.setEmpleadoId(factura.getEmpleadoId());
        dto.setFecha(factura.getFecha());
        dto.setSubtotal(factura.getSubtotal());
        dto.setImpuesto(factura.getImpuesto());
        dto.setDescuento(factura.getDescuento());
        dto.setTotal(factura.getTotal());
        dto.setMetodoPago(factura.getMetodoPago());
        dto.setEstado(factura.getEstado());
        dto.setNotas(factura.getNotas());
        dto.setCreatedAt(factura.getCreatedAt());

        if (factura.getDetalles() != null) {
            dto.setDetalles(factura.getDetalles().stream()
                .map(FacturaResponseDTO::detalleFromEntity)
                .collect(Collectors.toList()));
        }

        return dto;
    }

    private static DetalleResponseDTO detalleFromEntity(DetalleFactura detalle) {
        DetalleResponseDTO dto = new DetalleResponseDTO();
        dto.setId(detalle.getId());
        dto.setProductoId(detalle.getProductoId());
        dto.setProductoCodigo(detalle.getProductoCodigo());
        dto.setProductoNombre(detalle.getProductoNombre());
        dto.setProductoCategoria(detalle.getProductoCategoria());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setDescuento(detalle.getDescuento());
        dto.setSubtotal(detalle.getSubtotal());
        return dto;
    }
}
