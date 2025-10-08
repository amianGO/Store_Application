package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.CarritoCompra;
import com.example.inventory_app.Repositories.CarritoCompraRepository;
import com.example.inventory_app.Services.CarritoCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de CarritoCompra.
 *
 * @author DamianG
 * @version 1.0
 */
@Service
@Transactional
public class CarritoCompraServiceImpl implements CarritoCompraService {

    @Autowired
    private CarritoCompraRepository carritoCompraRepository;

    @Override
    public CarritoCompra create(CarritoCompra carritoCompra) {
        carritoCompra.setFechaCreacion(new Date());
        carritoCompra.setEstado("ACTIVO");
        carritoCompra.setTotalEstimado(BigDecimal.ZERO);
        return carritoCompraRepository.save(carritoCompra);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarritoCompra> findById(Long id) {
        return carritoCompraRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarritoCompra> findByCliente(Long clienteId) {
        return carritoCompraRepository.findByClienteId(clienteId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarritoCompra> findByEmpleado(Long empleadoId) {
        return carritoCompraRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarritoCompra> findCarritoActivo(Long clienteId) {
        return carritoCompraRepository.findByClienteIdAndEstado(clienteId, "ACTIVO");
    }

    @Override
    public CarritoCompra actualizarTotal(Long id) {
        return carritoCompraRepository.findById(id).map(carrito -> {
            BigDecimal total = carrito.getItems().stream()
                .map(item -> item.getPrecioUnitario().multiply(new BigDecimal(item.getCantidad())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            carrito.setTotalEstimado(total);
            return carritoCompraRepository.save(carrito);
        }).orElseThrow(() -> new RuntimeException("Carrito no encontrado"));
    }

    @Override
    public void vaciarCarrito(Long id) {
        carritoCompraRepository.findById(id).ifPresent(carrito -> {
            carrito.getItems().clear();
            carrito.setTotalEstimado(BigDecimal.ZERO);
            carritoCompraRepository.save(carrito);
        });
    }

    @Override
    public void completarCarrito(Long id) {
        carritoCompraRepository.findById(id).ifPresent(carrito -> {
            carrito.setEstado("COMPLETADO");
            carritoCompraRepository.save(carrito);
        });
    }

    @Override
    public Long eliminarCarritosAbandonados(int horasInactivo) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -horasInactivo);
        return carritoCompraRepository.deleteByFechaCreacionBeforeAndEstado(cal.getTime(), "ACTIVO");
    }
}
