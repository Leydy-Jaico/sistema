package com.clientes.backend.repository;

import com.clientes.backend.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByDni(String dni);

    List<Cliente> findByNombreContainingIgnoreCase(String nombre);

    boolean existsByDni(String dni);
}
