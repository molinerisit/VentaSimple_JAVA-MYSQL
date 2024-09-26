package com.gestionsimple.sistema_ventas.controller;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gestionsimple.sistema_ventas.dto.BalanceDTO;
import com.gestionsimple.sistema_ventas.dto.RentabilidadDTO;
import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import com.gestionsimple.sistema_ventas.model.Venta;
import com.gestionsimple.sistema_ventas.service.DetalleVentaService;
import com.gestionsimple.sistema_ventas.service.RentabilidadEjercicioService;
import com.gestionsimple.sistema_ventas.service.VentaService;

@Controller
@RequestMapping("/rentabilidad-ejercicio")
public class RentabilidadEjercicioController {

    @Autowired
    private VentaService ventaService;


    @Autowired
    private RentabilidadEjercicioService rentabilidadEjercicioService;

    @Autowired
    private DetalleVentaService detalleVentaService;

    @GetMapping
    public String mostrarRentabilidad(@RequestParam(value = "fechaInicio", required = false) LocalDateTime fechaInicio,
                                      @RequestParam(value = "fechaFin", required = false) LocalDateTime fechaFin,
                                      Model model) {
        List<Venta> ventas;
        if (fechaInicio != null && fechaFin != null) {
            ventas = ventaService.obtenerVentasPorRangoFechas(fechaInicio, fechaFin);
        } else {
            ventas = ventaService.obtenerTodasVentas();
        }

        // Calcular la rentabilidad de cada venta
        List<RentabilidadDTO> rentabilidadPorVenta = calcularRentabilidadConDetalles(ventas);

        model.addAttribute("rentabilidadPorVenta", rentabilidadPorVenta);
        return "rentabilidad_ejercicio";
    }

    private List<RentabilidadDTO> calcularRentabilidadConDetalles(List<Venta> ventas) {
        List<RentabilidadDTO> rentabilidadList = new ArrayList<>();
        for (Venta venta : ventas) {
            RentabilidadDTO rentabilidadDTO = new RentabilidadDTO();
            rentabilidadDTO.setIdVenta(venta.getId());
            rentabilidadDTO.setFechaHora(venta.getFechaHora());
            rentabilidadDTO.setTotalVenta(BigDecimal.valueOf(venta.getTotal()));

            // Obtener los detalles de la venta
            List<DetalleVenta> detallesVenta = detalleVentaService.obtenerDetallesPorVenta(venta.getId());

            BigDecimal totalGanancia = BigDecimal.ZERO;

            for (DetalleVenta detalle : detallesVenta) {
                BigDecimal precioVenta = detalle.getProducto().getPrecioVenta();
                BigDecimal precioCompra = detalle.getProducto().getPrecioCompra();
                BigDecimal gananciaProducto = precioVenta.subtract(precioCompra);
                totalGanancia = totalGanancia.add(gananciaProducto.multiply(BigDecimal.valueOf(detalle.getCantidad())));
            }

            rentabilidadDTO.setGananciaTotal(totalGanancia);
            rentabilidadList.add(rentabilidadDTO);
        }

        return rentabilidadList;
    }

    @PostMapping("/guardarBalance")
    public ResponseEntity<?> guardarBalance(@RequestBody List<BalanceDTO> balances) {
        try {
            for (BalanceDTO balance : balances) {
                rentabilidadEjercicioService.guardarBalanceDesdeDTO(balance);
            }
            return ResponseEntity.ok().build(); // Respuesta 200 OK
        } catch (Exception e) {
            e.printStackTrace(); // Para ver el error en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar el balance");
        }
    }


}