package com.example.inventory_app.Repositories;

import com.example.inventory_app.Entities.CarritoCompra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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
     * Busca carritos por empleado.
     * @param empleadoId ID del empleado
     * @return Lista de carritos gestionados por el empleado
     */
    List<CarritoCompra> findByEmpleadoId(Long empleadoId);
    
    /**
     * Busca un carrito por empleado y producto.
     * @param empleadoId ID del empleado
     * @param productoId ID del producto
     * @return Optional con el carrito si existe
     */
    Optional<CarritoCompra> findByEmpleadoIdAndProductoId(Long empleadoId, Long productoId);
    
    /**
     * Elimina carritos por ID de empleado.
     * @param empleadoId ID del empleado
     */
    @Modifying
    @Query("DELETE FROM CarritoCompra c WHERE c.empleadoId = :empleadoId")
    void deleteByEmpleadoId(@Param("empleadoId") Long empleadoId);
    
    /**
     * Cuenta el número de carritos por ID de empleado.
     * @param empleadoId ID del empleado
     * @return Número de carritos del empleado
     */
    @Query("SELECT COUNT(c) FROM CarritoCompra c WHERE c.empleadoId = :empleadoId")
    Long countByEmpleadoId(@Param("empleadoId") Long empleadoId);
}
