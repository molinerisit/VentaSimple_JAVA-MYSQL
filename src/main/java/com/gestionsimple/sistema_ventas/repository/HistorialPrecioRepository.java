package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.HistorialPrecio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistorialPrecioRepository extends JpaRepository<HistorialPrecio, Long> {
    List<HistorialPrecio> findByProductoId(Long productoId);
}
