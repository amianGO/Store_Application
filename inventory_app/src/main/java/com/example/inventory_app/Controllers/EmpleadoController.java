package com.example.inventory_app.Controllers;

import com.example.inventory_app.Config.TenantContext;
import com.example.inventory_app.Controllers.dto.EmpleadoRegistroDTO;
import com.example.inventory_app.Entities.Empleado;
import com.example.inventory_app.Services.EmpleadoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Controller REST para gestión de empleados en contexto multi-tenant.
 * 
 * IMPORTANTE: Este controller opera en el schema del TENANT (empresa).
 * - Requiere JWT de empresa o empleado con rol ADMIN
 * - TenantInterceptor configura automáticamente el schema correcto
 * - Todas las operaciones trabajan en el schema del tenant
 * 
 * @author Sistema Multi-Tenant
 * @version 2.0 - Multi-Tenant
 */
@RestController
@RequestMapping("/api/empresas/empleados")
@CrossOrigin(origins = "http://localhost:5173")
public class EmpleadoController {

    @Autowired
    private EmpleadoService empleadoService;
    
    @Autowired
    private com.example.inventory_app.Repositories.EmpleadoRepository empleadoRepository;

    /**
     * Registra un nuevo empleado en el schema de la empresa autenticada.
     * 
     * IMPORTANTE: Accesible por:
     * - Token de EMPRESA (tiene ROLE_EMPRESA)
     * - Token de EMPLEADO con rol ADMIN (tiene ROLE_ADMIN)
     */
    @PostMapping
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('EMPRESA') or hasRole('ADMIN')")
    public ResponseEntity<?> registrarEmpleado(
            @Valid @RequestBody EmpleadoRegistroDTO registroDTO,
            HttpServletRequest request) {
        
        try {
            // Obtener datos del JWT configurados por TenantInterceptor
            String schemaName = (String) request.getAttribute("schemaName");
            Long empresaId = (Long) request.getAttribute("empresaId");
            String tenantKey = (String) request.getAttribute("tenantKey");
            
            System.out.println("\n=== REGISTRO DE EMPLEADO ===");
            System.out.println("Schema actual: " + schemaName);
            System.out.println("Empresa ID: " + empresaId);
            System.out.println("Tenant Key: " + tenantKey);
            System.out.println("Usuario a crear: " + registroDTO.getUsuario());
            System.out.println("Rol: " + registroDTO.getRol());
            
            // Validar que el schema esté configurado
            if (schemaName == null || schemaName.equals("public")) {
                System.err.println("ERROR: No se pudo determinar el schema del tenant");
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al determinar el tenant"));
            }

            // Validar confirmación de contraseña
            if (!registroDTO.getPassword().equals(registroDTO.getConfirmPassword())) {
                return ResponseEntity
                    .badRequest()
                    .body(crearErrorResponse("Las contraseñas no coinciden"));
            }

            // Crear el empleado
            Empleado empleado = new Empleado();
            empleado.setNombre(registroDTO.getNombre());
            empleado.setApellido(registroDTO.getApellido());
            empleado.setDocumento(registroDTO.getDocumento());
            empleado.setUsuario(registroDTO.getUsuario());
            empleado.setPassword(registroDTO.getPassword()); // Password en texto plano - el servicio la hashea
            empleado.setTelefono(registroDTO.getTelefono());
            empleado.setEmail(registroDTO.getEmail());
            empleado.setCargo(registroDTO.getCargo());
            empleado.setRol(registroDTO.getRol());
            empleado.setEstadoActivo(true);

            // Guardar el empleado - el servicio hashea la password
            Empleado guardado = empleadoService.save(empleado);

            System.out.println("✓ Empleado creado exitosamente en schema: " + TenantContext.getCurrentTenant());
            System.out.println("  - ID: " + guardado.getId());
            System.out.println("  - Usuario: " + guardado.getUsuario());
            System.out.println("  - Rol: " + guardado.getRol());

            // Preparar tenant info (manejar posibles valores null)
            Map<String, Object> tenantInfo = new HashMap<>();
            tenantInfo.put("schemaName", schemaName);
            tenantInfo.put("empresaId", empresaId);
            tenantInfo.put("tenantKey", tenantKey);

            // Preparar respuesta
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Empleado registrado exitosamente");
            response.put("empleado", crearEmpleadoResponse(guardado));
            response.put("tenantInfo", tenantInfo);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            System.err.println("ERROR de validación: " + e.getMessage());
            return ResponseEntity
                .badRequest()
                .body(crearErrorResponse(e.getMessage()));
                
        } catch (Exception e) {
            System.err.println("ERROR inesperado al registrar empleado: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al registrar empleado: " + e.getMessage()));
        }
    }

    /**
     * Obtiene la lista de todos los empleados del tenant actual.
     * 
     * IMPORTANTE: Accesible por:
     * - Cualquier EMPLEADO autenticado (puede ver la lista)
     * - Token de EMPRESA
     */
    @GetMapping
    public ResponseEntity<?> listarEmpleados(HttpServletRequest request) {
        try {
            String schemaName = (String) request.getAttribute("schemaName");
            
            System.out.println("\n=== LISTADO DE EMPLEADOS ===");
            System.out.println("Schema actual: " + schemaName);
            
            // Obtener todos los empleados del tenant
            java.util.List<Empleado> empleados = empleadoRepository.findAll();
            
            System.out.println("✓ Total empleados encontrados: " + empleados.size());
            
            // Convertir a respuesta sin passwords
            java.util.List<Map<String, Object>> empleadosResponse = empleados.stream()
                .map(this::crearEmpleadoResponse)
                .collect(java.util.stream.Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", empleados.size());
            response.put("empleados", empleadosResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.err.println("ERROR al listar empleados: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al obtener empleados: " + e.getMessage()));
        }
    }

    /**
     * Crea una respuesta de error estandarizada.
     */
    private Map<String, Object> crearErrorResponse(String mensaje) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("message", mensaje);
        return error;
    }

    /**
     * Crea una respuesta con datos del empleado (sin contraseña).
     */
    private Map<String, Object> crearEmpleadoResponse(Empleado empleado) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", empleado.getId());
        data.put("nombre", empleado.getNombre());
        data.put("apellido", empleado.getApellido());
        data.put("documento", empleado.getDocumento());
        data.put("usuario", empleado.getUsuario());
        data.put("telefono", empleado.getTelefono());
        data.put("email", empleado.getEmail());
        data.put("cargo", empleado.getCargo());
        data.put("rol", empleado.getRol());
        data.put("activo", empleado.isEstadoActivo());
        return data;
    }
}
