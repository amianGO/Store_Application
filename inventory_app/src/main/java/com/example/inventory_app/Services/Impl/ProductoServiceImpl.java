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
        // @PrePersist se encarga de establecer createdAt, updatedAt y activo=true automáticamente
        // Solo manejamos lógica de negocio específica
        
        if (producto.getId() != null) {
            // Producto existente - verificar si cambió el stock
            Optional<Producto> productoExistente = productoRepository.findById(producto.getId());
            if (productoExistente.isPresent()) {
                Producto existente = productoExistente.get();
                
                // Solo aplicar lógica automática si el stock cambió
                if (!existente.getStock().equals(producto.getStock())) {
                    if (producto.getStock() != null && producto.getStock() == 0) {
                        producto.setActivo(false);
                    } else if (producto.getStock() != null && producto.getStock() > 0 && !existente.isActivo()) {
                        // Solo reactivar si estaba inactivo Y ahora tiene stock
                        producto.setActivo(true);
                    }
                }
            }
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
        return productoRepository.findByPrecioVentaBetweenAndActivoTrue(precioMin, precioMax);
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
        
        // Lógica automática: Si stock llega a 0, desactivar producto
        if (nuevoStock == 0) {
            producto.setActivo(false);
        } else if (nuevoStock > 0 && !producto.isActivo()) {
            // Si se agrega stock a un producto inactivo, reactivarlo
            producto.setActivo(true);
        }
        
        return productoRepository.save(producto);
    }

    @Override
    public void delete(Long id) {
        // Eliminación física del producto
        productoRepository.deleteById(id);
    }

    @Override
    public void deactivate(Long id) {
        productoRepository.findById(id).ifPresent(producto -> {
            producto.setActivo(false);
            productoRepository.save(producto);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAllActive() {
        return productoRepository.findByActivoTrue();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findByNombre(String nombre) {
        return productoRepository.findByNombreContainingIgnoreCaseAndActivoTrue(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Producto> findAll() {
        return productoRepository.findAll();
    }
}
