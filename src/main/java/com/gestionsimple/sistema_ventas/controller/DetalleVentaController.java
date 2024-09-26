package com.gestionsimple.sistema_ventas.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gestionsimple.sistema_ventas.dto.VentaConDetallesDTO;
import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import com.gestionsimple.sistema_ventas.model.Venta;
import com.gestionsimple.sistema_ventas.service.DetalleVentaService;

@Controller
@RequestMapping("/detalles-venta")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @GetMapping
    public String mostrarTodosDetallesVenta(@RequestParam(value = "filtro", required = false) String filtro,
                                            @RequestParam(value = "busqueda", required = false) String busqueda,
                                            Model model) {
        List<DetalleVenta> detallesVenta;

        if (busqueda != null && !busqueda.isEmpty()) {
            detallesVenta = detalleVentaService.buscarDetallesVentaPorParametros(busqueda);
        } else if (filtro != null) {
            LocalDateTime inicio;
            LocalDateTime fin = LocalDateTime.now();

            switch (filtro) {
                case "hoy":
                    inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
                    break;
                case "ayer":
                    inicio = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    fin = inicio.plusDays(1).minusNanos(1);
                    break;
                case "semana":
                    inicio = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    break;
                case "mes":
                    inicio = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                    break;
                default:
                    detallesVenta = detalleVentaService.obtenerTodosDetallesVenta();
                    model.addAttribute("ventasConDetalles", procesarDetallesVenta(detallesVenta));
                    return "detalles_venta";
            }

            detallesVenta = detalleVentaService.obtenerDetallesVentaPorRangoFechas(inicio, fin);
        } else {
            detallesVenta = detalleVentaService.obtenerTodosDetallesVenta();
        }

        model.addAttribute("ventasConDetalles", procesarDetallesVenta(detallesVenta));
        return "detalles_venta";
    }

    private List<VentaConDetallesDTO> procesarDetallesVenta(List<DetalleVenta> detallesVenta) {
        Map<Long, VentaConDetallesDTO> ventasMap = new HashMap<>();

        for (DetalleVenta detalle : detallesVenta) {
            Venta venta = detalle.getVenta();
            VentaConDetallesDTO ventaConDetallesDTO = ventasMap.computeIfAbsent(venta.getId(), k -> {
                VentaConDetallesDTO nuevaVenta = new VentaConDetallesDTO();
                nuevaVenta.setIdVenta(venta.getId());
                nuevaVenta.setFechaHoraFormatted(venta.getFechaHora().toString());  // Formatea la fecha según tu preferencia
                nuevaVenta.setMetodoPago(venta.getMetodoPago());
                nuevaVenta.setMontoPagado(venta.getMontoPagado());
                nuevaVenta.setVuelto(venta.getVuelto());
                nuevaVenta.setTotal(venta.getTotal());
                nuevaVenta.setDetallesVenta(new ArrayList<>());
                return nuevaVenta;
            });

            ventaConDetallesDTO.getDetallesVenta().add(detalle);
        }

        return new ArrayList<>(ventasMap.values());
    }
}
