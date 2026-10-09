package com.clientes.backend.service;

import com.clientes.backend.dto.ClienteRequestDTO;
import com.clientes.backend.model.Cliente;
import com.clientes.backend.repository.ClienteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock
    private ClienteRepository clienteRepository;

    @InjectMocks
    private ClienteService clienteService;

    private ClienteRequestDTO dto;

    @BeforeEach
    void setUp() {
        dto = new ClienteRequestDTO();
        dto.setDni("12345678");
        dto.setNombre("Leydy");
        dto.setApellido("Jaico");
        dto.setEmail("leydy@gmail.com");
        dto.setTelefono("987654321");
    }

    @Test
    void deberiaCrearCliente() {
        when(clienteRepository.existsByDni("12345678")).thenReturn(false);
        when(clienteRepository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        Cliente creado = clienteService.crear(dto);

        assertNotNull(creado);
        assertEquals(1L, creado.getId());
        assertEquals("12345678", creado.getDni());
        verify(clienteRepository).save(any(Cliente.class));
    }

    @Test
    void deberiaRechazarDniDuplicado() {
        when(clienteRepository.existsByDni("12345678")).thenReturn(true);

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> clienteService.crear(dto)
        );

        assertTrue(ex.getMessage().contains("DNI"));
        verify(clienteRepository, never()).save(any());
    }

    @Test
    void deberiaObtenerClientePorId() {
        Cliente cliente = new Cliente(1L, "12345678", "Leydy", "Jaico", "leydy@gmail.com", "987654321");
        when(clienteRepository.findById(1L)).thenReturn(Optional.of(cliente));

        Cliente encontrado = clienteService.obtenerPorId(1L);

        assertEquals("Leydy", encontrado.getNombre());
    }
}
