package com.example.inventory_app.Controllers;

import com.example.inventory_app.Entities.Empresa;
import com.example.inventory_app.Repositories.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador TEMPORAL para consultar datos en producción.
 * ELIMINAR EN PRODUCCIÓN FINAL.
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private EmpresaRepository empresaRepository;

    /**
     * GET /api/admin/empresas
     * Lista todas las empresas registradas (solo para debug)
     */
    @GetMapping("/empresas")
    public ResponseEntity<?> listarEmpresas() {
        try {
            List<Empresa> empresas = empresaRepository.findAll();
            
            // Crear respuesta simplificada
            List<Map<String, Object>> resultado = empresas.stream().map(e -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", e.getId());
                map.put("nombre", e.getNombre());
                map.put("email", e.getEmail());
                map.put("nit", e.getNit());
                map.put("schemaName", e.getSchemaName());
                map.put("tenantKey", e.getTenantKey());
                map.put("activa", e.isActiva());
                map.put("fechaRegistro", e.getFechaRegistro());
                return map;
            }).toList();
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                Map.of("error", "Error al consultar empresas: " + e.getMessage())
            );
        }
    }

    /**
     * GET /api/admin/empresa/{email}
     * Busca empresa por email
     */
    @GetMapping("/empresa/{email}")
    public ResponseEntity<?> buscarEmpresaPorEmail(@PathVariable String email) {
        try {
            var empresaOpt = empresaRepository.findByEmail(email);
            
            if (empresaOpt.isEmpty()) {
                return ResponseEntity.status(404).body(
                    Map.of("error", "Empresa no encontrada")
                );
            }
            
            Empresa e = empresaOpt.get();
            Map<String, Object> resultado = new HashMap<>();
            resultado.put("id", e.getId());
            resultado.put("nombre", e.getNombre());
            resultado.put("email", e.getEmail());
            resultado.put("nit", e.getNit());
            resultado.put("schemaName", e.getSchemaName());
            resultado.put("tenantKey", e.getTenantKey());
            resultado.put("activa", e.isActiva());
            resultado.put("fechaRegistro", e.getFechaRegistro());
            
            return ResponseEntity.ok(resultado);
            
        } catch (Exception ex) {
            return ResponseEntity.status(500).body(
                Map.of("error", "Error al buscar empresa: " + ex.getMessage())
            );
        }
    }
}
