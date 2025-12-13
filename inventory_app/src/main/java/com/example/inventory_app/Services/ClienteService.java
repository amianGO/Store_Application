package com.example.inventory_app.Services;

import com.example.inventory_app.Entities.Cliente;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define los servicios disponibles para la gestión de clientes.
 *
 * @author DamianG
 * @version 1.0
 */
public interface ClienteService {
    
    /**
     * Guarda un nuevo cliente o actualiza uno existente.
     * @param cliente Cliente a guardar
     * @return Cliente guardado
     */
    Cliente save(Cliente cliente);
    
    /**
     * Busca un cliente por su ID.
     * @param id ID del cliente
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findById(Long id);
    
    /**
     * Busca un cliente por su documento.
     * @param documento Documento del cliente
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findByDocumento(String documento);
    
    /**
     * Busca un cliente por su email.
     * @param email Email del cliente
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findByEmail(String email);
    
    /**
     * Busca clientes por nombre.
     * @param busqueda Texto a buscar en el nombre
     * @return Lista de clientes que coinciden
     */
    List<Cliente> buscarPorNombre(String busqueda);
    
    /**
     * Busca clientes por ciudad.
     * @param ciudad Ciudad a buscar
     * @return Lista de clientes que coinciden
     */
    List<Cliente> findByCiudad(String ciudad);
    
    /**
     * Obtiene todos los clientes.
     * @return Lista de todos los clientes
     */
    List<Cliente> findAll();
    
    /**
     * Obtiene todos los clientes activos.
     * @return Lista de clientes activos
     */
    List<Cliente> findAllActive();
    
    /**
     * Elimina un cliente.
     * @param id ID del cliente a eliminar
     */
    void delete(Long id);
    
    /**
     * Desactiva un cliente.
     * @param id ID del cliente a desactivar
     */
    void deactivate(Long id);
}
