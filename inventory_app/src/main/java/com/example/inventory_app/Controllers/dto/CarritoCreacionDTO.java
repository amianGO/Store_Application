package com.example.inventory_app.Controllers.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Min;
import jakarta.validation.Valid;
import java.util.List;

public class CarritoCreacionDTO {
    @NotNull(message = "El ID del cliente es requerido")
    @JsonProperty(value = "clienteId", required = true)
    private Long clienteId;
    
    @NotNull(message = "El ID del empleado es requerido")
    @JsonProperty(value = "empleadoId", required = true)
    private Long empleadoId;
    
    @NotEmpty(message = "Debe incluir al menos un detalle")
    @Valid
    @JsonProperty(value = "detalles")
    @JsonAlias({"detalle", "items"})
    private List<DetalleCarritoDTO> items;

    // Inner class for cart items
    public static class DetalleCarritoDTO {
        @NotNull(message = "El ID del producto es requerido")
        @JsonProperty(value = "productoId", required = true)
        private Long productoId;
        
        @NotNull(message = "La cantidad es requerida")
        @Min(value = 1, message = "La cantidad debe ser mayor a 0")
        private Integer cantidad;

        // Getters and Setters
        public Long getProductoId() {
            return productoId;
        }

        public void setProductoId(Long productoId) {
            this.productoId = productoId;
        }

        public Integer getCantidad() {
            return cantidad;
        }

        public void setCantidad(Integer cantidad) {
            this.cantidad = cantidad;
        }
    }

    // Getters and Setters
    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public Long getEmpleadoId() {
        return empleadoId;
    }

    public void setEmpleadoId(Long empleadoId) {
        this.empleadoId = empleadoId;
    }

    public List<DetalleCarritoDTO> getItems() {
        return items;
    }

    public void setItems(List<DetalleCarritoDTO> items) {
        this.items = items;
    }
}
