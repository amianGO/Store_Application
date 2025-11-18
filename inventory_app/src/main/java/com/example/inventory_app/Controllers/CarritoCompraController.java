package com.example.inventory_app.Controllers;

import com.example.inventory_app.Controllers.dto.CarritoCreacionDTO;
import com.example.inventory_app.Entities.*;
import com.example.inventory_app.Services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de carritos de compra.
 *
 * @author DamianG
 * @version 1.0
 */
@RestController
@RequestMapping("/api/carritos")
@CrossOrigin(origins = "*")
public class CarritoCompraController {

    @Autowired
    private CarritoCompraService carritoCompraService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ProductoService productoService;

    @PostMapping
    public ResponseEntity<CarritoCompra> crear(@Valid @RequestBody CarritoCreacionDTO carritoDTO) {
        // Validar que el cliente y empleado existan
        Cliente cliente = clienteService.findById(carritoDTO.getClienteId())
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        
        Empleado empleado = empleadoService.findById(carritoDTO.getEmpleadoId())
            .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // Crear el carrito
        CarritoCompra carrito = new CarritoCompra();
        carrito.setCliente(cliente);
        carrito.setEmpleado(empleado);

        // Agregar los items si existen
        if (carritoDTO.getItems() != null) {
            for (CarritoCreacionDTO.DetalleCarritoDTO detalleDTO : carritoDTO.getItems()) {
                Producto producto = productoService.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalleDTO.getProductoId()));
                
                DetalleCarrito detalle = new DetalleCarrito(producto, detalleDTO.getCantidad());
                carrito.addDetalle(detalle);
            }
        }

        return ResponseEntity.ok(carritoCompraService.create(carrito));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CarritoCompra> obtenerPorId(@PathVariable Long id) {
        return carritoCompraService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<CarritoCompra>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(carritoCompraService.findByCliente(clienteId));
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<CarritoCompra>> listarPorEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(carritoCompraService.findByEmpleado(empleadoId));
    }

    @GetMapping("/cliente/{clienteId}/activo")
    public ResponseEntity<CarritoCompra> obtenerCarritoActivo(@PathVariable Long clienteId) {
        return carritoCompraService.findCarritoActivo(clienteId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/actualizar-total")
    public ResponseEntity<CarritoCompra> actualizarTotal(@PathVariable Long id) {
        return ResponseEntity.ok(carritoCompraService.actualizarTotal(id));
    }

    @DeleteMapping("/{id}/vaciar")
    public ResponseEntity<Void> vaciar(@PathVariable Long id) {
        carritoCompraService.vaciarCarrito(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/completar")
    public ResponseEntity<Void> completar(@PathVariable Long id) {
        carritoCompraService.completarCarrito(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/limpiar-abandonados")
    public ResponseEntity<Long> limpiarCarritosAbandonados(@RequestParam int horas) {
        return ResponseEntity.ok(carritoCompraService.eliminarCarritosAbandonados(horas));
    }
}
