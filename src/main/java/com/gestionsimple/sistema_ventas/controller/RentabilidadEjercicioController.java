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
        model.addAttribute("rentabilidadPorVenta", rentabilidadPorVenta);

        // Obtener todos los insumos y agregarlos al modelo
        List<Insumo> insumos = insumoService.obtenerTodosLosInsumos();
        model.addAttribute("insumos", insumos); // Agregar insumos al modelo

        return "rentabilidad_ejercicio";
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

            for (DetalleVenta detalle : detallesVenta) {
                BigDecimal precioVenta = detalle.getProducto().getPrecioVenta();
                BigDecimal precioCompra = detalle.getProducto().getPrecioCompra();
                BigDecimal gananciaProducto = precioVenta.subtract(precioCompra);
                totalGanancia = totalGanancia.add(gananciaProducto.multiply(BigDecimal.valueOf(detalle.getCantidad())));
            }

            rentabilidadDTO.setGananciaTotal(totalGanancia);
            rentabilidadDTO.setSubtotal(subtotal); // Asigna el subtotal calculado
            rentabilidadDTO.setDescuento(descuento); // Asigna el descuento total
            rentabilidadDTO.setRecargo(recargo); // Asigna el recargo total
            
            // Cálculo del total y asignación
            BigDecimal totalVenta = subtotal.subtract(descuento).add(recargo); // Aquí calculamos el total
            rentabilidadDTO.setTotalVenta(totalVenta); // Asignamos el total

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

    
    @PostMapping("/actualizarStockInsumos")
    public ResponseEntity<Void> actualizarStockInsumos(@RequestBody List<InsumoDTO> insumos) {
        for (InsumoDTO insumoDTO : insumos) {
            // Buscar el insumo en la base de datos usando el ID
            Insumo insumo = insumoRepository.findById(insumoDTO.getId())
                    .orElseThrow();

            // Obtener la cantidad a restar del DTO
            BigDecimal cantidadARestar = insumoDTO.getCantidad(); // Cambia a BigDecimal

            // Convertir el stock actual a BigDecimal para comparar
            BigDecimal stockActual = BigDecimal.valueOf(insumo.getStock()); // Cambiar a BigDecimal

            if (stockActual.compareTo(cantidadARestar) >= 0) { // Usar compareTo para BigDecimal
                insumo.setStock(insumo.getStock() - cantidadARestar.intValue()); // Actualizar el stock, asegurando el tipo correcto
                insumoRepository.save(insumo); // Guardar los cambios en el repositorio
            } else {
                // Manejar el caso donde no hay suficiente stock
                return ResponseEntity.badRequest().build(); // O lanza una excepción personalizada
            }
        }

        return ResponseEntity.ok().build(); // Retorna un 200 OK si todo fue exitoso
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
