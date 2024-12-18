package com.gestionsimple.sistema_ventas.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gestionsimple.sistema_ventas.dto.BalanceDTO;
import com.gestionsimple.sistema_ventas.dto.InsumoDTO;
import com.gestionsimple.sistema_ventas.dto.RentabilidadEjercicioDTO;
import com.gestionsimple.sistema_ventas.model.Balance;
import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import com.gestionsimple.sistema_ventas.model.Insumo;
import com.gestionsimple.sistema_ventas.model.Venta;
import com.gestionsimple.sistema_ventas.repository.InsumoRepository;
import com.gestionsimple.sistema_ventas.service.BalanceService;
import com.gestionsimple.sistema_ventas.service.DetalleVentaService;
import com.gestionsimple.sistema_ventas.service.InsumoService;
import com.gestionsimple.sistema_ventas.service.RentabilidadEjercicioService;
import com.gestionsimple.sistema_ventas.service.VentaService;

@Controller
@RequestMapping("/rentabilidad-ejercicio")
public class RentabilidadEjercicioController {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private InsumoService insumoService;
    
    @Autowired
    private InsumoRepository insumoRepository;

    @Autowired
    private BalanceService balanceService;
    
    @Autowired
    private RentabilidadEjercicioService rentabilidadEjercicioService;

    @Autowired
    private DetalleVentaService detalleVentaService;

    @GetMapping
    public String mostrarRentabilidad(@RequestParam(value = "fechaInicio", required = false) String fechaInicioStr,
                                      @RequestParam(value = "fechaFin", required = false) String fechaFinStr,
                                      Model model) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yy");

        LocalDate fechaInicio = null;
        LocalDate fechaFin = null;

        if (fechaInicioStr != null && !fechaInicioStr.isEmpty()) {
            fechaInicio = LocalDate.parse(fechaInicioStr, inputFormatter);
        }
        if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
            fechaFin = LocalDate.parse(fechaFinStr, inputFormatter);
        }

        List<Balance> balances = balanceService.obtenerTodosBalances();
        List<BalanceDTO> balanceDTOs = balances.stream().map(balance -> {
            BalanceDTO dto = new BalanceDTO();
            dto.setId(balance.getId());
            dto.setGananciaTotal(BigDecimal.valueOf(balance.getGananciaTotal()));
            dto.setInversionTotal(BigDecimal.valueOf(balance.getInversionTotal()));
            dto.setDineroTotalRecaudado(BigDecimal.valueOf(balance.getDineroTotalRecaudado()));
            dto.setGastoInsumos(BigDecimal.valueOf(balance.getGastoInsumos()));
            dto.setFechaCreacion(balance.getFechaCreacion());
            
            // Establecer las fechas de compra en el DTO
            dto.setFechasCompras(balance.getFechasCompras());

            return dto;
        }).toList();

        model.addAttribute("balances", balanceDTOs);

        List<Venta> ventas;
        if (fechaInicio != null && fechaFin != null) {
            ventas = ventaService.obtenerVentasPorRangoFechas(fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));
        } else {
            ventas = ventaService.obtenerTodasVentas();
        }

        List<RentabilidadEjercicioDTO> rentabilidadPorVenta = calcularRentabilidadConDetalles(ventas);
        BigDecimal gananciaTotalPeriodo = calcularGananciaTotalPeriodo(rentabilidadPorVenta);
        model.addAttribute("rentabilidadPorVenta", rentabilidadPorVenta);
        model.addAttribute("gananciaTotalPeriodo", gananciaTotalPeriodo);

        List<Insumo> insumos = insumoService.obtenerTodosLosInsumos();
        model.addAttribute("insumos", insumos);

        return "rentabilidad_ejercicio";
    }

    private BigDecimal calcularGananciaTotalPeriodo(List<RentabilidadEjercicioDTO> rentabilidadPorVenta) {
        BigDecimal gananciaTotal = BigDecimal.ZERO;
        for (RentabilidadEjercicioDTO rentabilidad : rentabilidadPorVenta) {
            gananciaTotal = gananciaTotal.add(rentabilidad.getGananciaTotal());
        }
        return gananciaTotal;
    }

    private List<RentabilidadEjercicioDTO> calcularRentabilidadConDetalles(List<Venta> ventas) {
        List<RentabilidadEjercicioDTO> rentabilidadList = new ArrayList<>();
        for (Venta venta : ventas) {
            RentabilidadEjercicioDTO rentabilidadDTO = new RentabilidadEjercicioDTO();
            rentabilidadDTO.setIdVenta(venta.getId());

            LocalDateTime fechaHora = venta.getFechaHora().withHour(0).withMinute(0).withSecond(0).withNano(0);
            rentabilidadDTO.setFechaHora(fechaHora);
            rentabilidadDTO.setMetodoPago(venta.getMetodoPago());

            List<DetalleVenta> detallesVenta = detalleVentaService.obtenerDetallesPorVenta(venta.getId());
            rentabilidadDTO.setDetallesVenta(detallesVenta);

            BigDecimal totalGanancia = BigDecimal.ZERO;
            BigDecimal subtotal = BigDecimal.valueOf(venta.getSubtotalSinDescuentos());
            BigDecimal descuento = BigDecimal.valueOf(venta.getMontoDescuento());
            BigDecimal recargo = BigDecimal.valueOf(venta.getRecargo());
            
            List<BigDecimal> gananciasPorProducto = new ArrayList<>();

            for (DetalleVenta detalle : detallesVenta) {
                BigDecimal precioVenta;
                BigDecimal precioCompra;

                if (detalle.getProducto() != null) {
                    precioVenta = detalle.getProducto().getPrecioVenta();
                    precioCompra = detalle.getProducto().getPrecioCompra();
                } else {
                    precioVenta = BigDecimal.valueOf(detalle.getPrecio());
                    precioCompra = precioVenta.subtract(precioVenta.multiply(BigDecimal.valueOf(0.30)));
                }

                BigDecimal gananciaProducto = precioVenta.subtract(precioCompra);
                totalGanancia = totalGanancia.add(gananciaProducto.multiply(BigDecimal.valueOf(detalle.getCantidad())));
                gananciasPorProducto.add(gananciaProducto.multiply(BigDecimal.valueOf(detalle.getCantidad())));
            }

            rentabilidadDTO.setGananciaTotal(totalGanancia);
            rentabilidadDTO.setSubtotal(subtotal);
            rentabilidadDTO.setDescuento(descuento);
            rentabilidadDTO.setRecargo(recargo);
            rentabilidadDTO.setGananciasPorProducto(gananciasPorProducto);
            
            BigDecimal totalVenta = subtotal.subtract(descuento).add(recargo);
            rentabilidadDTO.setTotalVenta(totalVenta);

            rentabilidadList.add(rentabilidadDTO);
        }

        return rentabilidadList;
    }

    @PostMapping("/actualizarStockInsumos")
    public ResponseEntity<String> actualizarStockInsumos(@RequestBody List<InsumoDTO> insumos) {
        try {
            for (InsumoDTO insumoDTO : insumos) {
                Insumo insumo = insumoRepository.findById(insumoDTO.getId())
                        .orElseThrow(() -> new RuntimeException("Insumo no encontrado: " + insumoDTO.getId()));

                BigDecimal cantidadARestar = insumoDTO.getCantidad();

                if (insumo.getStock() < cantidadARestar.intValue()) {
                    return ResponseEntity.badRequest().body("No hay suficiente stock para el insumo: " + insumoDTO.getId());
                }

                insumo.setStock(insumo.getStock() - cantidadARestar.intValue());
                insumoRepository.save(insumo);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el stock: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }

    @PostMapping("/guardarInsumosSeleccionados")
    public ResponseEntity<?> guardarInsumosSeleccionados(@RequestBody List<Long> insumoIds) {
        try {
            for (Long insumoId : insumoIds) {
                Insumo insumo = insumoService.obtenerInsumoPorId(insumoId);
            }
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar insumos seleccionados");
        }
    }
}
