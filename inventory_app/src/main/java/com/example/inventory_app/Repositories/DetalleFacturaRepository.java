package com.example.inventory_app.Repositories;

import com.example.inventory_app.Entities.DetalleFactura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la entidad DetalleFactura.
 * Proporciona métodos para acceder y manipular datos de detalles de facturas.
 *
 * @author DamianG
 * @version 1.0
 */
@Repository
public interface DetalleFacturaRepository extends JpaRepository<DetalleFactura, Long> {
    
    /**
     * Busca detalles por ID de factura.
     * @param facturaId ID de la factura
     * @return Lista de detalles de la factura
     */
    List<DetalleFactura> findByFacturaId(Long facturaId);
    
    /**
     * Elimina detalles por ID de factura.
     * @param facturaId ID de la factura
     */
    void deleteByFacturaId(Long facturaId);
}
