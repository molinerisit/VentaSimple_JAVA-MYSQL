package com.gestionsimple.sistema_ventas.controller;

import com.gestionsimple.sistema_ventas.model.CierreCaja;
import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import com.gestionsimple.sistema_ventas.service.CierreCajaService;
import com.gestionsimple.sistema_ventas.service.DetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.time.format.DateTimeFormatter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class CierreCajaController {

    @Autowired
    private DetalleVentaService detalleVentaService;

    @Autowired
    private CierreCajaService cierreCajaService;

    private LocalDateTime fechaInicio; // Store the start time of the day
    private double montoInicial; // Store the initial cash amount

    @GetMapping("/cierrecaja")
    public String mostrarCierreDeCaja(Model model) {
        // Asegúrate de mostrar el monto inicial y la fecha de inicio
        model.addAttribute("montoInicial", montoInicial);
        model.addAttribute("fechaInicio", fechaInicio); // Add fechaInicio to the model
        // Obtiene todos los cierres de caja previos
        List<CierreCaja> cierresCaja = cierreCajaService.getAllCierresCaja();
        model.addAttribute("cierresCaja", cierresCaja); // Añade cierres de caja al modelo

        return "cierrecaja"; // Nombre del archivo de vista (cierrecaja.html)
    }

    @PostMapping("/cierrecaja")
    public String guardarMontoInicial(double montoInicial, RedirectAttributes redirectAttributes) {
        // Set the initial cash amount
        this.montoInicial = montoInicial;
        this.fechaInicio = LocalDateTime.now(); // Set the start time of the day

        // Redirect with the stored initial amount
        redirectAttributes.addFlashAttribute("montoInicial", montoInicial);
        redirectAttributes.addFlashAttribute("fechaInicio", fechaInicio);

        return "redirect:/cierrecaja";
    }

    @PostMapping("/cierrecaja/cierre")
    public String realizarCierreDeCaja(RedirectAttributes redirectAttributes) {

        List<DetalleVenta> detallesVentaHoy = detalleVentaService.obtenerDetallesVentaHoy();

        double totalVentas = 0;
        double totalDescuentos = 0;
        double totalRecargos = 0;
        double totalPagosEfectivo = 0;
        double totalPagosDebito = 0;
        double totalPagosCredito = 0;
        double totalPagosQR = 0;
        double totalVuelto = 0;

        for (DetalleVenta detalle : detallesVentaHoy) {
            totalVentas += detalle.getVenta().getTotal();
            totalDescuentos += detalle.getVenta().getMontoDescuento();
            totalRecargos += detalle.getVenta().getRecargo();

            String metodoPago = detalle.getVenta().getMetodoPago().toLowerCase();
            switch (metodoPago) {
                case "efectivo":
                    totalPagosEfectivo += detalle.getVenta().getMontoPagado();
                    totalVuelto += detalle.getVenta().getVuelto();
                    break;
                case "debito":
                    totalPagosDebito += detalle.getVenta().getMontoPagado();
                    break;
                case "credito":
                    totalPagosCredito += detalle.getVenta().getMontoPagado();
                    break;
                case "qr":
                    totalPagosQR += detalle.getVenta().getMontoPagado();
                    break;
            }
        }

        CierreCaja cierreCaja = new CierreCaja(
            fechaInicio, LocalDateTime.now(), montoInicial, totalVentas, totalDescuentos, 
            totalRecargos, totalPagosEfectivo, totalVuelto, totalPagosDebito, 
            totalPagosCredito, totalPagosQR
        );
        cierreCajaService.saveCierreCaja(cierreCaja);

        redirectAttributes.addFlashAttribute("montoInicial", montoInicial);
        redirectAttributes.addFlashAttribute("totalPagado", totalPagosEfectivo);
        redirectAttributes.addFlashAttribute("totalVuelto", totalVuelto);
        redirectAttributes.addFlashAttribute("totalCaja", montoInicial + totalPagosEfectivo - totalVuelto);
        redirectAttributes.addFlashAttribute("totalPagosDebito", totalPagosDebito);
        redirectAttributes.addFlashAttribute("totalPagosCredito", totalPagosCredito);
        redirectAttributes.addFlashAttribute("totalPagosQR", totalPagosQR);

        return "redirect:/cierrecaja";
    }


    @GetMapping("/cierrecaja/{id}")
    public String mostrarCierreCajaPorId(@PathVariable Long id, Model model) {
        Optional<CierreCaja> cierreCaja = cierreCajaService.getCierreCajaById(id);
        if (cierreCaja.isPresent()) {
            CierreCaja cierre = cierreCaja.get();
            
            // Formateo de fecha
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            String fechaCierreFormateada = cierre.getFechaCierre().format(formatter);
            String fechaInicioFormateada = cierre.getFechaInicio().format(formatter);
            
            // Agregar fechas formateadas al modelo
            model.addAttribute("fechaCierreFormateada", fechaCierreFormateada);
            model.addAttribute("fechaInicioFormateada", fechaInicioFormateada);
            model.addAttribute("cierreCaja", cierre);
            
            return "detalleCierreCaja"; // Asegúrate de que esta vista exista
        } else {
            model.addAttribute("error", "Cierre de caja no encontrado");
            return "error"; // Cambia esto por la vista de error que uses
        }
    }


}
