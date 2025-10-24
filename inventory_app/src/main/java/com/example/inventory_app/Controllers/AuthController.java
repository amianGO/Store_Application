package com.example.inventory_app.Controllers;

import com.example.inventory_app.Entities.Empleado;
import com.example.inventory_app.Services.EmpleadoService;
import com.example.inventory_app.Controllers.dto.EmpleadoRegistroDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para la autenticación y registro de empleados.
 *
 * @author DamianG
 * @version 1.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private EmpleadoService empleadoService;

    @PostMapping("/login")
    public ResponseEntity<Empleado> login(@RequestParam String usuario, @RequestParam String password) {
        return empleadoService.verificarCredenciales(usuario, password)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/register")
    public ResponseEntity<Empleado> register(@Valid @RequestBody EmpleadoRegistroDTO empleadoDTO) {
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
