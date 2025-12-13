package com.example.inventory_app.Controllers.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * DTO para creación de facturas.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaCreacionDTO {

    @NotNull(message = "El cliente es obligatorio")
    private Long clienteId;

    @NotNull(message = "El empleado es obligatorio")
    private Long empleadoId;

    private String metodoPago;

    private BigDecimal impuesto;

    private BigDecimal descuento;

    private String notas;

    @NotEmpty(message = "Debe agregar al menos un producto")
    private List<DetalleFacturaDTO> detalles;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DetalleFacturaDTO {

        @NotNull(message = "El producto es obligatorio")
        private Long productoId;

        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        private Integer cantidad;

        @DecimalMin(value = "0.0", message = "El descuento debe ser mayor o igual a 0")
        private BigDecimal descuento;
    }
}
