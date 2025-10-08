package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.Cliente;
import com.example.inventory_app.Repositories.ClienteRepository;
import com.example.inventory_app.Services.ClienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación de los servicios de Cliente.
 *
 * @author DamianG
 * @version 1.0
 */
@Service
@Transactional
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Override
    public Cliente save(Cliente cliente) {
        if (cliente.getId() == null) {
            cliente.setEstadoActivo(true);
            cliente.setFechaRegistro(new java.util.Date());
        }
        return clienteRepository.save(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> findById(Long id) {
        return clienteRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Cliente> findByDocumento(String documento) {
        return clienteRepository.findByDocumento(documento);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findByNombreOrApellido(String texto) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(texto, texto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAllActive() {
        return clienteRepository.findByEstadoActivoTrue();
    }

    @Override
    public void deactivate(Long id) {
        clienteRepository.findById(id).ifPresent(cliente -> {
            cliente.setEstadoActivo(false);
            clienteRepository.save(cliente);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByDocumento(String documento) {
        return clienteRepository.existsByDocumento(documento);
    }
}
