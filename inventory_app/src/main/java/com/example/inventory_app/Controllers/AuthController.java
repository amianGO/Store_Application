package com.example.inventory_app.Controllers;

import com.example.inventory_app.Controllers.dto.EmpresaResponseDTO;
import com.example.inventory_app.Entities.Empleado;
import com.example.inventory_app.Services.EmpleadoService;
import com.example.inventory_app.Services.EmpresaService;
import com.example.inventory_app.Config.JwtService;
import com.example.inventory_app.Controllers.dto.EmpleadoLoginDTO;
import com.example.inventory_app.Controllers.dto.EmpleadoRegistroDTO;
import com.example.inventory_app.Controllers.dto.LoginResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


/**
 * Controlador REST para la autenticación de empleados (Multi-Tenant).
 * 
 * IMPORTANTE: 
 * - Login de empleado requiere tenantKey para identificar el schema
 * - Registro de empleado se hace desde EmpleadoController (requiere JWT de empresa)
 *
 * @author DamianG
 * @version 2.0 (Multi-Tenant)
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private EmpleadoService empleadoService;

    @Autowired
    private EmpresaService empresaService;

    @Autowired
    private JwtService jwtService;

    /**
     * POST /api/auth/login
     * 
     * Login de empleado en sistema Multi-Tenant.
     * 
     * IMPORTANTE: Requiere tenantKey para identificar el schema de la empresa.
     * 
     * Flujo:
     * 1. Buscar empresa por tenantKey en schema public
     * 2. Configurar TenantContext con el schemaName de la empresa
     * 3. Buscar empleado en el schema del tenant
     * 4. Generar JWT con datos del empleado + empresa
     * 
     * @param loginDTO Credenciales + tenantKey
     * @return LoginResponse con JWT
     * 
     * Ejemplo Request:
     * POST http://localhost:8080/api/auth/login
     * {
     *   "usuario": "vendedor01",
     *   "password": "Password123@",
     *   "tenantKey": "abc123def456"
     * }
     * 
     * Response 200:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "empleadoId": 1,
     *   "usuario": "vendedor01",
     *   "rol": "VENDEDOR",
     *   "nombre": "Juan",
     *   "apellido": "Pérez",
     *   "cargo": "Vendedor"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody EmpleadoLoginDTO loginDTO, HttpServletRequest request) {
        try {
            System.out.println("[AUTH-CONTROLLER] Login empleado: " + loginDTO.getUsuario() + " (TenantKey: " + loginDTO.getTenantKey() + ")");
            
            // El TenantFilter ya configuró el schema correcto basado en el tenantKey
            // Solo necesitamos validar credenciales
            
            // Obtener datos configurados por el TenantFilter
            String schemaName = (String) request.getAttribute("schemaName");
            Long empresaId = (Long) request.getAttribute("empresaId");
            String tenantKey = (String) request.getAttribute("tenantKey");
            
            if (schemaName == null || empresaId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    crearErrorResponse("INVALID_TENANT", "Tenant Key inválido")
                );
            }
            
            System.out.println("[AUTH-CONTROLLER] Schema configurado: " + schemaName);
            
            // Validar credenciales del empleado (ya está en el schema correcto)
            Empleado empleado = empleadoService.verificarCredenciales(loginDTO.getUsuario(), loginDTO.getPassword())
                    .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
            
            System.out.println("[AUTH-CONTROLLER] Empleado autenticado: " + empleado.getNombre() + " " + empleado.getApellido());
            
            // Generar JWT con información del empleado + empresa
            String token = jwtService.generateTokenForEmpleado(
                empresaId,
                schemaName,
                tenantKey,
                empleado.getId(),
                empleado.getUsuario(),
                empleado.getRol().toString()
            );
            
            System.out.println("[AUTH-CONTROLLER] Token JWT generado para empleado");
            
            // Crear respuesta
            LoginResponse response = new LoginResponse(
                token,
                empleado.getId(),
                empleado.getUsuario(),
                empleado.getRol(),
                empleado.getNombre(),
                empleado.getApellido(),
                empleado.getCargo()
            );
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            System.err.println("[AUTH-CONTROLLER] Error de autenticación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                crearErrorResponse("INVALID_CREDENTIALS", e.getMessage())
            );
            
        } catch (Exception e) {
            System.err.println("[AUTH-CONTROLLER] Error interno: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearErrorResponse("INTERNAL_ERROR", "Error al procesar login")
            );
        }
    }
    
    /**
     * POST /api/auth/register - DESHABILITADO
     * 
     * El registro de empleados se hace desde el endpoint protegido:
     * POST /api/empresas/empleados (requiere JWT de empresa)
     * 
     * Ver EmpleadoController para crear empleados.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody EmpleadoRegistroDTO empleadoDTO) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
            crearErrorResponse("ENDPOINT_DESHABILITADO", 
                "Use POST /api/empresas/empleados con JWT de empresa para registrar empleados")
        );
    }

    /**
     * Método auxiliar para crear respuestas de error consistentes.
     */
    private Map<String, Object> crearErrorResponse(String codigo, String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", codigo);
        error.put("mensaje", mensaje);
        error.put("timestamp", java.time.LocalDateTime.now());
        return error;
    }
    
    /**
     * GET /api/auth/verificar-email
     * 
     * Verifica la dirección de correo electrónico de una empresa usando un token único.
     * 
     * @param token Token único para verificar el email
     * @return Detalles de la empresa si la verificación es exitosa
     * 
     * Ejemplo Request:
     * GET http://localhost:8080/api/auth/verificar-email?token=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
     * 
     * Response 200:
     * {
     *   "success": true,
     *   "message": "Email verificado exitosamente. Ahora puedes iniciar sesión.",
     *   "empresa": {
     *     "id": 1,
     *     "nombre": "Empresa SA",
     *     "email": "contacto@empresa.com",
     *     "estado": "ACTIVO"
     *   }
     * }
     */
    @GetMapping("/verificar-email")
    public ResponseEntity<?> verificarEmail(@RequestParam String token) {
        try {
            System.out.println("[AUTH] Verificando email con token: " + token);
            
            EmpresaResponseDTO empresa = empresaService.verificarEmailConToken(token);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Email verificado exitosamente. Ahora puedes iniciar sesión.",
                "empresa", empresa
            ));
        } catch (IllegalArgumentException e) {
            System.err.println("[AUTH] Error al verificar email: " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        }
    }

    /**
     * POST /api/auth/reenviar-verificacion
     * 
     * Reenvía el email de verificación a una dirección de correo electrónico.
     * 
     * @param body Contiene el campo "email" con la dirección de correo electrónico
     * @return Mensaje de éxito o error
     * 
     * Ejemplo Request:
     * POST http://localhost:8080/api/auth/reenviar-verificacion
     * {
     *   "email": "contacto@empresa.com"
     * }
     * 
     * Response 200:
     * {
     *   "success": true,
     *   "message": "Se ha enviado un nuevo email de verificación"
     * }
     */
    @PostMapping("/reenviar-verificacion")
    public ResponseEntity<?> reenviarVerificacion(@RequestBody Map<String, String> body) {
        try {
            String email = body.get("email");
            
            if (email == null || email.isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                        "success", false,
                        "message", "El email es requerido"
                    ));
            }
            
            System.out.println("[AUTH] Reenviando verificación a: " + email);
            
            EmpresaResponseDTO empresa = empresaService.reenviarEmailVerificacion(email);
            
            // TODO: Enviar email con nuevo token
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Se ha enviado un nuevo email de verificación",
                "tokenVerificacion", empresa.getId() // En producción NO enviar el token en respuesta
            ));
        } catch (IllegalArgumentException e) {
            System.err.println("[AUTH] Error al reenviar verificación: " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        }
    }
}
