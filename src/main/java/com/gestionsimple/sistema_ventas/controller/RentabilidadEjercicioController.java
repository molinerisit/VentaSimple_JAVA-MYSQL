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
import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import com.gestionsimple.sistema_ventas.model.Insumo;
import com.gestionsimple.sistema_ventas.model.Venta;
import com.gestionsimple.sistema_ventas.repository.InsumoRepository;
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
            fechaInicio = LocalDate.parse(fechaInicioStr, formatter);
        }

        if (fechaFinStr != null && !fechaFinStr.isEmpty()) {
            fechaFin = LocalDate.parse(fechaFinStr, formatter);
        }

        List<Venta> ventas;
        if (fechaInicio != null && fechaFin != null) {
            ventas = ventaService.obtenerVentasPorRangoFechas(fechaInicio.atStartOfDay(), fechaFin.atTime(23, 59, 59));
        } else {
            ventas = ventaService.obtenerTodasVentas();
        }

        // Obtiene la rentabilidad y detalles de las ventas
        List<RentabilidadEjercicioDTO> rentabilidadPorVenta = calcularRentabilidadConDetalles(ventas);
        BigDecimal gananciaTotalPeriodo = calcularGananciaTotalPeriodo(rentabilidadPorVenta); // Calcular la ganancia total del período
        model.addAttribute("rentabilidadPorVenta", rentabilidadPorVenta);
        model.addAttribute("gananciaTotalPeriodo", gananciaTotalPeriodo); // Agregar al modelo

        // Obtener todos los insumos y agregarlos al modelo
        List<Insumo> insumos = insumoService.obtenerTodosLosInsumos();
        model.addAttribute("insumos", insumos); // Agregar insumos al modelo

        return "rentabilidad_ejercicio";
    }

    // Método para calcular la ganancia total del período
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

            // Obtener detalles de la venta
            List<DetalleVenta> detallesVenta = detalleVentaService.obtenerDetallesPorVenta(venta.getId());
            rentabilidadDTO.setDetallesVenta(detallesVenta);

            BigDecimal totalGanancia = BigDecimal.ZERO; // Inicializa la ganancia total
            BigDecimal subtotal = BigDecimal.valueOf(venta.getSubtotalSinDescuentos()); // Asigna el subtotal
            BigDecimal descuento = BigDecimal.valueOf(venta.getMontoDescuento()); // Asigna el descuento
            BigDecimal recargo = BigDecimal.valueOf(venta.getRecargo()); // Asigna el recargo
            
            List<BigDecimal> gananciasPorProducto = new ArrayList<>(); // Inicializa la lista de ganancias por producto

            for (DetalleVenta detalle : detallesVenta) {
                BigDecimal precioVenta;
                BigDecimal precioCompra;

                if (detalle.getProducto() != null) {
                    // Si el producto no es manual, tomamos los valores directamente del producto
                    precioVenta = detalle.getProducto().getPrecioVenta();
                    precioCompra = detalle.getProducto().getPrecioCompra();
                } else {
                    // Si el producto es manual, usamos el precio ingresado y calculamos el precio de compra
                    precioVenta = BigDecimal.valueOf(detalle.getPrecio()); // Precio manual
                    precioCompra = precioVenta.subtract(precioVenta.multiply(BigDecimal.valueOf(0.30))); // 30% menos
                }

                // Calculamos la ganancia
                BigDecimal gananciaProducto = precioVenta.subtract(precioCompra);
                totalGanancia = totalGanancia.add(gananciaProducto.multiply(BigDecimal.valueOf(detalle.getCantidad())));
                gananciasPorProducto.add(gananciaProducto.multiply(BigDecimal.valueOf(detalle.getCantidad())));
            }



            rentabilidadDTO.setGananciaTotal(totalGanancia);
            rentabilidadDTO.setSubtotal(subtotal); // Asigna el subtotal calculado
            rentabilidadDTO.setDescuento(descuento); // Asigna el descuento total
            rentabilidadDTO.setRecargo(recargo); // Asigna el recargo total
            rentabilidadDTO.setGananciasPorProducto(gananciasPorProducto); // Agregar la lista de ganancias por producto
            
            // Cálculo del total y asignación
            BigDecimal totalVenta = subtotal.subtract(descuento).add(recargo); // Aquí calculamos el total
            rentabilidadDTO.setTotalVenta(totalVenta); // Asignamos el total

            rentabilidadList.add(rentabilidadDTO);
        }

        return rentabilidadList;
    }




    
    @PostMapping("/actualizarStockInsumos")
    public ResponseEntity<String> actualizarStockInsumos(@RequestBody List<InsumoDTO> insumos) {
        System.out.println("Insumos recibidos: " + insumos); // Añade este log

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
            e.printStackTrace(); // O usa un logger
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al actualizar el stock: " + e.getMessage());
        }

        return ResponseEntity.ok().build();
    }



    @PostMapping("/guardarInsumosSeleccionados")
    public ResponseEntity<?> guardarInsumosSeleccionados(@RequestBody List<Long> insumoIds) {
        try {
            // Aquí puedes procesar los insumos seleccionados
            for (Long insumoId : insumoIds) {
                Insumo insumo = insumoService.obtenerInsumoPorId(insumoId);
                // Procesa el insumo, como actualizar stock, realizar cálculos, etc.
            }
            return ResponseEntity.ok().build(); // Respuesta 200 OK
        } catch (Exception e) {
            e.printStackTrace(); // Para ver el error en la consola
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al guardar insumos seleccionados");
        }
    }
}
