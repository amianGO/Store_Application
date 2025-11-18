package com.example.inventory_app.Controllers;

import com.example.inventory_app.Entities.Empleado;
import com.example.inventory_app.Services.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.example.inventory_app.Controllers.dto.EmpleadoRegistroDTO;

import java.util.List;

/**
 * Controlador REST para la gestión de empleados.
 *
 * @author DamianG
 * @version 1.0
 */
@RestController
@RequestMapping("/api/empleados")
@CrossOrigin(origins = "*")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;

    @PostMapping
    public ResponseEntity<Empleado> crear(@Valid @RequestBody Empleado empleado) {
        return ResponseEntity.ok(empleadoService.save(empleado));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Empleado> obtenerPorId(@PathVariable Long id) {
        return empleadoService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuario}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Empleado> obtenerPorUsuario(@PathVariable String usuario) {
        return empleadoService.findByUsuario(usuario)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/documento/{documento}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Empleado> obtenerPorDocumento(@PathVariable String documento) {
        return empleadoService.findByDocumento(documento)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cargo/{cargo}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Empleado>> listarPorCargo(@PathVariable String cargo) {
        return ResponseEntity.ok(empleadoService.findByCargo(cargo));
    }

    @GetMapping
    public ResponseEntity<List<Empleado>> listarActivos() {
        return ResponseEntity.ok(empleadoService.findAllActive());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Empleado> actualizar(@PathVariable Long id, @RequestBody Empleado empleado) {
        return empleadoService.findById(id)
                .map(empleadoExistente -> {
                    empleado.setId(id);
                    return ResponseEntity.ok(empleadoService.save(empleado));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivar(@PathVariable Long id) {
        empleadoService.deactivate(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/auth")
    public ResponseEntity<Empleado> autenticar(@RequestParam String usuario, @RequestParam String password) {
        return empleadoService.verificarCredenciales(usuario, password)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/registro")
    public ResponseEntity<Empleado> registrar(@Valid @RequestBody EmpleadoRegistroDTO empleadoDTO) {
        Empleado empleado = new Empleado();
        empleado.setNombre(empleadoDTO.getNombre());
        empleado.setApellido(empleadoDTO.getApellido());
        empleado.setDocumento(empleadoDTO.getDocumento());
        empleado.setUsuario(empleadoDTO.getUsuario());
        empleado.setPassword(empleadoDTO.getPassword());
        empleado.setTelefono(empleadoDTO.getTelefono());
        empleado.setEmail(empleadoDTO.getEmail());
        empleado.setCargo(empleadoDTO.getCargo());
        empleado.setRol(empleadoDTO.getRol());
        
        return ResponseEntity.ok(empleadoService.save(empleado));
    }
}
