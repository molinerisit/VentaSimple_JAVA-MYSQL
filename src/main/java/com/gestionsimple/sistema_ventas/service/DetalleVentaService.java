package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.DetalleVenta;

import java.time.LocalDateTime;
import java.util.List;

public interface DetalleVentaService {
    List<DetalleVenta> obtenerDetallesVentaPorIdVenta(Long idVenta);
    List<DetalleVenta> obtenerDetallesVentaPorProductoId(Long productoId);
    List<DetalleVenta> obtenerTodosDetallesVenta();
    List<DetalleVenta> obtenerDetallesVentaPorFecha(LocalDateTime fechaHora); // Actualizar tipo a LocalDateTime
    List<DetalleVenta> buscarDetallesVenta(String query); // Nuevo método para búsqueda
    List<DetalleVenta> obtenerDetallesVentaHoy();
    List<DetalleVenta> obtenerDetallesVentaPorRangoFechas(LocalDateTime inicio, LocalDateTime fin);
    List<DetalleVenta> buscarDetallesVentaPorParametros(String query);
    List<DetalleVenta> obtenerDetallesPorVenta(Long ventaId);

    
}
