package com.clientes.backend.service;

import com.clientes.backend.dto.ClienteRequestDTO;
import com.clientes.backend.exception.RecursoNoEncontradoException;
import com.clientes.backend.model.Cliente;
import com.clientes.backend.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente crear(ClienteRequestDTO dto) {
        if (clienteRepository.existsByDni(dto.getDni())) {
            throw new IllegalArgumentException("Ya existe un cliente con ese DNI");
        }

        Cliente cliente = new Cliente();
        mapearDatos(dto, cliente);

        return clienteRepository.save(cliente);
    }

    public List<Cliente> listar() {
        return clienteRepository.findAll();
    }

    public Cliente obtenerPorId(Long id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con ID " + id + " no encontrado"));
    }

    public Cliente buscarPorDni(String dni) {
        return clienteRepository.findByDni(dni)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente con DNI " + dni + " no encontrado"));
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        return clienteRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public Cliente actualizar(Long id, ClienteRequestDTO dto) {
        Cliente cliente = obtenerPorId(id);

        if (!cliente.getDni().equals(dto.getDni()) && clienteRepository.existsByDni(dto.getDni())) {
            throw new IllegalArgumentException("Ya existe otro cliente con ese DNI");
        }

        mapearDatos(dto, cliente);
        return clienteRepository.save(cliente);
    }

    public void eliminar(Long id) {
        Cliente cliente = obtenerPorId(id);
        clienteRepository.delete(cliente);
    }

    private void mapearDatos(ClienteRequestDTO dto, Cliente cliente) {
        cliente.setDni(dto.getDni());
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
    }
}
