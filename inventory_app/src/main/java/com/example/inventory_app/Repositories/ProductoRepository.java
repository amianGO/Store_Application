package com.example.inventory_app.Repositories;

import com.example.inventory_app.Entities.Producto;
import com.example.inventory_app.Entities.CategoriaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad Producto.
 * Proporciona métodos para acceder y manipular datos de productos en la base de datos.
 *
 * @author DamianG
 * @version 1.0
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    
    /**
     * Busca un producto por su código.
     * @param codigo Código del producto
     * @return Optional con el producto si existe
     */
    Optional<Producto> findByCodigo(String codigo);
    
    /**
     * Busca productos por categoría.
     * @param categoria Categoría de productos
     * @return Lista de productos de la categoría especificada
     */
    List<Producto> findByCategoria(CategoriaProducto categoria);
    
    /**
     * Busca productos con stock menor al mínimo establecido.
     * @return Lista de productos con bajo stock
     */
    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.estadoActivo = true")
    List<Producto> findProductosConBajoStock();
    
    /**
     * Busca productos por rango de precios.
     * @param precioMin Precio mínimo
     * @param precioMax Precio máximo
     * @return Lista de productos dentro del rango de precios
     */
    List<Producto> findByPrecioVentaBetweenAndEstadoActivoTrue(BigDecimal precioMin, BigDecimal precioMax);
    
    /**
     * Busca productos por nombre que contengan el texto proporcionado.
     * @param nombre Texto a buscar en el nombre
     * @return Lista de productos que coinciden con la búsqueda
     */
    List<Producto> findByNombreContainingIgnoreCaseAndEstadoActivoTrue(String nombre);
    
    /**
     * Verifica si existe un producto con el código proporcionado.
     * @param codigo Código a verificar
     * @return true si existe, false si no
     */
    boolean existsByCodigo(String codigo);
}
