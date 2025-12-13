package com.example.inventory_app.Controllers;

import com.example.inventory_app.Config.TenantContext;
import com.example.inventory_app.Entities.Producto;
import com.example.inventory_app.Entities.CategoriaProducto;
import com.example.inventory_app.Services.ProductoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de productos en contexto multi-tenant.
 * 
 * IMPORTANTE: Este controller opera en el schema del TENANT (empresa).
 * - Requiere JWT de empresa o empleado autenticado
 * - TenantFilter configura automáticamente el schema correcto
 * - Todas las operaciones trabajan en el schema del tenant
 * - Los productos son específicos de cada empresa
 * 
 * @author Sistema Multi-Tenant
 * @version 2.0 - Multi-Tenant
 */
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class ProductoController {

    private final ProductoService productoService;

    /**
     * Crea un nuevo producto en el schema de la empresa/empleado autenticado.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPRESA')")
    public ResponseEntity<?> crearProducto(
            @Valid @RequestBody Producto producto,
            HttpServletRequest request) {
        
        try {
            String schemaName = TenantContext.getCurrentTenant();
            Long empresaId = (Long) request.getAttribute("empresaId");
            
            log.info("=== CREAR PRODUCTO ===");
            log.info("Schema actual: {}", schemaName);
            log.info("Empresa ID: {}", empresaId);
            log.info("Producto: {}", producto.getNombre());
            
            if (schemaName == null || schemaName.equals("public")) {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error: No se pudo determinar el tenant"));
            }

            Producto guardado = productoService.save(producto);
            
            log.info("✓ Producto creado en schema: {}", schemaName);
            log.info("  - ID: {}", guardado.getId());
            log.info("  - Código: {}", guardado.getCodigo());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Producto creado exitosamente",
                "producto", guardado,
                "tenantInfo", Map.of(
                    "schemaName", schemaName,
                    "empresaId", empresaId != null ? empresaId : 0
                )
            ));
            
        } catch (Exception e) {
            log.error("ERROR al crear producto: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al crear producto: " + e.getMessage()));
        }
    }

    /**
     * Obtiene todos los productos del tenant actual.
     */
    @GetMapping
    public ResponseEntity<?> listarProductos() {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            log.info("Listando productos del schema: {}", schemaName);
            
            List<Producto> productos = productoService.findAll();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "productos", productos,
                "total", productos.size(),
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al listar productos: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al listar productos"));
        }
    }

    /**
     * Obtiene un producto por ID del tenant actual.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerProducto(@PathVariable Long id) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            
            var productoOpt = productoService.findById(id);
            if (productoOpt.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(crearErrorResponse("Producto no encontrado"));
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "producto", productoOpt.get(),
                "schemaName", schemaName
            ));
                    
        } catch (Exception e) {
            log.error("ERROR al obtener producto: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al obtener producto"));
        }
    }

    /**
     * Busca un producto por código en el tenant actual.
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<?> obtenerPorCodigo(@PathVariable String codigo) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            
            var productoOpt = productoService.findByCodigo(codigo);
            if (productoOpt.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(crearErrorResponse("Producto no encontrado"));
            }
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "producto", productoOpt.get(),
                "schemaName", schemaName
            ));
                    
        } catch (Exception e) {
            log.error("ERROR al buscar producto por código: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al buscar producto"));
        }
    }

    /**
     * Actualiza un producto existente del tenant actual.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPRESA')")
    public ResponseEntity<?> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody Producto producto) {
        
        try {
            String schemaName = TenantContext.getCurrentTenant();
            
            log.info("=== ACTUALIZANDO PRODUCTO ===");
            log.info("ID: {}", id);
            log.info("Estado Activo recibido: {}", producto.isActivo());
            log.info("Stock recibido: {}", producto.getStock());
            
            var productoOpt = productoService.findById(id);
            if (productoOpt.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(crearErrorResponse("Producto no encontrado"));
            }
            
            producto.setId(id);
            Producto actualizado = productoService.save(producto);
            
            log.info("✓ Producto actualizado en schema: {}", schemaName);
            log.info("Estado Activo final: {}", actualizado.isActivo());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Producto actualizado exitosamente",
                "producto", actualizado,
                "schemaName", schemaName
            ));
                    
        } catch (Exception e) {
            log.error("ERROR al actualizar producto: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al actualizar producto"));
        }
    }

    /**
     * Actualiza solo el stock de un producto.
     */
    @PatchMapping("/{id}/stock")
    public ResponseEntity<?> actualizarStock(
            @PathVariable Long id, 
            @RequestParam int cantidad) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            Producto actualizado = productoService.actualizarStock(id, cantidad);
            
            log.info("✓ Stock actualizado en schema: {}", schemaName);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Stock actualizado exitosamente",
                "producto", actualizado,
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al actualizar stock: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al actualizar stock"));
        }
    }

    /**
     * Elimina un producto del tenant actual.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMPRESA')")
    public ResponseEntity<?> eliminarProducto(@PathVariable Long id) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            
            if (productoService.findById(id).isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(crearErrorResponse("Producto no encontrado"));
            }
            
            productoService.delete(id);
            
            log.info("✓ Producto eliminado en schema: {}", schemaName);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Producto eliminado exitosamente",
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al eliminar producto: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al eliminar producto"));
        }
    }

    /**
     * Busca productos por categoría en el tenant actual.
     */
    @GetMapping("/categoria/{categoria}")
    public ResponseEntity<?> buscarPorCategoria(@PathVariable CategoriaProducto categoria) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<Producto> productos = productoService.findByCategoria(categoria);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "productos", productos,
                "total", productos.size(),
                "categoria", categoria,
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al buscar productos por categoría: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al buscar productos"));
        }
    }

    /**
     * Lista productos con bajo stock del tenant actual.
     */
    @GetMapping("/bajo-stock")
    public ResponseEntity<?> listarBajoStock() {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<Producto> productos = productoService.findProductosConBajoStock();
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "productos", productos,
                "total", productos.size(),
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al listar productos bajo stock: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al listar productos"));
        }
    }

    /**
     * Busca productos por rango de precio del tenant actual.
     */
    @GetMapping("/rango-precio")
    public ResponseEntity<?> listarPorRangoPrecio(
            @RequestParam BigDecimal min, 
            @RequestParam BigDecimal max) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<Producto> productos = productoService.findByRangoPrecio(min, max);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "productos", productos,
                "total", productos.size(),
                "rangoPrecio", Map.of("min", min, "max", max),
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al buscar productos por rango de precio: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al buscar productos"));
        }
    }

    /**
     * Busca productos por nombre (búsqueda parcial) del tenant actual.
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNombre(@RequestParam String nombre) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<Producto> productos = productoService.findByNombre(nombre);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "productos", productos,
                "total", productos.size(),
                "busqueda", nombre,
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al buscar productos por nombre: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al buscar productos"));
        }
    }

    /**
     * Crea una respuesta de error estandarizada.
     */
    private Map<String, Object> crearErrorResponse(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", mensaje);
        return error;
    }
}
