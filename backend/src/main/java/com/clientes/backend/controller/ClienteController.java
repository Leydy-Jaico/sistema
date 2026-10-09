package com.clientes.backend.controller;

import com.clientes.backend.dto.ClienteRequestDTO;
import com.clientes.backend.model.Cliente;
import com.clientes.backend.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/clientes")
@CrossOrigin(origins = "*")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    public ResponseEntity<Cliente> crear(@Valid @RequestBody ClienteRequestDTO dto) {
        Cliente cliente = clienteService.crear(dto);

        URI location = URI.create("/api/v1/clientes/" + cliente.getId());

        return ResponseEntity.created(location).body(cliente);
    }

    @GetMapping
    public ResponseEntity<?> listarOBuscar(
            @RequestParam(required = false) String dni,
            @RequestParam(required = false) String nombre) {

        if (dni != null && !dni.isBlank()) {
            return ResponseEntity.ok(clienteService.buscarPorDni(dni));
        }

        if (nombre != null && !nombre.isBlank()) {
            return ResponseEntity.ok(clienteService.buscarPorNombre(nombre));
        }

        return ResponseEntity.ok(clienteService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(clienteService.obtenerPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteRequestDTO dto) {

        return ResponseEntity.ok(clienteService.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
