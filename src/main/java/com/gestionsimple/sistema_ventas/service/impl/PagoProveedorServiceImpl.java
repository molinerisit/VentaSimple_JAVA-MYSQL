package com.gestionsimple.sistema_ventas.service.impl;

import com.gestionsimple.sistema_ventas.model.PagoProveedor;
import com.gestionsimple.sistema_ventas.model.Proveedor;
import com.gestionsimple.sistema_ventas.repository.PagoProveedorRepository;
import com.gestionsimple.sistema_ventas.service.PagoProveedorService;
import com.gestionsimple.sistema_ventas.service.ProveedorService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PagoProveedorServiceImpl implements PagoProveedorService {

    @Autowired
    private PagoProveedorRepository pagoProveedorRepository;

    @Autowired  // Asegúrate de inyectar el ProveedorService
    private ProveedorService proveedorService;

    @Override
    @Transactional
    public void registrarPago(Long proveedorId, PagoProveedor pagoProveedor) {
        Proveedor proveedor = proveedorService.getProveedorById(proveedorId);
        if (proveedor != null) {
            pagoProveedor.setProveedor(proveedor);
            
            if (pagoProveedor.getId() != null) {
                // Si hay ID, actualizamos
                PagoProveedor existingPago = pagoProveedorRepository.findById(pagoProveedor.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Pago no encontrado con ID: " + pagoProveedor.getId()));

                // Actualizar los campos
                existingPago.setMonto(pagoProveedor.getMonto());
                existingPago.setDescripcion(pagoProveedor.getDescripcion());
                existingPago.setMedioDePago(pagoProveedor.getMedioDePago());
                existingPago.setPagado(pagoProveedor.isPagado());
                existingPago.setFecha_pago(pagoProveedor.getFecha_pago());

                // Guardar el pago actualizado
                pagoProveedorRepository.save(existingPago);
            } else {
                // Si no hay ID, es un nuevo pago
                pagoProveedorRepository.save(pagoProveedor);
            }
        } else {
            throw new IllegalArgumentException("Proveedor no encontrado con ID: " + proveedorId);
        }
    }


}



