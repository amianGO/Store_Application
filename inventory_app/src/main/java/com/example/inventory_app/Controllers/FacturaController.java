package com.example.inventory_app.Controllers;

import com.example.inventory_app.Config.TenantContext;
import com.example.inventory_app.Controllers.dto.FacturaCreacionDTO;
import com.example.inventory_app.Controllers.dto.FacturaResponseDTO;
import com.example.inventory_app.Entities.Cliente;
import com.example.inventory_app.Entities.DetalleFactura;
import com.example.inventory_app.Entities.Factura;
import com.example.inventory_app.Entities.Producto;
import com.example.inventory_app.Services.ClienteService;
import com.example.inventory_app.Services.EmpleadoService;
import com.example.inventory_app.Services.FacturaService;
import com.example.inventory_app.Services.ProductoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Controller REST para gestión de facturas en contexto multi-tenant.
 * 
 * @author Sistema Multi-Tenant
 * @version 2.0
 */
@RestController
@RequestMapping("/api/facturas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class FacturaController {

    private final FacturaService facturaService;
    private final ClienteService clienteService;
    private final EmpleadoService empleadoService;
    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<?> listarTodas() {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            log.info("Listando facturas del schema: {}", schemaName);
            
            List<Factura> facturas = facturaService.findAll();
            
            // Mapear facturas con datos completos de cliente y empleado
            List<Map<String, Object>> facturasConDatos = facturas.stream().map(factura -> {
                Map<String, Object> facturaMap = new HashMap<>();
                facturaMap.put("id", factura.getId());
                facturaMap.put("numeroFactura", factura.getNumeroFactura());
                facturaMap.put("fechaEmision", factura.getFecha());
                facturaMap.put("estado", factura.getEstado());
                facturaMap.put("metodoPago", factura.getMetodoPago());
                facturaMap.put("subtotal", factura.getSubtotal());
                facturaMap.put("impuesto", factura.getImpuesto());
                facturaMap.put("descuento", factura.getDescuento());
                facturaMap.put("total", factura.getTotal());
                facturaMap.put("notas", factura.getNotas());
                
                // Agregar datos completos del cliente
                facturaMap.put("clienteId", factura.getClienteId());
                clienteService.findById(factura.getClienteId()).ifPresent(cliente -> {
                    Map<String, Object> clienteMap = new HashMap<>();
                    clienteMap.put("id", cliente.getId());
                    clienteMap.put("nombre", cliente.getNombre());
                    clienteMap.put("apellido", cliente.getApellido());
                    clienteMap.put("cedula", cliente.getDocumento());
                    clienteMap.put("telefono", cliente.getTelefono());
                    clienteMap.put("email", cliente.getEmail());
                    facturaMap.put("cliente", clienteMap);
                    facturaMap.put("clienteNombre", cliente.getNombre() + " " + cliente.getApellido());
                });
                
                // Agregar datos completos del empleado
                facturaMap.put("empleadoId", factura.getEmpleadoId());
                empleadoService.findById(factura.getEmpleadoId()).ifPresent(empleado -> {
                    Map<String, Object> empleadoMap = new HashMap<>();
                    empleadoMap.put("id", empleado.getId());
                    empleadoMap.put("nombre", empleado.getNombre());
                    empleadoMap.put("apellido", empleado.getApellido());
                    empleadoMap.put("usuario", empleado.getUsuario());
                    empleadoMap.put("cargo", empleado.getCargo());
                    empleadoMap.put("rol", empleado.getRol());
                    facturaMap.put("empleado", empleadoMap);
                    facturaMap.put("empleadoNombre", empleado.getNombre() + " " + empleado.getApellido());
                });
                
                // Agregar detalles
                facturaMap.put("detalles", factura.getDetalles());
                
                return facturaMap;
            }).collect(Collectors.toList());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("facturas", facturasConDatos);
            response.put("total", facturasConDatos.size());
            response.put("schemaName", schemaName);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("ERROR al listar facturas: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al listar facturas"));
        }
    }

    @PostMapping
    public ResponseEntity<?> crear(
            @Valid @RequestBody FacturaCreacionDTO facturaDTO,
            HttpServletRequest request) {
        
        try {
            String schemaName = TenantContext.getCurrentTenant();
            Long empresaId = (Long) request.getAttribute("empresaId");
            
            log.info("=== CREAR FACTURA ===");
            log.info("Schema: {}", schemaName);
            log.info("Cliente ID: {}", facturaDTO.getClienteId());
            log.info("Empleado ID: {}", facturaDTO.getEmpleadoId());
            log.info("Cantidad de productos: {}", facturaDTO.getDetalles().size());
            
            if (schemaName == null || schemaName.equals("public")) {
                return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(crearErrorResponse("Error: No se pudo determinar el tenant"));
            }
            
            // Verificar cliente
            Cliente cliente = clienteService.findById(facturaDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            
            // Crear factura
            Factura factura = new Factura();
            factura.setClienteId(cliente.getId());
            factura.setEmpleadoId(facturaDTO.getEmpleadoId());
            factura.setMetodoPago(facturaDTO.getMetodoPago());
            factura.setImpuesto(facturaDTO.getImpuesto() != null ? facturaDTO.getImpuesto() : BigDecimal.ZERO);
            factura.setDescuento(facturaDTO.getDescuento() != null ? facturaDTO.getDescuento() : BigDecimal.ZERO);
            factura.setNotas(facturaDTO.getNotas());

            // Agregar detalles
            for (FacturaCreacionDTO.DetalleFacturaDTO detalleDTO : facturaDTO.getDetalles()) {
                Producto producto = productoService.findById(detalleDTO.getProductoId())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + detalleDTO.getProductoId()));
                
                log.info("=== PROCESANDO DETALLE ===");
                log.info("Producto: {} - Precio: {} - Cantidad: {}", 
                    producto.getNombre(), producto.getPrecioVenta(), detalleDTO.getCantidad());
                log.info("Descuento del detalle: {}", detalleDTO.getDescuento());
                
                DetalleFactura detalle = new DetalleFactura();
                detalle.setProductoId(producto.getId());
                detalle.setProductoCodigo(producto.getCodigo());
                detalle.setProductoNombre(producto.getNombre());
                detalle.setProductoCategoria(producto.getCategoria().toString());
                detalle.setCantidad(detalleDTO.getCantidad());
                detalle.setPrecioUnitario(producto.getPrecioVenta());
                detalle.setDescuento(detalleDTO.getDescuento() != null ? detalleDTO.getDescuento() : BigDecimal.ZERO);
                
                log.info("ANTES de calcularSubtotal() - Subtotal del detalle: {}", detalle.getSubtotal());
                detalle.calcularSubtotal();
                log.info("DESPUÉS de calcularSubtotal() - Subtotal del detalle: {}", detalle.getSubtotal());
                
                factura.addDetalle(detalle);
            }

            log.info("=== ANTES DE GUARDAR FACTURA ===");
            log.info("Subtotal factura: {}", factura.getSubtotal());
            log.info("Total factura: {}", factura.getTotal());

            // Guardar factura
            Factura facturaGuardada = facturaService.create(factura);
            
            log.info("✓ Factura creada: {}", facturaGuardada.getNumeroFactura());
            log.info("  Total: {}", facturaGuardada.getTotal());

            // Preparar respuesta con datos completos
            Map<String, Object> facturaResponse = new HashMap<>();
            facturaResponse.put("id", facturaGuardada.getId());
            facturaResponse.put("numeroFactura", facturaGuardada.getNumeroFactura());
            facturaResponse.put("fechaEmision", facturaGuardada.getFecha());
            facturaResponse.put("estado", facturaGuardada.getEstado());
            facturaResponse.put("metodoPago", facturaGuardada.getMetodoPago());
            facturaResponse.put("subtotal", facturaGuardada.getSubtotal());
            facturaResponse.put("impuesto", facturaGuardada.getImpuesto());
            facturaResponse.put("descuento", facturaGuardada.getDescuento());
            facturaResponse.put("total", facturaGuardada.getTotal());
            facturaResponse.put("notas", facturaGuardada.getNotas());
            facturaResponse.put("detalles", facturaGuardada.getDetalles());
            
            // Agregar cliente completo
            facturaResponse.put("clienteId", facturaGuardada.getClienteId());
            clienteService.findById(facturaGuardada.getClienteId()).ifPresent(c -> {
                Map<String, Object> clienteMap = new HashMap<>();
                clienteMap.put("id", c.getId());
                clienteMap.put("nombre", c.getNombre());
                clienteMap.put("apellido", c.getApellido());
                clienteMap.put("cedula", c.getDocumento());
                facturaResponse.put("cliente", clienteMap);
                facturaResponse.put("clienteNombre", c.getNombre() + " " + c.getApellido());
            });
            
            // Agregar empleado completo
            facturaResponse.put("empleadoId", facturaGuardada.getEmpleadoId());
            empleadoService.findById(facturaGuardada.getEmpleadoId()).ifPresent(e -> {
                Map<String, Object> empleadoMap = new HashMap<>();
                empleadoMap.put("id", e.getId());
                empleadoMap.put("nombre", e.getNombre());
                empleadoMap.put("apellido", e.getApellido());
                empleadoMap.put("usuario", e.getUsuario());
                empleadoMap.put("cargo", e.getCargo());
                empleadoMap.put("rol", e.getRol());
                facturaResponse.put("empleado", empleadoMap);
                facturaResponse.put("empleadoNombre", e.getNombre() + " " + e.getApellido());
            });

            Map<String, Object> finalResponse = new HashMap<>();
            finalResponse.put("success", true);
            finalResponse.put("message", "Factura creada exitosamente");
            finalResponse.put("numeroFactura", facturaGuardada.getNumeroFactura());
            finalResponse.put("factura", facturaResponse);
            finalResponse.put("schemaName", schemaName);

            return ResponseEntity.status(HttpStatus.CREATED).body(finalResponse);
            
        } catch (Exception e) {
            log.error("ERROR al crear factura: {}", e.getMessage());
            e.printStackTrace();
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al crear factura: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerPorId(@PathVariable Long id) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            
            return facturaService.findById(id)
                .map(factura -> ResponseEntity.ok(Map.of(
                    "success", true,
                    "factura", FacturaResponseDTO.fromEntity(factura),
                    "schemaName", schemaName
                )))
                .orElse(ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(crearErrorResponse("Factura no encontrada")));
        } catch (Exception e) {
            log.error("ERROR al obtener factura: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al obtener factura"));
        }
    }

    @GetMapping("/numero/{numeroFactura}")
    public ResponseEntity<?> obtenerPorNumero(@PathVariable String numeroFactura) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            
            return facturaService.findByNumeroFactura(numeroFactura)
                .map(factura -> ResponseEntity.ok(Map.of(
                    "success", true,
                    "factura", FacturaResponseDTO.fromEntity(factura),
                    "schemaName", schemaName
                )))
                .orElse(ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(crearErrorResponse("Factura no encontrada")));
        } catch (Exception e) {
            log.error("ERROR al obtener factura por número: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al obtener factura"));
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<?> listarPorCliente(@PathVariable Long clienteId) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<FacturaResponseDTO> facturas = facturaService.findByCliente(clienteId)
                .stream()
                .map(FacturaResponseDTO::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "facturas", facturas,
                "total", facturas.size(),
                "schemaName", schemaName
            ));
        } catch (Exception e) {
            log.error("ERROR al listar facturas por cliente: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al listar facturas"));
        }
    }

    @GetMapping("/empleado/{empleadoId}")
    public ResponseEntity<?> listarPorEmpleado(@PathVariable Long empleadoId) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<FacturaResponseDTO> facturas = facturaService.findByEmpleado(empleadoId)
                .stream()
                .map(FacturaResponseDTO::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "facturas", facturas,
                "total", facturas.size(),
                "schemaName", schemaName
            ));
        } catch (Exception e) {
            log.error("ERROR al listar facturas por empleado: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al listar facturas"));
        }
    }

    @GetMapping("/fecha")
    public ResponseEntity<?> listarPorRangoFechas(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<FacturaResponseDTO> facturas = facturaService.findByRangoFechas(fechaInicio, fechaFin)
                .stream()
                .map(FacturaResponseDTO::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "facturas", facturas,
                "total", facturas.size(),
                "schemaName", schemaName
            ));
        } catch (Exception e) {
            log.error("ERROR al listar facturas por rango de fechas: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al listar facturas"));
        }
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<?> listarPorEstado(@PathVariable String estado) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            List<FacturaResponseDTO> facturas = facturaService.findByEstado(estado)
                .stream()
                .map(FacturaResponseDTO::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "facturas", facturas,
                "total", facturas.size(),
                "estado", estado,
                "schemaName", schemaName
            ));
        } catch (Exception e) {
            log.error("ERROR al listar facturas por estado: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al listar facturas"));
        }
    }

    @GetMapping("/ventas-dia")
    public ResponseEntity<?> obtenerTotalVentasDia(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fecha) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            Double total = facturaService.calcularTotalVentasDia(fecha);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "fecha", fecha,
                "totalVentas", total,
                "schemaName", schemaName
            ));
        } catch (Exception e) {
            log.error("ERROR al calcular ventas del día: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al calcular ventas"));
        }
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<?> anular(@PathVariable Long id) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            
            if (facturaService.findById(id).isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(crearErrorResponse("Factura no encontrada"));
            }
            
            facturaService.anularFactura(id);
            
            log.info("✓ Factura anulada: {}", id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Factura anulada exitosamente",
                "schemaName", schemaName
            ));
        } catch (Exception e) {
            log.error("ERROR al anular factura: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al anular factura"));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            String schemaName = TenantContext.getCurrentTenant();
            
            if (facturaService.findById(id).isEmpty()) {
                return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(crearErrorResponse("Factura no encontrada"));
            }
            
            facturaService.delete(id);
            
            log.info("✓ Factura eliminada: {}", id);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Factura eliminada exitosamente",
                "schemaName", schemaName
            ));
        } catch (Exception e) {
            log.error("ERROR al eliminar factura: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearErrorResponse("Error al eliminar factura"));
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
