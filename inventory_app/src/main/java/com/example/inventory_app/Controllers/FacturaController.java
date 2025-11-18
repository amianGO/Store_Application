package com.example.inventory_app.Controllers;

import com.example.inventory_app.Entities.Factura;
import com.example.inventory_app.Services.FacturaService;
import com.example.inventory_app.Services.ClienteService;
import com.example.inventory_app.Services.EmpleadoService;
import com.example.inventory_app.Services.ProductoService;
import com.example.inventory_app.Controllers.dto.FacturaCreacionDTO;
import com.example.inventory_app.Controllers.dto.FacturaResponseDTO;
import com.example.inventory_app.Entities.DetalleFactura;
import com.example.inventory_app.Entities.Producto;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * Controlador REST para la gestión de facturas.
 *
 * @author DamianG
 * @version 1.0
 */
@RestController
@RequestMapping("/api/facturas")
@CrossOrigin(origins = "*")
public class FacturaController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<FacturaResponseDTO>> listarTodas() {
        List<FacturaResponseDTO> facturas = facturaService.findAll()
                .stream()
                .map(FacturaResponseDTO::fromEntity)
                .toList();
        return ResponseEntity.ok(facturas);
    }

    @PostMapping
    public ResponseEntity<Factura> crear(@Valid @RequestBody FacturaCreacionDTO facturaDTO) {
        Factura factura = new Factura();
        
        // Obtener y almacenar información del cliente
        var cliente = clienteService.findById(facturaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        factura.setClienteId(cliente.getId());
        factura.setClienteNombre(cliente.getNombre() + " " + cliente.getApellido());
        
        // Obtener y almacenar información del empleado
        var empleado = empleadoService.findById(facturaDTO.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
        factura.setEmpleadoId(empleado.getId());
        factura.setEmpleadoNombre(empleado.getNombre() + " " + empleado.getApellido());

        for (FacturaCreacionDTO.DetalleFacturaDTO detalleDTO : facturaDTO.getDetalles()) {
            Producto producto = productoService.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            // DEBUG: Imprimir valores recibidos
            System.out.println("=== DEBUG DETALLE FACTURA ===");
            System.out.println("Producto: " + producto.getNombre());
            System.out.println("Precio unitario: " + producto.getPrecioVenta());
            System.out.println("Cantidad: " + detalleDTO.getCantidad());
            System.out.println("Descuento recibido: " + detalleDTO.getDescuento());
            
            DetalleFactura detalle = new DetalleFactura();
            // Establecer datos del producto directamente en el detalle
            detalle.setProductoCodigo(producto.getCodigo());
            detalle.setProductoNombre(producto.getNombre());
            detalle.setProductoCategoria(producto.getCategoria().toString());
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta());
            detalle.setDescuento(detalleDTO.getDescuento() != null ? detalleDTO.getDescuento() : java.math.BigDecimal.ZERO);
            detalle.calcularSubtotal(); // Calculamos el subtotal explícitamente
            
            // DEBUG: Imprimir resultado calculado
            System.out.println("Subtotal calculado: " + detalle.getSubtotal());
            System.out.println("==============================");
            
            factura.addDetalle(detalle);
        }

        return ResponseEntity.ok(facturaService.create(factura));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Factura> obtenerPorId(@PathVariable Long id) {
        return facturaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numeroFactura}")
    public ResponseEntity<Factura> obtenerPorNumero(@PathVariable String numeroFactura) {
        return facturaService.findByNumeroFactura(numeroFactura)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<List<Factura>> listarPorCliente(@PathVariable Long clienteId) {
        return ResponseEntity.ok(facturaService.findByCliente(clienteId));
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<Factura>> listarPorEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(facturaService.findByEmpleado(empleadoId));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<Factura>> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
        return ResponseEntity.ok(facturaService.findByRangoFechas(fechaInicio, fechaFin));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Factura>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(facturaService.findByEstado(estado));
    }

    @GetMapping("/ventas-dia")
    public ResponseEntity<Double> obtenerTotalVentasDia(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha) {
        return ResponseEntity.ok(facturaService.calcularTotalVentasDia(fecha));
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<Void> anular(@PathVariable Long id) {
        facturaService.anularFactura(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (facturaService.findById(id).isPresent()) {
            facturaService.delete(id);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }
}
