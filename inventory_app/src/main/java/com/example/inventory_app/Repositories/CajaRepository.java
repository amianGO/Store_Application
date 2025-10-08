package com.example.inventory_app.Repositories;

import com.example.inventory_app.Entities.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Caja.
 * Proporciona métodos para acceder y manipular datos de cajas en la base de datos.
 *
 * @author DamianG
 * @version 1.0
 */
@Repository
public interface CajaRepository extends JpaRepository<Caja, Long> {
    
    /**
     * Busca una caja por su número.
     * @param numeroCaja Número de la caja
     * @return Optional con la caja si existe
     */
    Optional<Caja> findByNumeroCaja(String numeroCaja);
    
    /**
     * Busca cajas por empleado.
     * @param empleadoId ID del empleado
     * @return Lista de cajas operadas por el empleado
     */
    List<Caja> findByEmpleadoId(Long empleadoId);
    
    /**
     * Busca cajas por estado.
     * @param estado Estado de la caja
     * @return Lista de cajas con el estado especificado
     */
    List<Caja> findByEstado(String estado);
    
    /**
     * Busca cajas por fecha de apertura.
     * @param fecha Fecha de apertura
     * @return Lista de cajas abiertas en la fecha especificada
     */
    @Query("SELECT c FROM Caja c WHERE DATE(c.fechaApertura) = DATE(:fecha)")
    List<Caja> findByFechaApertura(@Param("fecha") Date fecha);
    
    /**
     * Verifica si existe una caja abierta para un empleado.
     * @param empleadoId ID del empleado
     * @return true si existe una caja abierta, false si no
     */
    boolean existsByEmpleadoIdAndEstado(Long empleadoId, String estado);
}
