package com.example.inventory_app.Repositories;

import com.example.inventory_app.Entities.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Empleado.
 * Proporciona métodos para acceder y manipular datos de empleados en la base de datos.
 *
 * @author DamianG
 * @version 1.0
 */
@Repository
public interface EmpleadoRepository extends JpaRepository<Empleado, Long> {
    
    /**
     * Busca un empleado por su nombre de usuario.
     * @param usuario Nombre de usuario del empleado
     * @return Optional con el empleado si existe
     */
    Optional<Empleado> findByUsuario(String usuario);
    
    /**
     * Busca un empleado por su número de documento.
     * @param documento Número de documento del empleado
     * @return Optional con el empleado si existe
     */
    Optional<Empleado> findByDocumento(String documento);
    
    /**
     * Busca empleados por cargo.
     * @param cargo Cargo del empleado
     * @return Lista de empleados con el cargo especificado
     */
    List<Empleado> findByCargo(String cargo);
    
    /**
     * Busca empleados activos en el sistema.
     * @return Lista de empleados activos
     */
    List<Empleado> findByEstadoActivoTrue();
    
    /**
     * Verifica si existe un empleado con el usuario proporcionado.
     * @param usuario Nombre de usuario a verificar
     * @return true si existe, false si no
     */
    boolean existsByUsuario(String usuario);
    
    /**
     * Verifica si existe un empleado con el documento proporcionado.
     * @param documento Número de documento a verificar
     * @return true si existe, false si no
     */
    boolean existsByDocumento(String documento);
}
