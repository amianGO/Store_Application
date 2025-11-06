package com.example.inventory_app.Controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
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
    @JsonProperty(value = "clienteId", required = true)
    private Long clienteId;
    
    @NotNull(message = "El ID del empleado es obligatorio")
    @JsonProperty(value = "empleadoId", required = true)
    private Long empleadoId;
    
    @NotEmpty(message = "Debe incluir al menos un detalle")
    @Valid
    @JsonProperty(value = "detalles")
    @JsonAlias({"detalle", "items"})
    private List<DetalleFacturaDTO> detalles;

    @Data
    public static class DetalleFacturaDTO {
        @NotNull(message = "El ID del producto es obligatorio")
        @JsonProperty(value = "productoId", required = true)
        private Long productoId;
        
        @NotNull(message = "La cantidad es obligatoria")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        @JsonProperty(required = true)
        private Integer cantidad;
    }
}
