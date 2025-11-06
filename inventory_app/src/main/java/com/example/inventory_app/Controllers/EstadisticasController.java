package com.example.inventory_app.Controllers;

import com.example.inventory_app.Services.FacturaService;
import com.example.inventory_app.Services.ProductoService;
import com.example.inventory_app.Services.ClienteService;
import com.example.inventory_app.Services.EmpleadoService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para estadísticas del sistema.
 *
 * @author DamianG
 * @version 1.0
 */
@RestController
@RequestMapping("/api/estadisticas")
@CrossOrigin(origins = "*")
public class EstadisticasController {

    @Autowired
    private FacturaService facturaService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private EmpleadoService empleadoService;

    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> obtenerDashboard() {
        Map<String, Object> estadisticas = new HashMap<>();
        
        // Estadísticas de productos
        estadisticas.put("totalProductos", productoService.findAll().size());
        estadisticas.put("productosBajoStock", productoService.findProductosConBajoStock().size());
        
        // Estadísticas de clientes
        estadisticas.put("totalClientes", clienteService.findAllActive().size());
        
        // Estadísticas de empleados
        estadisticas.put("totalEmpleados", empleadoService.findAllActive().size());
        
        // Estadísticas de ventas del día
        Date hoy = new Date();
        estadisticas.put("ventasHoy", facturaService.calcularTotalVentasDia(hoy));
        estadisticas.put("totalFacturasHoy", facturaService.findByRangoFechas(hoy, hoy).size());
        
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/ventas/dia")
    public ResponseEntity<Double> obtenerVentasDelDia(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha) {
        return ResponseEntity.ok(facturaService.calcularTotalVentasDia(fecha));
    }
}
