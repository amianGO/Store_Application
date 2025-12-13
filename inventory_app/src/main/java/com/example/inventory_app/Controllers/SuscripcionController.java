package com.example.inventory_app.Controllers;

import com.example.inventory_app.Controllers.dto.PlanSuscripcionDTO;
import com.example.inventory_app.Entities.TipoPlan;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para gestión de suscripciones y planes.
 * 
 * @author Sistema Multi-Tenant
 * @version 1.0
 */
@RestController
@RequestMapping("/api/suscripciones")
@CrossOrigin(origins = "http://localhost:5173")
public class SuscripcionController {

    /**
     * GET /api/suscripciones/planes
     * 
     * Obtiene todos los planes de suscripción disponibles.
     * ENDPOINT PÚBLICO - No requiere autenticación.
     */
    @GetMapping("/planes")
    public ResponseEntity<?> listarPlanes(
            @RequestParam(defaultValue = "false") boolean incluirPrueba) {
        
        try {
            System.out.println("▓ [SUSCRIPCION-CONTROLLER] Listando planes (incluirPrueba=" + incluirPrueba + ")");
            
            List<PlanSuscripcionDTO> planes;
            
            if (incluirPrueba) {
                // Incluir todos los planes
                planes = Arrays.stream(TipoPlan.values())
                    .map(PlanSuscripcionDTO::fromTipoPlan)
                    .collect(Collectors.toList());
            } else {
                // Excluir plan de PRUEBA
                planes = Arrays.stream(TipoPlan.values())
                    .filter(plan -> plan != TipoPlan.PRUEBA)
                    .map(PlanSuscripcionDTO::fromTipoPlan)
                    .collect(Collectors.toList());
            }
            
            System.out.println("▓ [SUSCRIPCION-CONTROLLER] Planes encontrados: " + planes.size());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "planes", planes,
                "total", planes.size()
            ));
            
        } catch (Exception e) {
            System.err.println("▓ [SUSCRIPCION-CONTROLLER] ERROR: " + e.getMessage());
            e.printStackTrace();
            
            return ResponseEntity
                .internalServerError()
                .body(Map.of(
                    "success", false,
                    "message", "Error al obtener planes: " + e.getMessage()
                ));
        }
    }
}
