package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.Factura;
import com.example.inventory_app.Repositories.FacturaRepository;
import com.example.inventory_app.Services.FacturaService;
import com.example.inventory_app.Services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    private ProductoService productoService;

    @Override
    public Factura create(Factura factura) {
        factura.setFechaEmision(new Date());
        factura.setEstado("COMPLETADA");
        
        // Calcular totales antes de guardar
        factura.calcularTotales();
        
        // Actualizar stock de productos
        factura.getDetalles().forEach(detalle -> {
            productoService.actualizarStock(
                detalle.getProducto().getId(),
                -detalle.getCantidad()
            );
        });
        
        return facturaRepository.save(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Factura> findById(Long id) {
        return facturaRepository.findById(id);
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
        return facturaRepository.findByFechaEmisionBetween(fechaInicio, fechaFin);
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
        facturaRepository.findById(id).ifPresent(factura -> {
            if ("COMPLETADA".equals(factura.getEstado())) {
                // Restaurar stock de productos
                factura.getDetalles().forEach(detalle -> {
                    productoService.actualizarStock(
                        detalle.getProducto().getId(),
                        detalle.getCantidad()
                    );
                });
                
                factura.setEstado("ANULADA");
                facturaRepository.save(factura);
            }
        });
    }
}
