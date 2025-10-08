package com.example.inventory_app.Controllers.dto;

import lombok.Data;
import java.util.List;

/**
 * DTO para la creación de facturas.
 *
 * @author DamianG
 * @version 1.0
 */
@Data
public class FacturaCreacionDTO {
    private Long clienteId;
    private Long empleadoId;
    private List<DetalleFacturaDTO> detalles;

    @Data
    public static class DetalleFacturaDTO {
        private Long productoId;
        private Integer cantidad;
    }
}
