package com.example.inventory_app.Controllers;

import com.example.inventory_app.Controllers.dto.*;
import com.example.inventory_app.Services.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador REST para gestión de Empresas (Multi-Tenant).
 * 
 * Endpoints disponibles:
 * - POST /api/auth/empresa/registro - Registrar nueva empresa
 * - POST /api/auth/empresa/login - Autenticar empresa
 * - GET /api/empresas/perfil - Obtener perfil de empresa autenticada
 * - PUT /api/empresas/perfil - Actualizar perfil de empresa
 * - POST /api/empresas/{id}/verificar - Verificar email de empresa
 * 
 * IMPORTANTE: Los endpoints de perfil requieren autenticación JWT (implementar después).
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@RestController
@RequestMapping("/api/auth/empresa")
@CrossOrigin(origins = "*") // Configurar origins específicos en producción
public class EmpresaController {

    @Autowired
    private EmpresaService empresaService;
    
    @Autowired
    private com.example.inventory_app.Services.EmpleadoService empleadoService;

    /**
     * POST /api/auth/empresa/registro
     * 
     * Registra una nueva empresa en el sistema Multi-Tenant.
     * 
     * Proceso:
     * 1. Validar datos de entrada (DTO)
     * 2. Crear empresa en schema public
     * 3. Generar schema dedicado (empresa_N)
     * 4. Crear suscripción de prueba (15 días)
     * 5. Clonar estructura desde template_schema
     * 
     * @param registroDTO Datos de registro validados
     * @return EmpresaResponseDTO con información de la empresa creada
     * 
     * Ejemplo Request:
     * POST http://localhost:8080/api/auth/empresa/registro
     * {
     *   "nombre": "Mi Empresa SAS",
     *   "nit": "900123456-7",
     *   "email": "contacto@miempresa.com",
     *   "password": "Password123!",
     *   "telefono": "3001234567",
     *   "direccion": "Calle 123 #45-67"
     * }
     * 
     * Response 201:
     * {
     *   "id": 1,
     *   "nombre": "Mi Empresa SAS",
     *   "nit": "900123456-7",
     *   "email": "contacto@miempresa.com",
     *   "schemaName": "empresa_1",
     *   "tenantKey": "abcd1234efgh5678",
     *   "activa": true,
     *   "verificada": false,
     *   "fechaRegistro": "2025-11-23T10:30:00"
     * }
     */
    @PostMapping("/registro")
    public ResponseEntity<?> registrarEmpresa(@Valid @RequestBody EmpresaRegistroDTO registroDTO) {
        try {
            System.out.println("[CONTROLLER] Solicitud de registro para: " + registroDTO.getEmail());
            
            EmpresaResponseDTO empresaCreada = empresaService.registrarEmpresa(registroDTO);
            
            System.out.println("[CONTROLLER] Empresa registrada exitosamente: " + empresaCreada.getTenantKey());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(empresaCreada);
            
        } catch (IllegalArgumentException e) {
            // Error de validación (email duplicado, NIT duplicado, etc.)
            System.err.println("[CONTROLLER] Error de validación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                crearErrorResponse("VALIDATION_ERROR", e.getMessage())
            );
            
        } catch (Exception e) {
            // Error interno del servidor
            System.err.println("[CONTROLLER] Error interno en registro: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearErrorResponse("INTERNAL_ERROR", "Error al registrar empresa. Por favor intente nuevamente.")
            );
        }
    }

    /**
     * POST /api/auth/empresa/login
     * 
     * Autentica una empresa y genera token JWT.
     * 
     * @param loginDTO Credenciales de acceso
     * @return LoginResponseDTO con token JWT y datos de empresa
     * 
     * Ejemplo Request:
     * POST http://localhost:8080/api/auth/empresa/login
     * {
     *   "email": "contacto@miempresa.com",
     *   "password": "Password123!"
     * }
     * 
     * Response 200:
     * {
     *   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
     *   "tipo": "Bearer",
     *   "empresa": {
     *     "id": 1,
     *     "nombre": "Mi Empresa SAS",
     *     "email": "contacto@miempresa.com",
     *     "schemaName": "empresa_1",
     *     "tenantKey": "abcd1234efgh5678"
     *   }
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody EmpresaLoginDTO loginDTO) {
        try {
            System.out.println("[CONTROLLER] Intento de login: " + loginDTO.getEmail());
            
            LoginResponseDTO response = empresaService.autenticarEmpresa(loginDTO);
            
            System.out.println("[CONTROLLER] Login exitoso para: " + loginDTO.getEmail());
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            // Credenciales inválidas
            System.err.println("[CONTROLLER] Login fallido: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                crearErrorResponse("INVALID_CREDENTIALS", e.getMessage())
            );
            
        } catch (Exception e) {
            // Error interno
            System.err.println("[CONTROLLER] Error interno en login: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearErrorResponse("INTERNAL_ERROR", "Error al procesar login")
            );
        }
    }

    /**
     * GET /api/auth/empresa/perfil
     * 
     * Obtiene el perfil de la empresa autenticada.
     * 
     * El empresaId se extrae automáticamente del JWT por el TenantInterceptor.
     * 
     * @param request HttpServletRequest para obtener empresaId del JWT
     * @return EmpresaResponseDTO con datos de perfil
     * 
     * Ejemplo Request:
     * GET http://localhost:8080/api/auth/empresa/perfil
     * Headers: Authorization: Bearer {token}
     * 
     * Response 200:
     * {
     *   "id": 1,
     *   "nombre": "Mi Empresa SAS",
     *   "nit": "900123456-7",
     *   "email": "contacto@miempresa.com",
     *   "telefono": "3001234567",
     *   "direccion": "Calle 123 #45-67",
     *   "schemaName": "empresa_1",
     *   "tenantKey": "abcd1234efgh5678",
     *   "activa": true,
     *   "verificada": true,
     *   "fechaRegistro": "2025-11-23T10:30:00"
     * }
     */
    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil(jakarta.servlet.http.HttpServletRequest request) {
        try {
            // Obtener empresaId del request (configurado por TenantInterceptor)
            Long empresaId = (Long) request.getAttribute("empresaId");
            
            if (empresaId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    crearErrorResponse("UNAUTHORIZED", "No se pudo identificar la empresa del token")
                );
            }
            
            System.out.println("[CONTROLLER] Consultando perfil de empresa: " + empresaId);
            
            EmpresaResponseDTO perfil = empresaService.obtenerEmpresaPorId(empresaId);
            
            return ResponseEntity.ok(perfil);
            
        } catch (IllegalArgumentException e) {
            System.err.println("[CONTROLLER] Empresa no encontrada: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                crearErrorResponse("NOT_FOUND", e.getMessage())
            );
            
        } catch (Exception e) {
            System.err.println("[CONTROLLER] Error al obtener perfil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearErrorResponse("INTERNAL_ERROR", "Error al obtener perfil")
            );
        }
    }

    /**
     * PUT /api/auth/empresa/perfil
     * 
     * Actualiza el perfil de la empresa autenticada.
     * 
     * NOTA: Solo permite actualizar nombre, telefono y dirección.
     * Email y NIT NO se pueden cambiar.
     * 
     * El empresaId se extrae automáticamente del JWT.
     * 
     * @param request HttpServletRequest para obtener empresaId del JWT
     * @param actualizacionDTO Datos a actualizar
     * @return EmpresaResponseDTO con datos actualizados
     * 
     * Ejemplo Request:
     * PUT http://localhost:8080/api/auth/empresa/perfil
     * Headers: Authorization: Bearer {token}
     * {
     *   "nombre": "Mi Empresa SAS - Actualizado",
     *   "telefono": "3009876543",
     *   "direccion": "Carrera 45 #67-89"
     * }
     * 
     * Response 200:
     * {
     *   "id": 1,
     *   "nombre": "Mi Empresa SAS - Actualizado",
     *   "telefono": "3009876543",
     *   "direccion": "Carrera 45 #67-89",
     *   ...
     * }
     */
    @PutMapping("/perfil")
    public ResponseEntity<?> actualizarPerfil(
            jakarta.servlet.http.HttpServletRequest request,
            @Valid @RequestBody EmpresaRegistroDTO actualizacionDTO) {
        try {
            // Obtener empresaId del request (configurado por TenantInterceptor)
            Long empresaId = (Long) request.getAttribute("empresaId");
            
            if (empresaId == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    crearErrorResponse("UNAUTHORIZED", "No se pudo identificar la empresa del token")
                );
            }
            
            System.out.println("[CONTROLLER] Actualizando perfil de empresa: " + empresaId);
            
            EmpresaResponseDTO empresaActualizada = empresaService.actualizarPerfil(empresaId, actualizacionDTO);
            
            System.out.println("[CONTROLLER] Perfil actualizado exitosamente");
            
            return ResponseEntity.ok(empresaActualizada);
            
        } catch (IllegalArgumentException e) {
            System.err.println("[CONTROLLER] Error de validación: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                crearErrorResponse("VALIDATION_ERROR", e.getMessage())
            );
            
        } catch (Exception e) {
            System.err.println("[CONTROLLER] Error al actualizar perfil: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearErrorResponse("INTERNAL_ERROR", "Error al actualizar perfil")
            );
        }
    }

    /**
     * POST /api/auth/empresa/{id}/verificar
     * 
     * Marca una empresa como verificada (proceso de verificación de email).
     * 
     * NOTA: Este endpoint debería ser llamado desde un link de verificación
     * enviado por email con un token temporal.
     * 
     * @param id ID de la empresa a verificar
     * @return Mensaje de confirmación
     * 
     * Ejemplo Request:
     * POST http://localhost:8080/api/auth/empresa/1/verificar?token=abc123def456
     * 
     * Response 200:
     * {
     *   "mensaje": "Empresa verificada exitosamente",
     *   "empresa": {
     *     "id": 1,
     *     "nombre": "Mi Empresa SAS",
     *     "verificada": true
     *   }
     * }
     */
    @PostMapping("/{id}/verificar")
    public ResponseEntity<?> verificarEmpresa(
            @PathVariable Long id,
            @RequestParam(required = false) String token) {
        try {
            System.out.println("[CONTROLLER] Endpoint deprecado - Redirigir a /api/auth/verificar-email");
            
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(crearErrorResponse(
                    "ENDPOINT_DEPRECADO", 
                    "Este endpoint está deprecado. Use GET /api/auth/verificar-email?token=... en su lugar"
                ));
            
        } catch (Exception e) {
            System.err.println("[CONTROLLER] Error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                crearErrorResponse("INTERNAL_ERROR", "Error al procesar solicitud")
            );
        }
    }

    /**
     * GET /api/auth/empresa/{id}
     * 
     * Obtiene los detalles de una empresa por su ID.
     * 
     * @param id ID de la empresa
     * @return EmpresaResponseDTO con datos de la empresa
     * 
     * Ejemplo Request:
     * GET http://localhost:8080/api/auth/empresa/1
     * 
     * Response 200:
     * {
     *   "id": 1,
     *   "nombre": "Mi Empresa SAS",
     *   "nit": "900123456-7",
     *   "email": "contacto@miempresa.com",
     *   "telefono": "3001234567",
     *   "direccion": "Calle 123 #45-67",
     *   "schemaName": "empresa_1",
     *   "tenantKey": "abcd1234efgh5678",
     *   "activa": true,
     *   "verificada": true,
     *   "fechaRegistro": "2025-11-23T10:30:00"
     * }
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            EmpresaResponseDTO empresa = empresaService.obtenerEmpresaPorId(id);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "empresa", empresa
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                    "success", false,
                    "message", e.getMessage()
                ));
        }
    }

    /**
     * GET /api/auth/empresa/{id}/tiene-empleados
     * 
     * Verifica si una empresa tiene empleados registrados.
     * 
     * ENDPOINT PÚBLICO - No requiere autenticación.
     * 
     * Uso: Después del login de empresa, verificar si debe:
     * - Crear primer empleado (tieneEmpleados = false)
     * - Hacer login de empleado (tieneEmpleados = true)
     * 
     * @param id ID de la empresa
     * @return Información sobre empleados de la empresa
     */
    @GetMapping("/{id}/tiene-empleados")
    public ResponseEntity<?> verificarEmpleados(@PathVariable Long id) {
        try {
            System.out.println("[CONTROLLER] Verificando empleados para empresa: " + id);
            
            // Obtener empresa
            EmpresaResponseDTO empresa = empresaService.obtenerEmpresaPorId(id);
            
            // Verificar empleados en el schema de la empresa
            boolean tieneEmpleados = empleadoService.empresaTieneEmpleados(
                empresa.getId(), 
                empresa.getSchemaName()
            );
            
            long cantidadEmpleados = empleadoService.contarEmpleados(
                empresa.getId(), 
                empresa.getSchemaName()
            );
            
            System.out.println("[CONTROLLER] Empresa " + id + " tiene " + cantidadEmpleados + " empleados");
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "empresaId", empresa.getId(),
                "tieneEmpleados", tieneEmpleados,
                "cantidadEmpleados", cantidadEmpleados,
                "schemaName", empresa.getSchemaName(),
                "requiereCrearPrimerEmpleado", !tieneEmpleados
            ));
            
        } catch (IllegalArgumentException e) {
            System.err.println("[CONTROLLER] Empresa no encontrada: " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(crearErrorResponse("NOT_FOUND", "Empresa no encontrada"));
            
        } catch (Exception e) {
            System.err.println("[CONTROLLER] Error al verificar empleados: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("INTERNAL_ERROR", "Error al verificar empleados"));
        }
    }
    
    /**
     * POST /api/auth/empresa/{id}/primer-empleado
     * 
     * Crea el primer empleado ADMIN para una empresa recién registrada.
     * 
     * REQUIERE: JWT de EMPRESA (configurado por TenantFilter)
     * NO requiere: JWT de empleado (porque aún no existe)
     * 
     * Este endpoint solo debería llamarse si GET /tiene-empleados retorna false.
     * 
     * @param id ID de la empresa (debe coincidir con el JWT)
     * @param dto Datos del primer empleado
     * @param request Para obtener empresaId del JWT
     * @return Empleado creado
     */
    @PostMapping("/{id}/primer-empleado")
    public ResponseEntity<?> crearPrimerEmpleado(
            @PathVariable Long id,
            @Valid @RequestBody PrimerEmpleadoDTO dto,
            jakarta.servlet.http.HttpServletRequest request) {
        try {
            // Obtener empresaId del JWT (configurado por TenantFilter)
            Long empresaIdFromJWT = (Long) request.getAttribute("empresaId");
            
            if (empresaIdFromJWT == null) {
                return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(crearErrorResponse("UNAUTHORIZED", "JWT de empresa requerido"));
            }
            
            // Validar que el ID del path coincida con el JWT
            if (!empresaIdFromJWT.equals(id)) {
                return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(crearErrorResponse("FORBIDDEN", "No puede crear empleados para otra empresa"));
            }
            
            System.out.println("[CONTROLLER] Creando primer empleado para empresa: " + id);
            
            // Validar contraseñas
            if (!dto.passwordsCoinciden()) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(crearErrorResponse("VALIDATION_ERROR", "Las contraseñas no coinciden"));
            }
            
            // Crear empleado
            com.example.inventory_app.Entities.Empleado empleado = empresaService.crearPrimerEmpleado(id, dto);
            
            System.out.println("[CONTROLLER] ✓ Primer empleado creado: " + empleado.getUsuario());
            
            // Retornar respuesta con datos del empleado
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Primer empleado creado exitosamente. Ahora puede iniciar sesión como empleado.",
                "empleado", Map.of(
                    "id", empleado.getId(),
                    "usuario", empleado.getUsuario(),
                    "nombre", empleado.getNombre(),
                    "apellido", empleado.getApellido(),
                    "email", empleado.getEmail(),
                    "cargo", empleado.getCargo(),
                    "rol", empleado.getRol().toString()
                )
            ));
            
        } catch (IllegalArgumentException e) {
            System.err.println("[CONTROLLER] Error de validación: " + e.getMessage());
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(crearErrorResponse("VALIDATION_ERROR", e.getMessage()));
            
        } catch (Exception e) {
            System.err.println("[CONTROLLER] Error al crear primer empleado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("INTERNAL_ERROR", "Error al crear empleado"));
        }
    }
    
    /**
     * Método auxiliar para crear respuestas de error consistentes.
     * 
     * @param codigo Código de error
     * @param mensaje Mensaje descriptivo
     * @return Map con estructura de error
     */
    private Map<String, Object> crearErrorResponse(String codigo, String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("error", codigo);
        error.put("mensaje", mensaje);
        error.put("timestamp", java.time.LocalDateTime.now());
        return error;
    }
}
