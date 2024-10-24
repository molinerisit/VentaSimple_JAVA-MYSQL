package com.gestionsimple.sistema_ventas.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
import com.gestionsimple.sistema_ventas.dto.RentabilidadEjercicioDTO;
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
    public String mostrarRentabilidad(@RequestParam(value = "fechaInicio", required = false) String fechaInicioStr,
                                      @RequestParam(value = "fechaFin", required = false) String fechaFinStr,
                                      Model model) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate fechaInicio = null;
        LocalDate fechaFin = null;
        
        if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
            fechaInicio = LocalDate.parse(fechaInicioStr, formatter); // Cambiar a LocalDate
        }

        if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
            fechaFin = LocalDate.parse(fechaFinStr, formatter); // Cambiar a LocalDate
        }

        List<Venta> ventas;
        if (fechaInicio != null && fechaFin != null) {
            ventas = ventaService.obtenerVentasPorRangoFechas(fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59)); // Convierte LocalDate a LocalDateTime
        } else {
            ventas = ventaService.obtenerTodasVentas();
        }

        // Llama a la sobrecarga del método si no se especifican fechas
        List<RentabilidadEjercicioDTO> rentabilidadPorVenta = calcularRentabilidadConDetalles(ventas);

        model.addAttribute("rentabilidadPorVenta", rentabilidadPorVenta);

        return "rentabilidad_ejercicio";
    }




 // Método que acepta solo la lista de ventas
    private List<RentabilidadEjercicioDTO> calcularRentabilidadConDetalles(List<Venta> ventas) {
        // Llama al método principal con fechas predeterminadas
        return calcularRentabilidadConDetalles(ventas, "1900-01-01", "2100-12-31"); // Rango amplio que cubre todo
    }

    // Método principal que usa fechas
    private List<RentabilidadEjercicioDTO> calcularRentabilidadConDetalles(List<Venta> ventas, String fechaInicioString, String fechaFinString) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate fechaInicio = LocalDate.parse(fechaInicioString, formatter);
        LocalDate fechaFin = LocalDate.parse(fechaFinString, formatter);

        List<RentabilidadEjercicioDTO> rentabilidadList = new ArrayList<>();
        for (Venta venta : ventas) {
            RentabilidadEjercicioDTO rentabilidadDTO = new RentabilidadEjercicioDTO();
            rentabilidadDTO.setIdVenta(venta.getId());
            rentabilidadDTO.setFechaHora(venta.getFechaHora());
            rentabilidadDTO.setTotalVenta(BigDecimal.valueOf(venta.getTotal()));

            // Obtener detalles de la venta
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