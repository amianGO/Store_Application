package com.example.inventory_app.Repositories;

import com.example.inventory_app.Entities.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Factura.
 * Proporciona métodos para acceder y manipular datos de facturas en la base de datos.
 *
 * @author DamianG
 * @version 1.0
 */
@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    
    /**
     * Busca una factura por su número.
     * @param numeroFactura Número de la factura
     * @return Optional con la factura si existe
     */
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    /**
     * Busca facturas por cliente.
     * @param clienteId ID del cliente
     * @return Lista de facturas del cliente
     */
    List<Factura> findByClienteId(Long clienteId);
    
    /**
     * Busca facturas por empleado.
     * @param empleadoId ID del empleado
     * @return Lista de facturas generadas por el empleado
     */
    List<Factura> findByEmpleadoId(Long empleadoId);
    
    /**
     * Busca facturas por rango de fechas.
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de facturas dentro del rango de fechas
     */
    List<Factura> findByFechaEmisionBetween(Date fechaInicio, Date fechaFin);
    
    /**
     * Busca facturas por estado.
     * @param estado Estado de la factura
     * @return Lista de facturas con el estado especificado
     */
    List<Factura> findByEstado(String estado);
    
    /**
     * Calcula el total de ventas por día.
     * @param fecha Fecha para calcular el total
     * @return Total de ventas del día
     */
    @Query("SELECT SUM(f.total) FROM Factura f WHERE DATE(f.fechaEmision) = DATE(:fecha) AND f.estado = 'COMPLETADA'")
    Optional<Double> calcularTotalVentasPorDia(@Param("fecha") Date fecha);
}
