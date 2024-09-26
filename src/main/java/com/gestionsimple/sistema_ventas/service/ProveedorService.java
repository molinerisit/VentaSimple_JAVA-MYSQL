package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.Compra;
import com.gestionsimple.sistema_ventas.model.CompraInsumo;
import com.gestionsimple.sistema_ventas.model.DeudaProveedor;
import com.gestionsimple.sistema_ventas.model.Proveedor;

import java.util.List;

public interface ProveedorService {
    List<Proveedor> getAllProveedores();
    void saveProveedor(Proveedor proveedor);
    Proveedor getProveedorById(Long id);
    void deleteProveedorById(Long id);
    Proveedor findById(Long id);
    List<Compra> obtenerComprasPorProveedor(Long proveedorId);
    List<CompraInsumo> obtenerCompraInsumoPorProveedor(Long proveedorId); // Cambiado a CompraInsumo
    
    // Métodos nuevos
    List<Compra> obtenerComprasPendientesPorProveedor(Long proveedorId);
    List<CompraInsumo> obtenerInsumosPendientesPorProveedor(Long proveedorId);
	DeudaProveedor getDeudaPorCompraId(Long compraId);
    List<DeudaProveedor> getDeudasPendientesPorProveedor(Long proveedorId);

}
