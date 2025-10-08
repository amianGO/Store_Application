package com.example.inventory_app.Services;

import com.example.inventory_app.Entities.Empleado;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define los servicios disponibles para la gestión de empleados.
 *
 * @author DamianG
 * @version 1.0
 */
public interface EmpleadoService {
    
    /**
     * Guarda un nuevo empleado o actualiza uno existente.
     * @param empleado Empleado a guardar
     * @return Empleado guardado
     */
    Empleado save(Empleado empleado);
    
    /**
     * Busca un empleado por su ID.
     * @param id ID del empleado
     * @return Optional con el empleado si existe
     */
    Optional<Empleado> findById(Long id);
    
    /**
     * Busca un empleado por su usuario.
     * @param usuario Nombre de usuario
     * @return Optional con el empleado si existe
     */
    Optional<Empleado> findByUsuario(String usuario);
    
    /**
     * Busca un empleado por su documento.
     * @param documento Documento del empleado
     * @return Optional con el empleado si existe
     */
    Optional<Empleado> findByDocumento(String documento);
    
    /**
     * Obtiene empleados por cargo.
     * @param cargo Cargo del empleado
     * @return Lista de empleados con el cargo
     */
    List<Empleado> findByCargo(String cargo);
    
    /**
     * Obtiene todos los empleados activos.
     * @return Lista de empleados activos
     */
    List<Empleado> findAllActive();
    
    /**
     * Desactiva un empleado.
     * @param id ID del empleado a desactivar
     */
    void deactivate(Long id);
    
    /**
     * Verifica credenciales de un empleado.
     * @param usuario Nombre de usuario
     * @param password Contraseña
     * @return Optional con el empleado si las credenciales son válidas
     */
    Optional<Empleado> verificarCredenciales(String usuario, String password);
}
