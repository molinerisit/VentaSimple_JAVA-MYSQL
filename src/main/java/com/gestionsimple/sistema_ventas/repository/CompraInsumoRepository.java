package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.Compra;
import com.gestionsimple.sistema_ventas.model.CompraInsumo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompraInsumoRepository extends JpaRepository<CompraInsumo, Long> {
    List<CompraInsumo> findByProveedorIdAndPagadoFalse(Long proveedorId);
    List<CompraInsumo> findByProveedorId(Long proveedorId);
    
}


