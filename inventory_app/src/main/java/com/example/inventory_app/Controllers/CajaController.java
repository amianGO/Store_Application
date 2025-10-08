package com.example.inventory_app.Controllers;

import com.example.inventory_app.Entities.Caja;
import com.example.inventory_app.Services.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Controlador REST para la gestión de cajas.
 *
 * @author DamianG
 * @version 1.0
 */
@RestController
@RequestMapping("/api/cajas")
@CrossOrigin(origins = "*")
public class CajaController {

    @Autowired
    private CajaService cajaService;

    @PostMapping
    public ResponseEntity<Caja> abrirCaja(@RequestBody Caja caja) {
        return ResponseEntity.ok(cajaService.abrirCaja(caja));
    }

    @PatchMapping("/{id}/cerrar")
    public ResponseEntity<Caja> cerrarCaja(
            @PathVariable Long id,
            @RequestParam BigDecimal montoFinal,
            @RequestParam(required = false) String observaciones) {
        return ResponseEntity.ok(cajaService.cerrarCaja(id, montoFinal, observaciones));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Caja> obtenerPorId(@PathVariable Long id) {
        return cajaService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/numero/{numeroCaja}")
    public ResponseEntity<Caja> obtenerPorNumero(@PathVariable String numeroCaja) {
        return cajaService.findByNumeroCaja(numeroCaja)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<List<Caja>> listarPorEmpleado(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(cajaService.findByEmpleado(empleadoId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Caja>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(cajaService.findByEstado(estado));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<Caja>> listarPorFechaApertura(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha) {
        return ResponseEntity.ok(cajaService.findByFechaApertura(fecha));
    }

    @GetMapping("/empleado/{empleadoId}/caja-abierta")
    public ResponseEntity<Boolean> verificarCajaAbierta(@PathVariable Long empleadoId) {
        return ResponseEntity.ok(cajaService.tieneCajaAbierta(empleadoId));
    }

    @PatchMapping("/{id}/actualizar-ventas")
    public ResponseEntity<Caja> actualizarVentas(
            @PathVariable Long id,
            @RequestParam BigDecimal montoVenta) {
        return ResponseEntity.ok(cajaService.actualizarTotalVentas(id, montoVenta));
    }
}
