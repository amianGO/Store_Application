package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.DetalleFactura;
import com.example.inventory_app.Entities.Factura;
import com.example.inventory_app.Repositories.FacturaRepository;
import com.example.inventory_app.Services.FacturaService;
import com.example.inventory_app.Services.ProductoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;
    private final ProductoService productoService;

    @Override
    public Factura create(Factura factura) {
        log.info("Creando factura con {} detalles", factura.getDetalles().size());
        
        // CRÍTICO: Calcular subtotales de cada detalle primero
        factura.getDetalles().forEach(DetalleFactura::calcularSubtotal);
        
        // Luego calcular totales de la factura
        factura.calcularTotales();
        
        log.info("Subtotal: {}, Total: {}", factura.getSubtotal(), factura.getTotal());
        
        // Guardar factura (cascade guardará los detalles automáticamente)
        Factura facturaGuardada = facturaRepository.save(factura);
        
        // Actualizar stock de productos
        factura.getDetalles().forEach(detalle -> {
            try {
                productoService.actualizarStock(detalle.getProductoId(), -detalle.getCantidad());
                log.info("Stock actualizado para producto ID: {}, cantidad: -{}", 
                    detalle.getProductoId(), detalle.getCantidad());
            } catch (Exception e) {
                log.error("Error al actualizar stock del producto {}: {}", 
                    detalle.getProductoId(), e.getMessage());
            }
        });
        
        return facturaGuardada;
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
    public List<Factura> findAll() {
        return facturaRepository.findAll();
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
    public List<Factura> findByEstado(String estado) {
        return facturaRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Factura> findByRangoFechas(Date fechaInicio, Date fechaFin) {
        return facturaRepository.findByRangoFechas(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public Double calcularTotalVentasDia(Date fecha) {
        Double total = facturaRepository.calcularTotalVentasDia(fecha);
        return total != null ? total : 0.0;
    }

    @Override
    public void anularFactura(Long id) {
        facturaRepository.findById(id).ifPresent(factura -> {
            factura.setEstado("ANULADA");
            facturaRepository.save(factura);
            
            // Devolver stock a los productos
            factura.getDetalles().forEach(detalle -> {
                try {
                    productoService.actualizarStock(detalle.getProductoId(), detalle.getCantidad());
                    log.info("Stock devuelto para producto ID: {}, cantidad: +{}", 
                        detalle.getProductoId(), detalle.getCantidad());
                } catch (Exception e) {
                    log.error("Error al devolver stock del producto {}: {}", 
                        detalle.getProductoId(), e.getMessage());
                }
            });
        });
    }

    @Override
    public void delete(Long id) {
        facturaRepository.deleteById(id);
    }
}
