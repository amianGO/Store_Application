package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.CarritoCompra;
import com.example.inventory_app.Entities.Producto;
import com.example.inventory_app.Repositories.CarritoCompraRepository;
import com.example.inventory_app.Repositories.ProductoRepository;
import com.example.inventory_app.Services.CarritoCompraService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CarritoCompraServiceImpl implements CarritoCompraService {

    @Autowired
    private CarritoCompraRepository carritoRepository;
    
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public CarritoCompra agregarProducto(Long empleadoId, Long productoId, Integer cantidad) {
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (!producto.isActivo()) {
            throw new RuntimeException("El producto no está disponible");
        }
        
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }

        Optional<CarritoCompra> itemExistente = carritoRepository
            .findByEmpleadoIdAndProductoId(empleadoId, productoId);
        
        if (itemExistente.isPresent()) {
            CarritoCompra item = itemExistente.get();
            int nuevaCantidad = item.getCantidad() + cantidad;
            
            if (producto.getStock() < nuevaCantidad) {
                throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
            }
            
            item.setCantidad(nuevaCantidad);
            return carritoRepository.save(item);
        } else {
            CarritoCompra nuevoItem = new CarritoCompra();
            nuevoItem.setEmpleadoId(empleadoId);
            nuevoItem.setProductoId(productoId);
            nuevoItem.setCantidad(cantidad);
            nuevoItem.setPrecioUnitario(producto.getPrecioVenta());
            return carritoRepository.save(nuevoItem);
        }
    }

    @Override
    public CarritoCompra actualizarCantidad(Long id, Integer cantidad) {
        CarritoCompra item = carritoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Item del carrito no encontrado"));
        
        Producto producto = productoRepository.findById(item.getProductoId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        
        if (producto.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + producto.getStock());
        }
        
        item.setCantidad(cantidad);
        return carritoRepository.save(item);
    }

    @Override
    public void eliminarItem(Long id) {
        carritoRepository.deleteById(id);
    }

    @Override
    public void vaciarCarrito(Long empleadoId) {
        carritoRepository.deleteByEmpleadoId(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CarritoCompra> obtenerCarritoPorEmpleado(Long empleadoId) {
        return carritoRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CarritoCompra> findById(Long id) {
        return carritoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalCarrito(Long empleadoId) {
        List<CarritoCompra> items = carritoRepository.findByEmpleadoId(empleadoId);
        return items.stream()
            .map(CarritoCompra::getSubtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    @Transactional(readOnly = true)
    public Long contarItems(Long empleadoId) {
        return carritoRepository.countByEmpleadoId(empleadoId);
    }
}
