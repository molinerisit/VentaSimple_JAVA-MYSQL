package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta, Long> {

    List<DetalleVenta> findByProductoId(Long productoId);

    List<DetalleVenta> findByVentaId(Long ventaId);

    List<DetalleVenta> findByFecha(LocalDate fechaHoy);

    List<DetalleVenta> findByVentaFechaHora(LocalDateTime fechaHora);
    
    @Query("SELECT d FROM DetalleVenta d WHERE d.venta.fechaHora BETWEEN :startOfDay AND :endOfDay")
    List<DetalleVenta> findByVentaFechaHoraBetween(@Param("startOfDay") LocalDateTime startOfDay, @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT dv FROM DetalleVenta dv WHERE " +
            "dv.venta.metodoPago LIKE %:query% OR " +
            "dv.producto.nombre LIKE %:query%")
     List<DetalleVenta> findByQuery(@Param("query") String query);
    
    
    @Query("SELECT HOUR(d.venta.fechaHora) as hora, COUNT(d) as cantidad FROM DetalleVenta d GROUP BY HOUR(d.venta.fechaHora)")
    List<Object[]> obtenerHorariosMasConcurridos();

    @Query("SELECT DATE(d.venta.fechaHora) as fecha, COUNT(d) as cantidad FROM DetalleVenta d GROUP BY DATE(d.venta.fechaHora)")
    List<Object[]> obtenerDiasMasConcurridos();

    @Query("SELECT d.venta.metodoPago, COUNT(d) as cantidad FROM DetalleVenta d GROUP BY d.venta.metodoPago")
    List<Object[]> obtenerMediosDePago();

    @Query("SELECT d.producto.nombre, SUM(d.cantidad) as cantidad FROM DetalleVenta d GROUP BY d.producto.nombre ORDER BY cantidad DESC")
    List<Object[]> obtenerProductosMasVendidos();
    
    @Query("SELECT SUM(d.subtotal) FROM DetalleVenta d")
    Double obtenerTotalRecaudado();

    @Query("SELECT SUM(d.subtotal) FROM DetalleVenta d WHERE DATE(d.venta.fechaHora) = :dia")
    Double obtenerRecaudadoPorDia(@Param("dia") LocalDate dia);
}
