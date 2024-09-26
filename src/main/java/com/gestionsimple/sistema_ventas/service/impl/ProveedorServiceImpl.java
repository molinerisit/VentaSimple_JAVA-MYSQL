package com.gestionsimple.sistema_ventas.service.impl;

import com.gestionsimple.sistema_ventas.model.Compra;
import com.gestionsimple.sistema_ventas.model.CompraInsumo;
import com.gestionsimple.sistema_ventas.model.DeudaProveedor;
import com.gestionsimple.sistema_ventas.model.Proveedor;
import com.gestionsimple.sistema_ventas.repository.ProveedorRepository;
import com.gestionsimple.sistema_ventas.repository.CompraRepository;
import com.gestionsimple.sistema_ventas.repository.DeudaProveedorRepository;
import com.gestionsimple.sistema_ventas.repository.CompraInsumoRepository;
import com.gestionsimple.sistema_ventas.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProveedorServiceImpl implements ProveedorService {

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private CompraRepository compraRepository;

    @Autowired
    private CompraInsumoRepository compraInsumoRepository;

    @Autowired
    private DeudaProveedorRepository deudaProveedorRepository;

    
    @Override
    public List<Proveedor> getAllProveedores() {
        return proveedorRepository.findAll();
    }

    @Override
    public void saveProveedor(Proveedor proveedor) {
        proveedorRepository.save(proveedor);
    }

    @Override
    public Proveedor getProveedorById(Long id) {
        return proveedorRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteProveedorById(Long id) {
        proveedorRepository.deleteById(id);
    }

    @Override
    public Proveedor findById(Long id) {
        return proveedorRepository.findById(id).orElse(null);
    }

    @Override
    public List<Compra> obtenerComprasPorProveedor(Long proveedorId) {
        return compraRepository.findByProveedorId(proveedorId);
    }

    @Override
    public List<CompraInsumo> obtenerCompraInsumoPorProveedor(Long proveedorId) {
        return compraInsumoRepository.findByProveedorId(proveedorId);
    }

    @Override
    public List<Compra> obtenerComprasPendientesPorProveedor(Long proveedorId) {
        return compraRepository.findByProveedorIdAndPagadoFalse(proveedorId);
    }

    @Override
    public List<CompraInsumo> obtenerInsumosPendientesPorProveedor(Long proveedorId) {
        return compraInsumoRepository.findByProveedorIdAndPagadoFalse(proveedorId);
    }
    
    @Override
    public DeudaProveedor getDeudaPorCompraId(Long compraId) {
        return deudaProveedorRepository.findByCompraId(compraId); // Asumiendo que tienes un repositorio con este método
    }
    
    @Override
    public List<DeudaProveedor> getDeudasPendientesPorProveedor(Long proveedorId) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId).orElse(null);
        if (proveedor != null) {
            // Filtrar las deudas que no estén pagadas
            return proveedor.getDeudas().stream()
                            .filter(deuda -> !deuda.isPagado()) // Suponiendo que `DeudaProveedor` tiene un boolean `pagado`
                            .toList();
        }
        return List.of(); // Devuelve una lista vacía si no se encuentra el proveedor
    }


}
