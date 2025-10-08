package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.Caja;
import com.example.inventory_app.Repositories.CajaRepository;
import com.example.inventory_app.Services.CajaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de Caja.
 *
 * @author DamianG
 * @version 1.0
 */
@Service
@Transactional
public class CajaServiceImpl implements CajaService {

    @Autowired
    private CajaRepository cajaRepository;

    @Override
    public Caja abrirCaja(Caja caja) {
        if (tieneCajaAbierta(caja.getEmpleado().getId())) {
            throw new RuntimeException("El empleado ya tiene una caja abierta");
        }
        
        caja.setFechaApertura(new Date());
        caja.setEstado("ABIERTA");
        caja.setTotalVentas(BigDecimal.ZERO);
        return cajaRepository.save(caja);
    }

    @Override
    public Caja cerrarCaja(Long id, BigDecimal montoFinal, String observaciones) {
        return cajaRepository.findById(id)
            .map(caja -> {
                if (!"ABIERTA".equals(caja.getEstado())) {
                    throw new RuntimeException("La caja no está abierta");
                }
                
                caja.setFechaCierre(new Date());
                caja.setMontoFinal(montoFinal);
                caja.setObservaciones(observaciones);
                caja.setEstado("CERRADA");
                
                return cajaRepository.save(caja);
            })
            .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Caja> findById(Long id) {
        return cajaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Caja> findByNumeroCaja(String numeroCaja) {
        return cajaRepository.findByNumeroCaja(numeroCaja);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Caja> findByEmpleado(Long empleadoId) {
        return cajaRepository.findByEmpleadoId(empleadoId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Caja> findByEstado(String estado) {
        return cajaRepository.findByEstado(estado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Caja> findByFechaApertura(Date fecha) {
        return cajaRepository.findByFechaApertura(fecha);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean tieneCajaAbierta(Long empleadoId) {
        return cajaRepository.existsByEmpleadoIdAndEstado(empleadoId, "ABIERTA");
    }

    @Override
    public Caja actualizarTotalVentas(Long id, BigDecimal montoVenta) {
        return cajaRepository.findById(id)
            .map(caja -> {
                if (!"ABIERTA".equals(caja.getEstado())) {
                    throw new RuntimeException("La caja no está abierta");
                }
                
                BigDecimal nuevoTotal = caja.getTotalVentas().add(montoVenta);
                caja.setTotalVentas(nuevoTotal);
                
                return cajaRepository.save(caja);
            })
            .orElseThrow(() -> new RuntimeException("Caja no encontrada"));
    }
}
