package com.example.inventory_app.Services;

import com.example.inventory_app.Config.TenantContext;
import com.example.inventory_app.Config.Rol;
import com.example.inventory_app.Controllers.dto.*;
import com.example.inventory_app.Entities.*;
import com.example.inventory_app.Entities.Empleado;
import com.example.inventory_app.Repositories.EmpresaRepository;
import com.example.inventory_app.Repositories.SuscripcionRepository;
import com.example.inventory_app.Services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Servicio para gestión de empresas en el sistema multi-tenant.
 * 
 * IMPORTANTE: Este servicio trabaja SIEMPRE en el schema PUBLIC
 * porque gestiona entidades globales del sistema.
 * 
 * Responsabilidades:
 * - Registro de nuevas empresas
 * - Autenticación de empresas
 * - Gestión de perfiles empresariales
 * - Creación de schemas para tenants
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Service
@Transactional
public class EmpresaService {

    @Autowired
    private EmpresaRepository empresaRepository;

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private SuscripcionService suscripcionService;
    
    @Autowired
    private EmpleadoService empleadoService;
    
    @Autowired
    private EmailService emailService;
    
    @Autowired
    private org.springframework.jdbc.core.JdbcTemplate jdbcTemplate;

    @Autowired
    private SchemaManagementService schemaManagementService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.example.inventory_app.Config.JwtService jwtService;

    /**
     * Registra una nueva empresa en el sistema.
     * 
     * Proceso:
     * 1. Validar datos únicos (email, NIT)
     * 2. Crear empresa con contraseña encriptada
     * 3. Generar tenant key
     * 4. Crear suscripción de prueba
     * 5. Crear schema dedicado en la base de datos
     * 6. Clonar estructura desde template_schema
     * 
     * @param dto Datos de registro
     * @return EmpresaResponseDTO con datos de la empresa registrada
     * @throws IllegalArgumentException Si hay datos duplicados o inválidos
     */
    public EmpresaResponseDTO registrarEmpresa(EmpresaRegistroDTO dto) {
        // Asegurarnos de estar en schema public
        TenantContext.resetToDefault();

        // Validación 1: Verificar que las contraseñas coincidan
        if (!dto.passwordsCoinciden()) {
            throw new IllegalArgumentException("Las contraseñas no coinciden");
        }

        // Validación 2: Verificar email único
        if (empresaRepository.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Validación 3: Verificar NIT único
        if (empresaRepository.existsByNit(dto.getNit())) {
            throw new IllegalArgumentException("El NIT ya está registrado");
        }

        try {
            // Crear entidad Empresa
            Empresa empresa = new Empresa();
            empresa.setNombre(dto.getNombre());
            empresa.setNombreComercial(dto.getNombreComercial());
            empresa.setNit(dto.getNit());
            empresa.setEmail(dto.getEmail());
            empresa.setPassword(passwordEncoder.encode(dto.getPassword())); // Encriptar
            empresa.setTelefono(dto.getTelefono());
            empresa.setDireccion(dto.getDireccion());
            empresa.setCiudad(dto.getCiudad());
            empresa.setPais(dto.getPais());
            empresa.setIndustria(dto.getIndustria());
            empresa.setNumeroEmpleados(dto.getNumeroEmpleados());
            empresa.setActiva(true);
            empresa.setEmailVerificado(false); // Requiere verificación de email (se generará token automáticamente)

            // Guardar empresa (generará el ID)
            empresa = empresaRepository.save(empresa);

            // Generar y asignar schema name
            String schemaName = empresa.generarSchemaName();
            empresa.setSchemaName(schemaName);
            empresa = empresaRepository.save(empresa);

            // Crear suscripción de prueba (15 días)
            Suscripcion suscripcion = suscripcionService.crearSuscripcionPrueba(empresa);

            // Crear schema dedicado para la empresa
            schemaManagementService.crearSchemaParaTenant(schemaName);

            System.out.println("[EMPRESA-SERVICE] Empresa registrada exitosamente: " + empresa.getEmail());
            System.out.println("[EMPRESA-SERVICE] Schema creado: " + schemaName);
            System.out.println("[EMPRESA-SERVICE] License Key: " + suscripcion.getLicenseKey());

            // Enviar email de verificación
            emailService.enviarEmailVerificacion(
                empresa.getEmail(),
                empresa.getNombre(),
                empresa.getTokenVerificacion()
            );

            return convertirADTO(empresa);

        } catch (Exception e) {
            System.err.println("[ERROR] Error al registrar empresa: " + e.getMessage());
            throw new RuntimeException("Error al registrar empresa: " + e.getMessage(), e);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Autentica una empresa y valida su acceso al sistema.
     * 
     * Validaciones:
     * - Credenciales correctas
     * - Empresa activa
     * - Empresa verificada
     * - Suscripción activa y no expirada
     * 
     * @param dto Credenciales de login
     * @return LoginResponseDTO con token JWT y datos de empresa
     * @throws IllegalArgumentException Si las credenciales son inválidas o no tiene acceso
     */
    public LoginResponseDTO autenticarEmpresa(EmpresaLoginDTO dto) {
        // Asegurarnos de estar en schema public
        TenantContext.resetToDefault();

        try {
            // Buscar empresa por email
            Empresa empresa = empresaRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

            // Verificar contraseña
            if (!passwordEncoder.matches(dto.getPassword(), empresa.getPassword())) {
                throw new IllegalArgumentException("Credenciales inválidas");
            }

            // Verificar que tenga acceso
            if (!empresa.tieneAcceso()) {
                String razon = obtenerRazonAccesoDenegado(empresa);
                throw new IllegalArgumentException("Acceso denegado: " + razon);
            }

            // Actualizar último acceso
            empresa.setUltimoAcceso(LocalDateTime.now());
            empresaRepository.save(empresa);

            System.out.println("[EMPRESA-SERVICE] Login exitoso: " + empresa.getEmail());

            // Generar token JWT con información de tenant
            String token = jwtService.generateTokenForEmpresa(
                empresa.getId(),
                empresa.getEmail(),
                empresa.getTenantKey(),
                empresa.getSchemaName()
            );

            // Crear LoginResponseDTO
            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);
            response.setTipo("Bearer");
            response.setEmpresa(convertirADTO(empresa));

            System.out.println("[EMPRESA-SERVICE] Token JWT generado para tenant: " + empresa.getSchemaName());

            return response;

        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Obtiene el perfil completo de una empresa por su ID.
     * 
     * @param empresaId ID de la empresa
     * @return EmpresaResponseDTO con datos de la empresa
     */
    public EmpresaResponseDTO obtenerEmpresaPorId(Long empresaId) {
        TenantContext.resetToDefault();
        try {
            Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
            return convertirADTO(empresa);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Obtiene una empresa por su email.
     * 
     * @param email Email de la empresa
     * @return Optional con la empresa si existe
     */
    public Optional<Empresa> obtenerEmpresaPorEmail(String email) {
        TenantContext.resetToDefault();
        try {
            return empresaRepository.findByEmail(email);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Obtiene una empresa por su tenant key.
     * Útil para resolver tenant en subdominios.
     * 
     * @param tenantKey Tenant key de la empresa
     * @return Optional con la empresa si existe
     */
    public Optional<Empresa> obtenerEmpresaPorTenantKey(String tenantKey) {
        TenantContext.resetToDefault();
        try {
            return empresaRepository.findByTenantKey(tenantKey);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Obtiene una empresa por su schema name.
     * 
     * @param schemaName Nombre del schema
     * @return Optional con la empresa si existe
     */
    public Optional<Empresa> obtenerEmpresaPorSchema(String schemaName) {
        TenantContext.resetToDefault();
        try {
            return empresaRepository.findBySchemaName(schemaName);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Actualiza el perfil de una empresa.
     * 
     * @param empresaId ID de la empresa
     * @param dto Datos a actualizar
     * @return EmpresaResponseDTO con datos actualizados
     */
    public EmpresaResponseDTO actualizarPerfil(Long empresaId, EmpresaRegistroDTO dto) {
        TenantContext.resetToDefault();
        try {
            Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

            // Actualizar campos permitidos
            if (dto.getNombre() != null) {
                empresa.setNombre(dto.getNombre());
            }
            if (dto.getNombreComercial() != null) {
                empresa.setNombreComercial(dto.getNombreComercial());
            }
            if (dto.getTelefono() != null) {
                empresa.setTelefono(dto.getTelefono());
            }
            if (dto.getDireccion() != null) {
                empresa.setDireccion(dto.getDireccion());
            }
            if (dto.getCiudad() != null) {
                empresa.setCiudad(dto.getCiudad());
            }
            if (dto.getPais() != null) {
                empresa.setPais(dto.getPais());
            }
            if (dto.getIndustria() != null) {
                empresa.setIndustria(dto.getIndustria());
            }
            if (dto.getNumeroEmpleados() != null) {
                empresa.setNumeroEmpleados(dto.getNumeroEmpleados());
            }

            empresa = empresaRepository.save(empresa);
            return convertirADTO(empresa);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Verifica el email de una empresa usando el token.
     * 
     * @param token Token de verificación
     * @return EmpresaResponseDTO con datos de la empresa verificada
     */
    public EmpresaResponseDTO verificarEmailConToken(String token) {
        TenantContext.resetToDefault();
        try {
            System.out.println("[EMPRESA-SERVICE] Verificando email con token: " + token);
            
            Empresa empresa = empresaRepository.findByTokenVerificacion(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de verificación inválido o expirado"));

            System.out.println("[EMPRESA-SERVICE] Empresa encontrada: " + empresa.getNombre());
            
            // Usar método de la entidad para verificar
            empresa.verificarEmail();
            empresa = empresaRepository.save(empresa);

            System.out.println("[EMPRESA-SERVICE] Email verificado exitosamente: " + empresa.getEmail());
            
            return convertirADTO(empresa);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Reenvía el email de verificación generando un nuevo token.
     * 
     * @param email Email de la empresa
     * @return EmpresaResponseDTO con nuevo token generado
     */
    public EmpresaResponseDTO reenviarEmailVerificacion(String email) {
        TenantContext.resetToDefault();
        try {
            Empresa empresa = empresaRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));

            if (empresa.isEmailVerificado()) {
                throw new IllegalArgumentException("El email ya está verificado");
            }

            // Regenerar token
            empresa.regenerarTokenVerificacion();
            empresa = empresaRepository.save(empresa);

            System.out.println("[EMPRESA-SERVICE] Nuevo token generado para: " + empresa.getEmail());
            
            // Enviar email con nuevo token
            emailService.enviarEmailVerificacion(
                empresa.getEmail(),
                empresa.getNombre(),
                empresa.getTokenVerificacion()
            );
            
            return convertirADTO(empresa);
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Convierte una Empresa a EmpresaResponseDTO.
     * 
     * @param empresa Empresa a convertir
     * @return DTO de respuesta
     */
    public EmpresaResponseDTO convertirADTO(Empresa empresa) {
        EmpresaResponseDTO dto = new EmpresaResponseDTO();
        dto.setId(empresa.getId());
        dto.setNombre(empresa.getNombre());
        dto.setNombreComercial(empresa.getNombreComercial());
        dto.setNit(empresa.getNit());
        dto.setEmail(empresa.getEmail());
        dto.setTelefono(empresa.getTelefono());
        dto.setDireccion(empresa.getDireccion());
        dto.setCiudad(empresa.getCiudad());
        dto.setPais(empresa.getPais());
        dto.setTenantKey(empresa.getTenantKey());
        dto.setSchemaName(empresa.getSchemaName());
        dto.setActiva(empresa.isActiva());
        dto.setEmailVerificado(empresa.isEmailVerificado());
        dto.setFechaVerificacion(empresa.getFechaVerificacion());
        dto.setFechaRegistro(empresa.getFechaRegistro());
        dto.setLogo(empresa.getLogo());
        dto.setSitioWeb(empresa.getSitioWeb());

        // Agregar información de suscripción
        if (empresa.getSuscripcionActiva() != null) {
            Suscripcion sub = empresa.getSuscripcionActiva();
            EmpresaResponseDTO.SuscripcionInfoDTO subInfo = new EmpresaResponseDTO.SuscripcionInfoDTO();
            subInfo.setTipoPlan(sub.getTipoPlan().getNombre());
            subInfo.setEstado(sub.getEstado().getDescripcion());
            subInfo.setFechaVencimiento(sub.getFechaVencimiento());
            subInfo.setDiasRestantes(sub.diasRestantes());
            subInfo.setLicenseKey(sub.getLicenseKey());
            subInfo.setTerminalesActivas(sub.getTerminalesActivas());
            subInfo.setMaxTerminales(sub.getMaxTerminales());
            dto.setSuscripcion(subInfo);
        }

        return dto;
    }

    /**
     * Obtiene la razón por la cual una empresa no tiene acceso.
     * 
     * @param empresa Empresa a validar
     * @return Mensaje descriptivo
     */
    private String obtenerRazonAccesoDenegado(Empresa empresa) {
        if (!empresa.isActiva()) {
            return "Empresa desactivada. Contacte a soporte.";
        }
        if (!empresa.isEmailVerificado()) {
            return "Email no verificado. Revise su correo.";
        }
        if (empresa.getSuscripcionActiva() == null) {
            return "No tiene suscripción activa.";
        }
        if (!empresa.getSuscripcionActiva().estaActiva()) {
            return "Suscripción expirada o suspendida. Renueve su plan.";
        }
        return "Acceso denegado por razones de seguridad.";
    }

    /**
     * Crea el primer empleado ADMIN para una empresa usando JdbcTemplate.
     * Este método NO requiere JWT de empleado, solo de empresa.
     * 
     * @param empresaId ID de la empresa
     * @param dto Datos del primer empleado
     * @return Empleado creado
     */
    @Transactional
    public Empleado crearPrimerEmpleado(Long empresaId, PrimerEmpleadoDTO dto) {
        TenantContext.resetToDefault();
        try {
            // Verificar que la empresa existe
            Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
            
            System.out.println("[EMPRESA-SERVICE] Creando primer empleado para: " + empresa.getNombre());
            System.out.println("[EMPRESA-SERVICE] Schema: " + empresa.getSchemaName());
            
            // Validar contraseñas
            if (!dto.passwordsCoinciden()) {
                throw new IllegalArgumentException("Las contraseñas no coinciden");
            }
            
            // Hashear password
            String hashedPassword = passwordEncoder.encode(dto.getPassword());
            
            // Usar JdbcTemplate para insertar directamente en el schema correcto
            jdbcTemplate.execute("SET search_path TO " + empresa.getSchemaName());
            
            String sql = "INSERT INTO empleados " +
                        "(nombre, apellido, documento, usuario, password, telefono, email, cargo, rol, estado_activo, fecha_contratacion) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";
            
            Long empleadoId = jdbcTemplate.queryForObject(sql, Long.class,
                dto.getNombre(),
                dto.getApellido(),
                dto.getDocumento(),
                dto.getUsuario(),
                hashedPassword,
                dto.getTelefono(),
                dto.getEmail(),
                "Administrador",
                "ADMIN",
                true,
                new java.sql.Timestamp(System.currentTimeMillis())
            );
            
            System.out.println("[EMPRESA-SERVICE] ✓ Primer empleado creado con ID: " + empleadoId);
            
            // Resetear search_path
            jdbcTemplate.execute("SET search_path TO public");
            
            // Crear objeto Empleado para retornar
            Empleado empleado = new Empleado();
            empleado.setId(empleadoId);
            empleado.setNombre(dto.getNombre());
            empleado.setApellido(dto.getApellido());
            empleado.setDocumento(dto.getDocumento());
            empleado.setUsuario(dto.getUsuario());
            empleado.setEmail(dto.getEmail());
            empleado.setTelefono(dto.getTelefono());
            empleado.setCargo("Administrador");
            empleado.setRol(Rol.ADMIN);
            empleado.setEstadoActivo(true);
            
            return empleado;
            
        } catch (Exception e) {
            System.err.println("[EMPRESA-SERVICE] Error al crear empleado: " + e.getMessage());
            throw e;
        } finally {
            try {
                jdbcTemplate.execute("SET search_path TO public");
            } catch (Exception e) {
                // Ignorar
            }
            TenantContext.clear();
        }
    }
}
