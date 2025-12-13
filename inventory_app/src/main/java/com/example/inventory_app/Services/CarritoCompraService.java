package com.example.inventory_app.Services;

import com.example.inventory_app.Entities.CarritoCompra;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Servicio para gestión de Carrito de Compras multi-tenant.
 */
public interface CarritoCompraService {
    
    CarritoCompra agregarProducto(Long empleadoId, Long productoId, Integer cantidad);
    
    CarritoCompra actualizarCantidad(Long id, Integer cantidad);
    
    void eliminarItem(Long id);
    
    void vaciarCarrito(Long empleadoId);
    
    List<CarritoCompra> obtenerCarritoPorEmpleado(Long empleadoId);
    
    Optional<CarritoCompra> findById(Long id);
    
    BigDecimal calcularTotalCarrito(Long empleadoId);
    
    Long contarItems(Long empleadoId);
}
