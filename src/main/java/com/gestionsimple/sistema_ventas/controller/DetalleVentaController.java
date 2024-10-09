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
import java.time.format.DateTimeFormatter;
import com.gestionsimple.sistema_ventas.service.DetalleVentaService;

@Controller
@RequestMapping("/detalles-venta")
public class DetalleVentaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @GetMapping
    public String mostrarTodosDetallesVenta(@RequestParam(value = "filtro", required = false) String filtro,
                                            @RequestParam(value = "busqueda", required = false) String busqueda,
                                            @RequestParam(value = "orden", required = false, defaultValue = "desc") String orden,
                                            Model model) {
        System.out.println("Filtro: " + filtro + ", Búsqueda: " + busqueda + ", Orden: " + orden);

        List<DetalleVenta> detallesVenta;

        // Filtrado por búsqueda
        // Verificar si 'filtro' es null y manejar el caso
        if (filtro == null) {
            // Manejo de caso si filtro es null, por ejemplo, asignar un valor predeterminado o devolver todos los detalles
            detallesVenta = detalleVentaService.obtenerTodosDetallesVenta();
        } else {
            // Filtrado por búsqueda
            if (busqueda != null && !busqueda.isEmpty()) {
                detallesVenta = detalleVentaService.buscarDetallesVentaPorParametros(busqueda);
            } else {
                // Filtrado por fecha
                LocalDateTime inicio;
                LocalDateTime fin = LocalDateTime.now();
                
                switch (filtro) {
                    case "hoy":
                        inicio = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
                        detallesVenta = detalleVentaService.obtenerDetallesVentaPorRangoFechas(inicio, fin);
                        break;
                    case "ayer":
                        inicio = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                        fin = inicio.plusDays(1).minusNanos(1);
                        detallesVenta = detalleVentaService.obtenerDetallesVentaPorRangoFechas(inicio, fin);
                        break;
                    case "semana":
                        inicio = LocalDateTime.now().minusDays(LocalDateTime.now().getDayOfWeek().getValue() - 1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                        detallesVenta = detalleVentaService.obtenerDetallesVentaPorRangoFechas(inicio, fin);
                        break;
                    case "mes":
                        inicio = LocalDateTime.now().withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
                        detallesVenta = detalleVentaService.obtenerDetallesVentaPorRangoFechas(inicio, fin);
                        break;
                    default:
                        detallesVenta = detalleVentaService.obtenerTodosDetallesVenta();
                        break;
                }
        }
        }
        // Ordenar los detalles de venta antes de procesarlos
        if ("asc".equals(orden)) {
            detallesVenta.sort((d1, d2) -> d1.getVenta().getFechaHora().compareTo(d2.getVenta().getFechaHora()));
        } else {
            detallesVenta.sort((d1, d2) -> d2.getVenta().getFechaHora().compareTo(d1.getVenta().getFechaHora()));
        }

        model.addAttribute("ventasConDetalles", procesarDetallesVenta(detallesVenta));
        return "detalles_venta";
    }

    private List<VentaConDetallesDTO> procesarDetallesVenta(List<DetalleVenta> detallesVenta) {
        Map<Long, VentaConDetallesDTO> ventasMap = new HashMap<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

        for (DetalleVenta detalle : detallesVenta) {
            Venta venta = detalle.getVenta();
            VentaConDetallesDTO ventaConDetallesDTO = ventasMap.computeIfAbsent(venta.getId(), k -> {
                VentaConDetallesDTO nuevaVenta = new VentaConDetallesDTO();
                nuevaVenta.setIdVenta(venta.getId());

                // Formatear la fecha
                if (venta.getFechaHora() != null) {
                    nuevaVenta.setFechaHoraFormatted(venta.getFechaHora().format(formatter));
                } else {
                    nuevaVenta.setFechaHoraFormatted("Fecha no disponible");
                }

                nuevaVenta.setMetodoPago(venta.getMetodoPago());
                nuevaVenta.setMontoPagado(venta.getMontoPagado());
                nuevaVenta.setVuelto(venta.getVuelto());
                nuevaVenta.setSubtotalSinDescuentos(venta.getSubtotalSinDescuentos());
                nuevaVenta.setTotal(venta.getTotal());
                nuevaVenta.setDniCliente(venta.getDniCliente()); // Agregar DNI del cliente
                nuevaVenta.setMontoDescuento(venta.getMontoDescuento()); // Agregar descuento
                nuevaVenta.setRecargo(venta.getRecargo()); // Agregar recargo aquí

                nuevaVenta.setDetallesVenta(new ArrayList<>());
                return nuevaVenta;
            });

            // Añadir detalle a la venta
            ventaConDetallesDTO.getDetallesVenta().add(detalle);
        }

        // Convertir a lista y ordenar por ID (o por fecha si es necesario)
        List<VentaConDetallesDTO> listaVentas = new ArrayList<>(ventasMap.values());
        listaVentas.sort((v1, v2) -> Long.compare(v2.getIdVenta(), v1.getIdVenta())); // Ordenar de más reciente a más antiguo

        return listaVentas;
    }


}
