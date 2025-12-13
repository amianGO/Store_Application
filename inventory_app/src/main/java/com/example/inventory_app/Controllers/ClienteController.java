package com.example.inventory_app.Controllers;

import com.example.inventory_app.Config.TenantContext;
import com.example.inventory_app.Entities.Cliente;
import com.example.inventory_app.Services.ClienteService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller REST para gestión de clientes en contexto multi-tenant.
 *
 * IMPORTANTE: Este controller opera en el schema del TENANT (empresa).
 * - Requiere JWT de empresa o empleado autenticado
 * - TenantFilter configura automáticamente el schema correcto
 * - Todas las operaciones trabajan en el schema del tenant
 * - Los clientes son específicos de cada empresa
 *
 * @author Sistema Multi-Tenant
 * @version 2.0 - Multi-Tenant
 */
@RestController
@RequestMapping("/api/clientes")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class ClienteController {

    private final ClienteService clienteService;

    /**
     * Crea un nuevo cliente en el schema de la empresa/empleado autenticado.
     */
    @PostMapping
    public ResponseEntity<?> crearCliente(
            @Valid @RequestBody Cliente cliente,
            HttpServletRequest request) {

        try {
            String schemaName = TenantContext.getCurrentTenant();
            Long empresaId = (Long) request.getAttribute("empresaId");

            log.info("=== CREAR CLIENTE ===");
            log.info("Schema actual: {}", schemaName);
            log.info("Empresa ID: {}", empresaId);
            log.info("Cliente: {} {}", cliente.getNombre(), cliente.getApellido());

            if (schemaName == null || schemaName.equals("public")) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(crearErrorResponse("Error: No se pudo determinar el tenant"));
            }

            Cliente guardado = clienteService.save(cliente);

            log.info("✓ Cliente creado en schema: {}", schemaName);
            log.info("  - ID: {}", guardado.getId());
            log.info("  - Documento: {}", guardado.getDocumento());

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Cliente creado exitosamente",
                    "cliente", guardado,
                    "tenantInfo", Map.of(
                            "schemaName", schemaName,
                            "empresaId", empresaId != null ? empresaId : 0
                    )
            ));

        } catch (Exception e) {
            log.error("ERROR al crear cliente: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al crear cliente: " + e.getMessage()));
        }
    }

    /**
     * Obtiene todos los clientes del tenant actual.
     */
    @GetMapping
    public ResponseEntity<?> listarClientes() {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            log.info("Listando clientes del schema: {}", schemaName);

            List<Cliente> clientes = clienteService.findAll();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "clientes", clientes,
                    "total", clientes.size(),
                    "schemaName", schemaName
            ));

        } catch (Exception e) {
            log.error("ERROR al listar clientes: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al listar clientes"));
        }
    }

    /**
     * Obtiene un cliente por ID del tenant actual.
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerCliente(@PathVariable Long id) {
        try {
            String schemaName = TenantContext.getCurrentTenant();

            var clienteOpt = clienteService.findById(id);
            if (clienteOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(crearErrorResponse("Cliente no encontrado"));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "cliente", clienteOpt.get(),
                    "schemaName", schemaName
            ));

        } catch (Exception e) {
            log.error("ERROR al obtener cliente: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al obtener cliente"));
        }
    }

    /**
     * Busca un cliente por documento en el tenant actual.
     */
    @GetMapping("/documento/{documento}")
    public ResponseEntity<?> obtenerPorDocumento(@PathVariable String documento) {
        try {
            String schemaName = TenantContext.getCurrentTenant();

            var clienteOpt = clienteService.findByDocumento(documento);
            if (clienteOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(crearErrorResponse("Cliente no encontrado"));
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "cliente", clienteOpt.get(),
                    "schemaName", schemaName
            ));

        } catch (Exception e) {
            log.error("ERROR al buscar cliente por documento: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al buscar cliente"));
        }
    }

    /**
     * Actualiza un cliente existente del tenant actual.
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizarCliente(
            @PathVariable Long id,
            @Valid @RequestBody Cliente cliente) {

        try {
            String schemaName = TenantContext.getCurrentTenant();

            log.info("=== ACTUALIZANDO CLIENTE ===");
            log.info("ID: {}", id);

            var clienteOpt = clienteService.findById(id);
            if (clienteOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(crearErrorResponse("Cliente no encontrado"));
            }

            cliente.setId(id);
            Cliente actualizado = clienteService.save(cliente);

            log.info("✓ Cliente actualizado en schema: {}", schemaName);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cliente actualizado exitosamente",
                    "cliente", actualizado,
                    "schemaName", schemaName
            ));

        } catch (Exception e) {
            log.error("ERROR al actualizar cliente: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al actualizar cliente"));
        }
    }

    /**
     * Elimina permanentemente un cliente del tenant actual.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarCliente(@PathVariable Long id) {
        try {
            String schemaName = TenantContext.getCurrentTenant();

            if (clienteService.findById(id).isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(crearErrorResponse("Cliente no encontrado"));
            }

            clienteService.delete(id);

            log.info("✓ Cliente eliminado permanentemente en schema: {}", schemaName);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cliente eliminado exitosamente",
                    "schemaName", schemaName
            ));

        } catch (Exception e) {
            log.error("ERROR al eliminar cliente: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al eliminar cliente"));
        }
    }

    /**
     * Desactiva un cliente sin eliminarlo del tenant actual.
     */
    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<?> desactivarCliente(@PathVariable Long id) {
        try {
            String schemaName = TenantContext.getCurrentTenant();

            var clienteOpt = clienteService.findById(id);
            if (clienteOpt.isEmpty()) {
                return ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(crearErrorResponse("Cliente no encontrado"));
            }

            clienteService.deactivate(id);

            log.info("✓ Cliente desactivado en schema: {}", schemaName);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cliente desactivado exitosamente",
                    "schemaName", schemaName
            ));

        } catch (Exception e) {
            log.error("ERROR al desactivar cliente: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al desactivar cliente"));
        }
    }

    /**
     * Lista clientes activos del tenant actual.
     */
    @GetMapping("/activos")
    public ResponseEntity<?> listarClientesActivos() {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<Cliente> clientes = clienteService.findAllActive();

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "clientes", clientes,
                    "total", clientes.size(),
                    "schemaName", schemaName
            ));

        } catch (Exception e) {
            log.error("ERROR al listar clientes activos: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al listar clientes"));
        }
    }

    /**
     * Busca clientes por nombre o apellido (búsqueda parcial) del tenant actual.
     */
    @GetMapping("/buscar")
    public ResponseEntity<?> buscarPorNombre(@RequestParam String busqueda) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<Cliente> clientes = clienteService.buscarPorNombre(busqueda);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "clientes", clientes,
                    "total", clientes.size(),
                    "busqueda", busqueda,
                    "schemaName", schemaName
            ));

        } catch (Exception e) {
            log.error("ERROR al buscar clientes: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al buscar clientes"));
        }
    }

    /**
     * Busca clientes por ciudad del tenant actual.
     */
    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<?> buscarPorCiudad(@PathVariable String ciudad) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<Cliente> clientes = clienteService.findByCiudad(ciudad);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "clientes", clientes,
                    "total", clientes.size(),
                    "ciudad", ciudad,
                    "schemaName", schemaName
            ));

        } catch (Exception e) {
            log.error("ERROR al buscar clientes por ciudad: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error al buscar clientes"));
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
}
