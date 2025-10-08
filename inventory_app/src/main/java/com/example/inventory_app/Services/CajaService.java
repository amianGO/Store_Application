package com.example.inventory_app.Services;

import com.example.inventory_app.Entities.Caja;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define los servicios disponibles para la gestión de cajas.
 *
 * @author DamianG
 * @version 1.0
 */
public interface CajaService {
    
    /**
     * Abre una nueva caja.
     * @param caja Caja a abrir
     * @return Caja abierta
     */
    Caja abrirCaja(Caja caja);
    
    /**
     * Cierra una caja.
     * @param id ID de la caja
     * @param montoFinal Monto final en caja
     * @param observaciones Observaciones del cierre
     * @return Caja cerrada
     */
    Caja cerrarCaja(Long id, BigDecimal montoFinal, String observaciones);
    
    /**
     * Busca una caja por su ID.
     * @param id ID de la caja
     * @return Optional con la caja si existe
     */
    Optional<Caja> findById(Long id);
    
    /**
     * Busca una caja por su número.
     * @param numeroCaja Número de la caja
     * @return Optional con la caja si existe
     */
    Optional<Caja> findByNumeroCaja(String numeroCaja);
    
    /**
     * Obtiene las cajas por empleado.
     * @param empleadoId ID del empleado
     * @return Lista de cajas del empleado
     */
    List<Caja> findByEmpleado(Long empleadoId);
    
    /**
     * Obtiene cajas por estado.
     * @param estado Estado de la caja
     * @return Lista de cajas con el estado
     */
    List<Caja> findByEstado(String estado);
    
    /**
     * Obtiene cajas por fecha de apertura.
     * @param fecha Fecha de apertura
     * @return Lista de cajas abiertas en la fecha
     */
    List<Caja> findByFechaApertura(Date fecha);
    
    /**
     * Verifica si un empleado tiene una caja abierta.
     * @param empleadoId ID del empleado
     * @return true si tiene caja abierta, false si no
     */
    boolean tieneCajaAbierta(Long empleadoId);
    
    /**
     * Actualiza el total de ventas de una caja.
     * @param id ID de la caja
     * @param montoVenta Monto de la venta a agregar
     * @return Caja actualizada
     */
    Caja actualizarTotalVentas(Long id, BigDecimal montoVenta);
}
