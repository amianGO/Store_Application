package com.example.inventory_app.Services;

import com.example.inventory_app.Entities.Producto;
import com.example.inventory_app.Entities.CategoriaProducto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Interface que define los servicios disponibles para la gestión de productos.
 *
 * @author DamianG
 * @version 1.0
 */
public interface ProductoService {
    
    /**
     * Guarda un nuevo producto o actualiza uno existente.
     * @param producto Producto a guardar
     * @return Producto guardado
     */
    Producto save(Producto producto);
    
    /**
     * Busca un producto por su ID.
     * @param id ID del producto
     * @return Optional con el producto si existe
     */
    Optional<Producto> findById(Long id);
    
    /**
     * Busca un producto por su código.
     * @param codigo Código del producto
     * @return Optional con el producto si existe
     */
    Optional<Producto> findByCodigo(String codigo);
    
    /**
     * Obtiene productos por categoría.
     * @param categoria Categoría de productos
     * @return Lista de productos de la categoría
     */
    List<Producto> findByCategoria(CategoriaProducto categoria);
    
    /**
     * Obtiene productos con stock bajo.
     * @return Lista de productos con stock bajo
     */
    List<Producto> findProductosConBajoStock();
    
    /**
     * Busca productos por rango de precios.
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Lista de productos en el rango
     */
    List<Producto> findByRangoPrecio(BigDecimal precioMin, BigDecimal precioMax);
    
    /**
     * Actualiza el stock de un producto.
     * @param id ID del producto
     * @param cantidad Cantidad a agregar (positiva) o restar (negativa)
     * @return Producto actualizado
     */
    Producto actualizarStock(Long id, int cantidad);
    
    /**
     * Desactiva un producto.
     * @param id ID del producto a desactivar
     */
    void deactivate(Long id);
    
    /**
     * Busca productos por nombre.
     * @param nombre Nombre a buscar
     * @return Lista de productos que coinciden
     */
    List<Producto> findByNombre(String nombre);
}
