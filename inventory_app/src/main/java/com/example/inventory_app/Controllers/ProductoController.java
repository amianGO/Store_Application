package com.example.inventory_app.Controllers;

import com.example.inventory_app.Entities.Producto;
import com.example.inventory_app.Entities.CategoriaProducto;
import com.example.inventory_app.Services.ProductoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controlador REST para la gestión de productos.
 *
 * @author DamianG
 * @version 1.0
 */
@RestController
@RequestMapping("/api/productos")
@CrossOrigin(origins = "*")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<Producto> crear(@Valid @RequestBody Producto producto) {
        return ResponseEntity.ok(productoService.save(producto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorId(@PathVariable Long id) {
        return productoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Producto> obtenerPorCodigo(@PathVariable String codigo) {
        return productoService.findByCodigo(codigo)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<List<Producto>> listarPorCategoria(@PathVariable CategoriaProducto categoria) {
        return ResponseEntity.ok(productoService.findByCategoria(categoria));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<Producto>> listarBajoStock() {
        return ResponseEntity.ok(productoService.findProductosConBajoStock());
    }

    @GetMapping("/rango-precio")
    public ResponseEntity<List<Producto>> listarPorRangoPrecio(
            @RequestParam BigDecimal min, 
            @RequestParam BigDecimal max) {
        return ResponseEntity.ok(productoService.findByRangoPrecio(min, max));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        System.out.println("=== ACTUALIZANDO PRODUCTO ===");
        System.out.println("ID: " + id);
        System.out.println("Estado Activo recibido: " + producto.isEstadoActivo());
        System.out.println("Stock recibido: " + producto.getStock());
        
        return productoService.findById(id)
                .map(productoExistente -> {
                    producto.setId(id);
                    Producto result = productoService.save(producto);
                    System.out.println("Estado Activo final: " + result.isEstadoActivo());
                    return ResponseEntity.ok(result);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/stock")
    public ResponseEntity<Producto> actualizarStock(
            @PathVariable Long id, 
            @RequestParam int cantidad) {
        return ResponseEntity.ok(productoService.actualizarStock(id, cantidad));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoService.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<Producto>> buscarPorNombre(@RequestParam String nombre) {
        return ResponseEntity.ok(productoService.findByNombre(nombre));
    }

    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
        return ResponseEntity.ok(productoService.findAll());
    }
}
