package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.DeudaProveedor;
import com.gestionsimple.sistema_ventas.repository.DeudaProveedorRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeudaProveedorService {

    @Autowired
    private DeudaProveedorRepository deudaProveedorRepository;

    // Registrar una nueva deuda
    public void registrarDeuda(Long proveedorId, DeudaProveedor deuda) {
        deudaProveedorRepository.save(deuda);
    }

    // Obtener todas las deudas de un proveedor específico
    public List<DeudaProveedor> obtenerDeudasPorProveedor(Long proveedorId) {
        return deudaProveedorRepository.findByProveedorId(proveedorId);
    }

    // Obtener una deuda específica por su ID
    public DeudaProveedor getDeudaById(Long deudaId) {
        Optional<DeudaProveedor> deudaOpt = deudaProveedorRepository.findById(deudaId);
        return deudaOpt.orElse(null);
    }

    // Guardar o actualizar una deuda
    public void saveDeuda(DeudaProveedor deuda) {
        deudaProveedorRepository.save(deuda);
    }

    // Obtener todas las deudas pendientes (no pagadas) de un proveedor
    public List<DeudaProveedor> getDeudasPendientesPorProveedor(Long proveedorId) {
        return deudaProveedorRepository.findByProveedorIdAndPagadoFalse(proveedorId);
    }
}
