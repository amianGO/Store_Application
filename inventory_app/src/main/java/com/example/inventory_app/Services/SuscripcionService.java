package com.example.inventory_app.Services;

import com.example.inventory_app.Config.TenantContext;
import com.example.inventory_app.Entities.*;
import com.example.inventory_app.Repositories.EmpresaRepository;
import com.example.inventory_app.Repositories.SuscripcionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para gestión de suscripciones.
 * 
 * IMPORTANTE: Este servicio trabaja SIEMPRE en el schema PUBLIC
 * porque gestiona entidades globales del sistema.
 * 
 * Responsabilidades:
 * - Crear suscripciones de prueba
 * - Activar planes pagados
 * - Renovar suscripciones
 * - Validar límites de uso
 * - Gestionar terminales activas
 * 
 * @author DamianG
 * @version 1.0
 * @since 2025-11-23
 */
@Service
@Transactional
public class SuscripcionService {

    @Autowired
    private SuscripcionRepository suscripcionRepository;

    @Autowired
    private EmpresaRepository empresaRepository;

    /**
     * Crea una suscripción de prueba para una empresa recién registrada.
     * 
     * Características:
     * - Plan PRUEBA
     * - Duración: 15 días
     * - Estado: PRUEBA
     * - Límites según TipoPlan.PRUEBA
     * 
     * @param empresa Empresa para la cual crear la suscripción
     * @return Suscripción creada
     */
    public Suscripcion crearSuscripcionPrueba(Empresa empresa) {
        TenantContext.resetToDefault();
        try {
            Suscripcion suscripcion = new Suscripcion();
            suscripcion.setEmpresa(empresa);
            suscripcion.setTipoPlan(TipoPlan.PRUEBA);
            suscripcion.setEstado(EstadoSuscripcion.PRUEBA);
            suscripcion.setFechaInicio(LocalDateTime.now());
            suscripcion.setFechaVencimiento(LocalDateTime.now().plusDays(15)); // 15 días de prueba
            suscripcion.setPrecioPagado(0.0);
            suscripcion.setRenovacionAutomatica(false);

            suscripcion = suscripcionRepository.save(suscripcion);

            System.out.println("[SUSCRIPCION-SERVICE] Suscripción de prueba creada para: " + empresa.getEmail());
            System.out.println("[SUSCRIPCION-SERVICE] Vence: " + suscripcion.getFechaVencimiento());

            return suscripcion;
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Activa un plan pagado para una empresa.
     * 
     * @param empresaId ID de la empresa
     * @param tipoPlan Tipo de plan a activar
     * @param meses Duración en meses
     * @param precioPagado Precio pagado
     * @param metodoPago Método de pago usado
     * @param referenciaPago Referencia de la transacción
     * @return Suscripción actualizada
     */
    public Suscripcion activarPlan(Long empresaId, TipoPlan tipoPlan, int meses, 
                                   Double precioPagado, String metodoPago, String referenciaPago) {
        TenantContext.resetToDefault();
        try {
            // Buscar suscripción existente
            Suscripcion suscripcion = suscripcionRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));

            // Actualizar a plan pagado
            suscripcion.setTipoPlan(tipoPlan);
            suscripcion.setEstado(EstadoSuscripcion.ACTIVA);
            suscripcion.setFechaInicio(LocalDateTime.now());
            suscripcion.setFechaVencimiento(LocalDateTime.now().plusMonths(meses));
            suscripcion.setPrecioPagado(precioPagado);
            suscripcion.setMetodoPago(metodoPago);
            suscripcion.setReferenciaPago(referenciaPago);

            // Actualizar límites según el nuevo plan
            suscripcion.setMaxTerminales(tipoPlan.getMaxTerminales());
            suscripcion.setMaxProductos(tipoPlan.getMaxProductos());
            suscripcion.setMaxEmpleados(tipoPlan.getMaxEmpleados());

            suscripcion = suscripcionRepository.save(suscripcion);

            System.out.println("[SUSCRIPCION-SERVICE] Plan activado: " + tipoPlan.getNombre());
            System.out.println("[SUSCRIPCION-SERVICE] Vence: " + suscripcion.getFechaVencimiento());

            return suscripcion;
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Renueva una suscripción existente.
     * 
     * @param suscripcionId ID de la suscripción
     * @param meses Meses a renovar
     * @param precioPagado Precio pagado
     * @param metodoPago Método de pago
     * @param referenciaPago Referencia de pago
     * @return Suscripción renovada
     */
    public Suscripcion renovarSuscripcion(Long suscripcionId, int meses, 
                                         Double precioPagado, String metodoPago, String referenciaPago) {
        TenantContext.resetToDefault();
        try {
            Suscripcion suscripcion = suscripcionRepository.findById(suscripcionId)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));

            // Renovar
            suscripcion.renovar(meses);
            suscripcion.setPrecioPagado(precioPagado);
            suscripcion.setMetodoPago(metodoPago);
            suscripcion.setReferenciaPago(referenciaPago);

            suscripcion = suscripcionRepository.save(suscripcion);

            System.out.println("[SUSCRIPCION-SERVICE] Suscripción renovada por " + meses + " meses");
            System.out.println("[SUSCRIPCION-SERVICE] Nueva fecha de vencimiento: " + suscripcion.getFechaVencimiento());

            return suscripcion;
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Obtiene la suscripción de una empresa.
     * 
     * @param empresa Empresa
     * @return Suscripción de la empresa
     */
    public Suscripcion obtenerSuscripcionDeEmpresa(Empresa empresa) {
        TenantContext.resetToDefault();
        try {
            return suscripcionRepository.findByEmpresa(empresa)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Obtiene la suscripción de una empresa por su ID.
     * 
     * @param empresaId ID de la empresa
     * @return Suscripción de la empresa
     */
    public Suscripcion obtenerSuscripcionPorEmpresaId(Long empresaId) {
        TenantContext.resetToDefault();
        try {
            Empresa empresa = empresaRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Empresa no encontrada"));
            
            return suscripcionRepository.findByEmpresa(empresa)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Registra una terminal activa para una empresa.
     * Valida que no se exceda el límite del plan.
     * 
     * @param empresaId ID de la empresa
     * @return Suscripción actualizada
     * @throws IllegalStateException si se alcanzó el límite
     */
    public Suscripcion registrarTerminalActiva(Long empresaId) {
        TenantContext.resetToDefault();
        try {
            Suscripcion suscripcion = suscripcionRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));

            suscripcion.agregarTerminalActiva();
            suscripcion = suscripcionRepository.save(suscripcion);

            System.out.println("[SUSCRIPCION-SERVICE] Terminal registrada. Total activas: " + suscripcion.getTerminalesActivas());
            
            return suscripcion;
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Libera una terminal activa al hacer logout.
     * 
     * @param empresaId ID de la empresa
     * @return Suscripción actualizada
     */
    public Suscripcion liberarTerminalActiva(Long empresaId) {
        TenantContext.resetToDefault();
        try {
            Suscripcion suscripcion = suscripcionRepository.findById(empresaId)
                .orElseThrow(() -> new IllegalArgumentException("Suscripción no encontrada"));

            suscripcion.removerTerminalActiva();
            suscripcion = suscripcionRepository.save(suscripcion);

            System.out.println("[SUSCRIPCION-SERVICE] Terminal liberada. Total activas: " + suscripcion.getTerminalesActivas());
            
            return suscripcion;
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Verifica suscripciones expiradas y actualiza su estado.
     * Método para ejecutar periódicamente (cron job).
     * 
     * @return Cantidad de suscripciones actualizadas
     */
    public int verificarSuscripcionesExpiradas() {
        TenantContext.resetToDefault();
        try {
            var expiradas = suscripcionRepository.findExpiradas(LocalDateTime.now());
            
            for (Suscripcion suscripcion : expiradas) {
                suscripcion.setEstado(EstadoSuscripcion.EXPIRADA);
                suscripcionRepository.save(suscripcion);
                
                System.out.println("[SUSCRIPCION-SERVICE] Suscripción expirada: " + suscripcion.getEmpresa().getEmail());
            }
            
            if (!expiradas.isEmpty()) {
                System.out.println("[SUSCRIPCION-SERVICE] Total de suscripciones expiradas: " + expiradas.size());
            }
            
            return expiradas.size();
        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Obtiene todos los planes de suscripción disponibles.
     * 
     * @return Lista de planes disponibles
     */
    public List<TipoPlan> obtenerPlanesDisponibles() {
        // Retornar todos los planes excepto el de PRUEBA
        return Arrays.stream(TipoPlan.values())
            .filter(plan -> plan != TipoPlan.PRUEBA) // Excluir PRUEBA (se crea automáticamente)
            .collect(Collectors.toList());
    }
    
    /**
     * Obtiene TODOS los planes incluyendo PRUEBA.
     * 
     * @return Lista de todos los planes
     */
    public List<TipoPlan> obtenerTodosLosPlanes() {
        return Arrays.asList(TipoPlan.values());
    }
}
