package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.CompraInsumo;

import java.util.List;
import java.util.Optional;

public interface CompraInsumoService {
    List<CompraInsumo> getAllCompras();
    void saveCompra(CompraInsumo compraInsumo);
    void deleteCompraById(Long id);
    Optional<CompraInsumo> findById(Long id);
    CompraInsumo getCompraById(Long id);
    List<CompraInsumo> getComprasInsumosPendientesPorProveedor(Long proveedorId);

}

