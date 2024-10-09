package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.Cliente;
import com.gestionsimple.sistema_ventas.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    public Optional<Cliente> obtenerClientePorDni(String dni) {
        return clienteRepository.findByDni(dni);
    }

    public void guardarCliente(Cliente cliente) {
        clienteRepository.save(cliente);
    }

    public Cliente registrarCliente(Cliente cliente) {
        return clienteRepository.save(cliente);
    }

    public double obtenerDescuentoPorCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return cliente.getDescuento(); // Supone que tienes un campo "descuento" en la entidad Cliente
    }

    public Optional<Cliente> actualizarCliente(Long id, Cliente detallesCliente) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(id);
        if (clienteOpt.isPresent()) {
            Cliente clienteExistente = clienteOpt.get();
            clienteExistente.setNombre(detallesCliente.getNombre());
            clienteExistente.setApellido(detallesCliente.getApellido());
            clienteExistente.setDni(detallesCliente.getDni());
            clienteExistente.setDireccion(detallesCliente.getDireccion());
            clienteExistente.setTelefono(detallesCliente.getTelefono());

            return Optional.of(clienteRepository.save(clienteExistente));
        }
        return Optional.empty();
    }

    public boolean eliminarCliente(Long id) {
        if (clienteRepository.existsById(id)) {
            clienteRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<Cliente> obtenerTodosLosClientes() {
        return clienteRepository.findAll();
    }
}
