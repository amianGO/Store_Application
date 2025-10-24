package com.example.inventory_app.Controllers;

import com.example.inventory_app.Entities.Factura;
import com.example.inventory_app.Services.FacturaService;
import com.example.inventory_app.Services.ClienteService;
import com.example.inventory_app.Services.EmpleadoService;
import com.example.inventory_app.Services.ProductoService;
import com.example.inventory_app.Controllers.dto.FacturaCreacionDTO;
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

    @PostMapping
    public ResponseEntity<Factura> crear(@Valid @RequestBody FacturaCreacionDTO facturaDTO) {
        Factura factura = new Factura();
        factura.setCliente(clienteService.findById(facturaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
        factura.setEmpleado(empleadoService.findById(facturaDTO.getEmpleadoId())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado")));

        for (FacturaCreacionDTO.DetalleFacturaDTO detalleDTO : facturaDTO.getDetalles()) {
            Producto producto = productoService.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
            
            DetalleFactura detalle = new DetalleFactura();
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitario(producto.getPrecioVenta());
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
}
