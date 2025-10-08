package com.example.inventory_app.Repositories;

import com.example.inventory_app.Entities.CarritoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad CarritoCompra.
 * Proporciona métodos para acceder y manipular datos de carritos de compra en la base de datos.
 *
 * @author DamianG
 * @version 1.0
 */
@Repository
public interface CarritoCompraRepository extends JpaRepository<CarritoCompra, Long> {
    
    /**
     * Busca carritos por cliente.
     * @param clienteId ID del cliente
     * @return Lista de carritos del cliente
     */
    List<CarritoCompra> findByClienteId(Long clienteId);
    
    /**
     * Busca carritos por empleado.
     * @param empleadoId ID del empleado
     * @return Lista de carritos gestionados por el empleado
     */
    List<CarritoCompra> findByEmpleadoId(Long empleadoId);
    
    /**
     * Busca carritos por estado.
     * @param estado Estado del carrito
     * @return Lista de carritos con el estado especificado
     */
    List<CarritoCompra> findByEstado(String estado);
    
    /**
     * Busca el carrito activo de un cliente.
     * @param clienteId ID del cliente
     * @param estado Estado activo del carrito
     * @return Optional con el carrito activo si existe
     */
    Optional<CarritoCompra> findByClienteIdAndEstado(Long clienteId, String estado);
    
    /**
     * Elimina los carritos abandonados por más de cierto tiempo.
     * @param fecha Fecha límite para considerar un carrito como abandonado
     * @return Número de carritos eliminados
     */
    Long deleteByFechaCreacionBeforeAndEstado(java.util.Date fecha, String estado);
}
