package com.example.inventory_app.Controllers;

import com.example.inventory_app.Config.TenantContext;
import com.example.inventory_app.Entities.CarritoCompra;
import com.example.inventory_app.Entities.Producto;
import com.example.inventory_app.Services.CarritoCompraService;
import com.example.inventory_app.Services.ProductoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para gestión de carritos de compra en contexto multi-tenant.
 * 
 * IMPORTANTE: Este controller opera en el schema del TENANT (empresa).
 * - Requiere JWT de empleado autenticado
 * - TenantFilter configura automáticamente el schema correcto
 * - Cada empleado tiene su propio carrito temporal
 * 
 * @author Sistema Multi-Tenant
 * @version 2.0 - Multi-Tenant
 */
@RestController
@RequestMapping("/api/carrito")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class CarritoCompraController {

    private final CarritoCompraService carritoService;
    private final ProductoService productoService;

    /**
     * Agregar producto al carrito del empleado autenticado.
     */
    @PostMapping("/agregar")
    public ResponseEntity<?> agregarProducto(
            @RequestBody Map<String, Object> request,
            HttpServletRequest httpRequest) {
        
        try {
            String schemaName = TenantContext.getCurrentTenant();
            Long empleadoId = (Long) httpRequest.getAttribute("empleadoId");
            
            if (empleadoId == null) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(crearErrorResponse("Se requiere autenticación de empleado"));
            }
            
            Long productoId = Long.valueOf(request.get("productoId").toString());
            Integer cantidad = Integer.valueOf(request.get("cantidad").toString());
            
            log.info("=== AGREGAR AL CARRITO ===");
            log.info("Empleado ID: {}", empleadoId);
            log.info("Producto ID: {}", productoId);
            log.info("Cantidad: {}", cantidad);
            log.info("Schema: {}", schemaName);
            
            CarritoCompra item = carritoService.agregarProducto(empleadoId, productoId, cantidad);
            
            // Obtener información del producto
            Producto producto = productoService.findById(productoId)
                .orElse(null);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Producto agregado al carrito",
                "item", crearItemResponse(item, producto),
                "totalCarrito", carritoService.calcularTotalCarrito(empleadoId),
                "cantidadItems", carritoService.contarItems(empleadoId),
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al agregar producto al carrito: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse(e.getMessage()));
        }
    }

    /**
     * Obtener el carrito del empleado autenticado.
     */
    @GetMapping
    public ResponseEntity<?> obtenerCarrito(HttpServletRequest request) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            Long empleadoId = (Long) request.getAttribute("empleadoId");
            
            if (empleadoId == null) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(crearErrorResponse("Se requiere autenticación de empleado"));
            }
            
            List<CarritoCompra> items = carritoService.obtenerCarritoPorEmpleado(empleadoId);
            
            // Enriquecer con información de productos
            List<Map<String, Object>> itemsConProducto = items.stream()
                .map(item -> {
                    Producto producto = productoService.findById(item.getProductoId()).orElse(null);
                    return crearItemResponse(item, producto);
                })
                .collect(Collectors.toList());
            
            BigDecimal total = carritoService.calcularTotalCarrito(empleadoId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "items", itemsConProducto,
                "cantidadItems", items.size(),
                "total", total,
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al obtener carrito: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al obtener carrito"));
        }
    }

    /**
     * Actualizar cantidad de un item del carrito.
     */
    @PutMapping("/item/{id}")
    public ResponseEntity<?> actualizarCantidad(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request,
            HttpServletRequest httpRequest) {
        
        try {
            String schemaName = TenantContext.getCurrentTenant();
            Long empleadoId = (Long) httpRequest.getAttribute("empleadoId");
            
            Integer cantidad = request.get("cantidad");
            
            log.info("=== ACTUALIZAR CANTIDAD ===");
            log.info("Item ID: {}", id);
            log.info("Nueva cantidad: {}", cantidad);
            
            CarritoCompra item = carritoService.actualizarCantidad(id, cantidad);
            Producto producto = productoService.findById(item.getProductoId()).orElse(null);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Cantidad actualizada",
                "item", crearItemResponse(item, producto),
                "totalCarrito", carritoService.calcularTotalCarrito(empleadoId),
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al actualizar cantidad: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse(e.getMessage()));
        }
    }

    /**
     * Eliminar un item del carrito.
     */
    @DeleteMapping("/item/{id}")
    public ResponseEntity<?> eliminarItem(
            @PathVariable Long id,
            HttpServletRequest request) {
        
        try {
            String schemaName = TenantContext.getCurrentTenant();
            Long empleadoId = (Long) request.getAttribute("empleadoId");
            
            carritoService.eliminarItem(id);
            
            log.info("✓ Item eliminado del carrito");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Item eliminado del carrito",
                "totalCarrito", carritoService.calcularTotalCarrito(empleadoId),
                "cantidadItems", carritoService.contarItems(empleadoId),
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al eliminar item: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al eliminar item"));
        }
    }

    /**
     * Vaciar todo el carrito del empleado.
     */
    @DeleteMapping("/vaciar")
    public ResponseEntity<?> vaciarCarrito(HttpServletRequest request) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            Long empleadoId = (Long) request.getAttribute("empleadoId");
            
            if (empleadoId == null) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(crearErrorResponse("Se requiere autenticación de empleado"));
            }
            
            carritoService.vaciarCarrito(empleadoId);
            
            log.info("✓ Carrito vaciado completamente");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Carrito vaciado exitosamente",
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al vaciar carrito: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al vaciar carrito"));
        }
    }

    /**
     * Obtener total y cantidad de items del carrito.
     */
    @GetMapping("/resumen")
    public ResponseEntity<?> obtenerResumen(HttpServletRequest request) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            Long empleadoId = (Long) request.getAttribute("empleadoId");
            
            if (empleadoId == null) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(crearErrorResponse("Se requiere autenticación de empleado"));
            }
            
            BigDecimal total = carritoService.calcularTotalCarrito(empleadoId);
            Long cantidad = carritoService.contarItems(empleadoId);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "total", total,
                "cantidadItems", cantidad,
                "schemaName", schemaName
            ));
            
        } catch (Exception e) {
            log.error("ERROR al obtener resumen: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al obtener resumen"));
        }
    }

    /**
     * Crea respuesta con información del item y producto.
     */
    private Map<String, Object> crearItemResponse(CarritoCompra item, Producto producto) {
        Map<String, Object> response = new HashMap<>();
        response.put("id", item.getId());
        response.put("productoId", item.getProductoId());
        response.put("cantidad", item.getCantidad());
        response.put("precioUnitario", item.getPrecioUnitario());
        response.put("subtotal", item.getSubtotal());
        response.put("createdAt", item.getCreatedAt());
        
        if (producto != null) {
            Map<String, Object> productoInfo = new HashMap<>();
            productoInfo.put("codigo", producto.getCodigo());
            productoInfo.put("nombre", producto.getNombre());
            productoInfo.put("categoria", producto.getCategoria());
            productoInfo.put("stockDisponible", producto.getStock());
            productoInfo.put("activo", producto.isActivo());
            response.put("producto", productoInfo);
        }
        
        return response;
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
