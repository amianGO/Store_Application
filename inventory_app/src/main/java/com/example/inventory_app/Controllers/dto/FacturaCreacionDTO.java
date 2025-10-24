package com.example.inventory_app.Controllers.dto;

import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
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
    @NotNull(message = "El ID del cliente es obligatorio")
    private Long clienteId;
    
    @NotNull(message = "El ID del empleado es obligatorio")
    private Long empleadoId;
    
    @NotEmpty(message = "Debe incluir al menos un detalle")
    @Valid
    private List<DetalleFacturaDTO> detalles;

    @Data
    public static class DetalleFacturaDTO {
        @NotNull(message = "El ID del producto es obligatorio")
        private Long productoId;
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;
    }
}
