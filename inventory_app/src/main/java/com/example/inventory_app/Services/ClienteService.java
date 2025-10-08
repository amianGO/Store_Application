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
     * Busca clientes por nombre o apellido.
     * @param texto Texto a buscar
     * @return Lista de clientes que coinciden
     */
    List<Cliente> findByNombreOrApellido(String texto);
    
    /**
     * Obtiene todos los clientes activos.
     * @return Lista de clientes activos
     */
    List<Cliente> findAllActive();
    
    /**
     * Desactiva un cliente.
     * @param id ID del cliente a desactivar
     */
    void deactivate(Long id);
    
    /**
     * Verifica si existe un cliente con el documento dado.
     * @param documento Documento a verificar
     * @return true si existe, false si no
     */
    boolean existsByDocumento(String documento);
}
