package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.Empleado;
import com.example.inventory_app.Repositories.EmpleadoRepository;
import com.example.inventory_app.Services.EmpleadoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de Empleado.
 *
 * @author DamianG
 * @version 1.0
 */
@Service
@Transactional
public class EmpleadoServiceImpl implements EmpleadoService {

    @Autowired
    private EmpleadoRepository empleadoRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public Empleado save(Empleado empleado) {
        if (empleado.getId() == null) {
            empleado.setEstadoActivo(true);
            empleado.setFechaContratacion(new java.util.Date());
            // Encriptar contraseña solo para nuevos empleados
            empleado.setPassword(passwordEncoder.encode(empleado.getPassword()));
        }
        return empleadoRepository.save(empleado);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findById(Long id) {
        return empleadoRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findByUsuario(String usuario) {
        return empleadoRepository.findByUsuario(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> findByDocumento(String documento) {
        return empleadoRepository.findByDocumento(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> findByCargo(String cargo) {
        return empleadoRepository.findByCargo(cargo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Empleado> findAllActive() {
        return empleadoRepository.findByEstadoActivoTrue();
    }

    @Override
    public void deactivate(Long id) {
        empleadoRepository.findById(id).ifPresent(empleado -> {
            empleado.setEstadoActivo(false);
            empleadoRepository.save(empleado);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Empleado> verificarCredenciales(String usuario, String password) {
        return empleadoRepository.findByUsuario(usuario)
            .filter(empleado -> empleado.isEstadoActivo() && 
                              passwordEncoder.matches(password, empleado.getPassword()));
    }
}
