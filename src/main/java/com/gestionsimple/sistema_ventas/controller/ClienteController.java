package com.gestionsimple.sistema_ventas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import com.gestionsimple.sistema_ventas.model.Cliente;
import com.gestionsimple.sistema_ventas.service.ClienteService;

import java.util.List;
import java.util.Optional;

@Controller // Cambiar a Controller para servir HTML
@RequestMapping("/clientes")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    // Método para servir la página HTML que lista los clientes
    @GetMapping
    public String mostrarClientes(Model model) {
        List<Cliente> listaClientes = clienteService.obtenerTodosLosClientes();
        model.addAttribute("clientes", listaClientes); // Agregar la lista de clientes al modelo
        return "Clientes"; // Nombre de la plantilla HTML
    }

    // Método para mostrar la página de crear cliente
    @GetMapping("/crear")
    public String mostrarCrearCliente(Model model) {
        model.addAttribute("cliente", new Cliente()); // Asegúrate de que el objeto Cliente esté presente
        return "crear_cliente"; // Nombre de la plantilla HTML para crear cliente
    }


    // Obtener cliente por DNI
    @GetMapping("/dni/{dni}")
    public ResponseEntity<Cliente> obtenerClientePorDni(@PathVariable String dni) {
        return clienteService.obtenerClientePorDni(dni)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Registrar nuevo cliente
    @PostMapping("/crear") // Método para manejar el registro de un nuevo cliente
    public String registrarCliente(@ModelAttribute Cliente cliente) {
        clienteService.registrarCliente(cliente);
        return "redirect:/clientes"; // Redirigir a la lista de clientes después de crear
    }

    // Actualizar un cliente existente
    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable Long id, @RequestBody Cliente detallesCliente) {
        Optional<Cliente> clienteActualizado = clienteService.actualizarCliente(id, detallesCliente);
        return clienteActualizado
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Eliminar un cliente por ID
    @GetMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        if (clienteService.eliminarCliente(id)) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
