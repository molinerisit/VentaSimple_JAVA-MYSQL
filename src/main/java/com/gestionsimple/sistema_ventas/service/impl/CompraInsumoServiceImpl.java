package com.gestionsimple.sistema_ventas.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestionsimple.sistema_ventas.model.CompraInsumo;
import com.gestionsimple.sistema_ventas.repository.CompraInsumoRepository;
import com.gestionsimple.sistema_ventas.service.CompraInsumoService;

@Service
public class CompraInsumoServiceImpl implements CompraInsumoService {

    @Autowired
    private CompraInsumoRepository compraInsumoRepository;

    @Override
    public List<CompraInsumo> getAllCompras() {
        return compraInsumoRepository.findAll();
    }

    @Override
    public void saveCompra(CompraInsumo compraInsumo) {
        compraInsumoRepository.save(compraInsumo);
    }

    @Override
    public CompraInsumo getCompraById(Long id) {
        return compraInsumoRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteCompraById(Long id) {
        compraInsumoRepository.deleteById(id);
    }

    @Override
    public Optional<CompraInsumo> findById(Long id) {
        return compraInsumoRepository.findById(id);
    }
    
    public List<CompraInsumo> getComprasInsumosPendientesPorProveedor(Long proveedorId) {
        return compraInsumoRepository.findByProveedorIdAndPagadoFalse(proveedorId);
    }

}
