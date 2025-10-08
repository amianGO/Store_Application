package com.example.inventory_app.Repositories;

import com.example.inventory_app.Entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Cliente.
 * Proporciona métodos para acceder y manipular datos de clientes en la base de datos.
 *
 * @author DamianG
 * @version 1.0
 */
@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    /**
     * Busca un cliente por su número de documento.
     * @param documento Número de documento del cliente
     * @return Optional con el cliente si existe
     */
    Optional<Cliente> findByDocumento(String documento);
    
    /**
     * Busca clientes por nombre o apellido que contengan el texto proporcionado.
     * @param texto Texto a buscar en nombre o apellido
     * @return Lista de clientes que coinciden con la búsqueda
     */
    List<Cliente> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String texto, String texto2);
    
    /**
     * Busca clientes activos en el sistema.
     * @return Lista de clientes activos
     */
    List<Cliente> findByEstadoActivoTrue();
    
    /**
     * Verifica si existe un cliente con el documento proporcionado.
     * @param documento Número de documento a verificar
     * @return true si existe, false si no
     */
    boolean existsByDocumento(String documento);
}
