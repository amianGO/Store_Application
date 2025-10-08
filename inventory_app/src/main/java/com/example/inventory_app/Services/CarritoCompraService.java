package com.example.inventory_app.Services;

import com.example.inventory_app.Entities.CarritoCompra;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define los servicios disponibles para la gestión de carritos de compra.
 *
 * @author DamianG
 * @version 1.0
 */
public interface CarritoCompraService {
    
    /**
     * Crea un nuevo carrito de compra.
     * @param carritoCompra Carrito a crear
     * @return Carrito creado
     */
    CarritoCompra create(CarritoCompra carritoCompra);
    
    /**
     * Busca un carrito por su ID.
     * @param id ID del carrito
     * @return Optional con el carrito si existe
     */
    Optional<CarritoCompra> findById(Long id);
    
    /**
     * Obtiene los carritos de un cliente.
     * @param clienteId ID del cliente
     * @return Lista de carritos del cliente
     */
    List<CarritoCompra> findByCliente(Long clienteId);
    
    /**
     * Obtiene los carritos gestionados por un empleado.
     * @param empleadoId ID del empleado
     * @return Lista de carritos del empleado
     */
    List<CarritoCompra> findByEmpleado(Long empleadoId);
    
    /**
     * Obtiene el carrito activo de un cliente.
     * @param clienteId ID del cliente
     * @return Optional con el carrito activo si existe
     */
    Optional<CarritoCompra> findCarritoActivo(Long clienteId);
    
    /**
     * Actualiza el total estimado del carrito.
     * @param id ID del carrito
     * @return Carrito actualizado
     */
    CarritoCompra actualizarTotal(Long id);
    
    /**
     * Vacía un carrito de compra.
     * @param id ID del carrito
     */
    void vaciarCarrito(Long id);
    
    /**
     * Marca un carrito como completado.
     * @param id ID del carrito
     */
    void completarCarrito(Long id);
    
    /**
     * Elimina carritos abandonados.
     * @param horasInactivo Horas de inactividad para considerar abandonado
     * @return Número de carritos eliminados
     */
    Long eliminarCarritosAbandonados(int horasInactivo);
}
