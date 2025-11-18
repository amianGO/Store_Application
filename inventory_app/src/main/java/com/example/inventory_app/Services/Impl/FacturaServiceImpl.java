package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.Factura;
import com.example.inventory_app.Repositories.FacturaRepository;
import com.example.inventory_app.Repositories.DetalleFacturaRepository;
import com.example.inventory_app.Services.FacturaService;
import com.example.inventory_app.Services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de Factura.
 *
 * @author DamianG
 * @version 1.0
 */
@Service
@Transactional
public class FacturaServiceImpl implements FacturaService {

    @Autowired
    private FacturaRepository facturaRepository;

    @Autowired
    private DetalleFacturaRepository detalleFacturaRepository;

    @Autowired
    private ProductoService productoService;

    @Override
    @Transactional(readOnly = true)
    public List<Factura> findAll() {
        List<Factura> facturas = facturaRepository.findAll();
        // Cargar detalles para cada factura
        facturas.forEach(factura -> {
            factura.setDetalles(detalleFacturaRepository.findByFacturaId(factura.getId()));
        });
        return facturas;
    }

    @Override
    public Factura create(Factura factura) {
        factura.setFechaEmision(LocalDateTime.now());
        factura.setEstado("COMPLETADA");
        
        // Calcular totales antes de guardar
        factura.calcularTotales();
        
        // Guardar primero la factura para obtener el ID
        Factura facturaGuardada = facturaRepository.save(factura);
        
        // Guardar los detalles con el ID de la factura
        factura.getDetalles().forEach(detalle -> {
            detalle.setFacturaId(facturaGuardada.getId());
            detalleFacturaRepository.save(detalle);
            
            // Actualizar stock de productos usando el código del producto
            productoService.findByCodigo(detalle.getProductoCodigo())
                .ifPresent(producto -> {
                    productoService.actualizarStock(
                        producto.getId(),
                        -detalle.getCantidad()
                    );
                });
        });
        
        // Cargar los detalles en la factura guardada
        facturaGuardada.setDetalles(detalleFacturaRepository.findByFacturaId(facturaGuardada.getId()));
        
        return facturaGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Factura> findById(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        Optional<Factura> facturaOpt = facturaRepository.findById(id);
        if (facturaOpt.isPresent()) {
            Factura factura = facturaOpt.get();
            factura.setDetalles(detalleFacturaRepository.findByFacturaId(factura.getId()));
        }
        return facturaOpt;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Factura> findByNumeroFactura(String numeroFactura) {
        return facturaRepository.findByNumeroFactura(numeroFactura);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> findByCliente(Long clienteId) {
        return facturaRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> findByEmpleado(Long empleadoId) {
        return facturaRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> findByRangoFechas(Date fechaInicio, Date fechaFin) {
        LocalDateTime inicio = fechaInicio.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime fin = fechaFin.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
        return facturaRepository.findByFechaEmisionBetween(inicio, fin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> findByEstado(String estado) {
        return facturaRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalVentasDia(Date fecha) {
        return facturaRepository.calcularTotalVentasPorDia(fecha)
            .orElse(0.0);
    }

    @Override
    public void anularFactura(Long id) {
        if (id == null) {
            return;
        }
        facturaRepository.findById(id).ifPresent(factura -> {
            if ("COMPLETADA".equals(factura.getEstado())) {
                // Restaurar stock de productos usando el código del producto
                factura.getDetalles().forEach(detalle -> {
                    productoService.findByCodigo(detalle.getProductoCodigo())
                        .ifPresent(producto -> {
                            productoService.actualizarStock(
                                producto.getId(),
                                detalle.getCantidad()
                            );
                        });
                });
                
                factura.setEstado("ANULADA");
                facturaRepository.save(factura);
            }
        });
    }
    
    @Override
    @Transactional
    public void delete(Long id) {
        if (id != null && facturaRepository.existsById(id)) {
            // Eliminar primero los detalles
            detalleFacturaRepository.deleteByFacturaId(id);
            // Luego eliminar la factura
            facturaRepository.deleteById(id);
        }
    }
}
