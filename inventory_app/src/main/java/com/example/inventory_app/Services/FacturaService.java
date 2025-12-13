package com.example.inventory_app.Services;

import com.example.inventory_app.Entities.Factura;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define los servicios disponibles para la gestión de facturas.
 *
 * @author DamianG
 * @version 1.0
 */
public interface FacturaService {
    
    /**
     * Crea una nueva factura.
     * @param factura Factura a crear
     * @return Factura creada
     */
    Factura create(Factura factura);
    
    /**
     * Busca una factura por su ID.
     * @param id ID de la factura
     * @return Optional con la factura si existe
     */
    Optional<Factura> findById(Long id);
    
    /**
     * Busca una factura por su número.
     * @param numeroFactura Número de la factura
     * @return Optional con la factura si existe
     */
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    
    /**
     * Obtiene todas las facturas.
     * @return Lista de todas las facturas
     */
    List<Factura> findAll();
    
    /**
     * Obtiene las facturas de un cliente.
     * @param clienteId ID del cliente
     * @return Lista de facturas del cliente
     */
    List<Factura> findByCliente(Long clienteId);
    
    /**
     * Obtiene las facturas generadas por un empleado.
     * @param empleadoId ID del empleado
     * @return Lista de facturas del empleado
     */
    List<Factura> findByEmpleado(Long empleadoId);
    
    /**
     * Obtiene facturas por estado.
     * @param estado Estado de la factura
     * @return Lista de facturas con el estado
     */
    List<Factura> findByEstado(String estado);
    
    /**
     * Obtiene facturas por rango de fechas.
     * @param fechaInicio Fecha inicial
     * @param fechaFin Fecha final
     * @return Lista de facturas en el rango
     */
    List<Factura> findByRangoFechas(Date fechaInicio, Date fechaFin);
    
    /**
     * Calcula el total de ventas de un día.
     * @param fecha Fecha para calcular
     * @return Total de ventas
     */
    Double calcularTotalVentasDia(Date fecha);
    
    /**
     * Anula una factura.
     * @param id ID de la factura a anular
     */
    void anularFactura(Long id);
    
    /**
     * Elimina una factura.
     * @param id ID de la factura a eliminar
     */
    void delete(Long id);
}
