package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.Producto;
import com.example.inventory_app.Entities.CategoriaProducto;
import com.example.inventory_app.Repositories.ProductoRepository;
import com.example.inventory_app.Services.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de Producto.
 *
 * @author DamianG
 * @version 1.0
 */
@Service
@Transactional
public class ProductoServiceImpl implements ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public Producto save(Producto producto) {
        if (producto.getId() == null) {
            producto.setEstadoActivo(true);
            producto.setFechaRegistro(new java.util.Date());
        }
        return productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findById(Long id) {
        return productoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Producto> findByCodigo(String codigo) {
        return productoRepository.findByCodigo(codigo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByCategoria(CategoriaProducto categoria) {
        return productoRepository.findByCategoria(categoria);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findProductosConBajoStock() {
        return productoRepository.findProductosConBajoStock();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByRangoPrecio(BigDecimal precioMin, BigDecimal precioMax) {
        return productoRepository.findByPrecioVentaBetweenAndEstadoActivoTrue(precioMin, precioMax);
    }

    @Override
    public Producto actualizarStock(Long id, int cantidad) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
        int nuevoStock = producto.getStock() + cantidad;
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente");
        }
        
        producto.setStock(nuevoStock);
        return productoRepository.save(producto);
    }

    @Override
    public void deactivate(Long id) {
        productoRepository.findById(id).ifPresent(producto -> {
            producto.setEstadoActivo(false);
            productoRepository.save(producto);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndEstadoActivoTrue(nombre);
    }
}
