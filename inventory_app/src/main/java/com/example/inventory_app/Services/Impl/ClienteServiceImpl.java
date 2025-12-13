package com.example.inventory_app.Services.Impl;

import com.example.inventory_app.Entities.Cliente;
import com.example.inventory_app.Repositories.ClienteRepository;
import com.example.inventory_app.Services.ClienteService;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;

    @Override
    public Cliente save(Cliente cliente) {
        // @PrePersist se encarga de establecer createdAt, updatedAt y activo=true automáticamente
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
    public Optional<Cliente> findByEmail(String email) {
        return clienteRepository.findByEmail(email);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAll() {
        return clienteRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findAllActive() {
        return clienteRepository.findByActivoTrue();
    }

    @Override
    public void delete(Long id) {
        clienteRepository.deleteById(id);
    }

    @Override
    public void deactivate(Long id) {
        clienteRepository.findById(id).ifPresent(cliente -> {
            cliente.setActivo(false);
            clienteRepository.save(cliente);
        });
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> buscarPorNombre(String busqueda) {
        return clienteRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(busqueda, busqueda);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Cliente> findByCiudad(String ciudad) {
        return clienteRepository.findByCiudad(ciudad);
    }
}
