package com.gestionsimple.sistema_ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.gestionsimple.sistema_ventas.model.Venta;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    // Método para obtener ventas dentro de un rango de fechas
    List<Venta> findByFechaHoraBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
