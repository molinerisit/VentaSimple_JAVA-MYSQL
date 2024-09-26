package com.gestionsimple.sistema_ventas.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.gestionsimple.sistema_ventas.service.EstadisticasService;

@Controller
public class EstadisticasController {

    @Autowired
    private EstadisticasService estadisticasService;

    @GetMapping("/estadisticas")
    public String mostrarEstadisticas(Model model) {
        List<Object[]> horariosMasConcurridos = estadisticasService.obtenerHorariosMasConcurridos();
        List<Object[]> diasMasConcurridos = estadisticasService.obtenerDiasMasConcurridos();
        List<Object[]> mediosDePago = estadisticasService.obtenerMediosDePago();
        List<Object[]> productosMasVendidos = estadisticasService.obtenerProductosMasVendidos();

        // Agregar logs
        System.out.println("Horarios Más Concurridos: " + horariosMasConcurridos);
        System.out.println("Días Más Concurridos: " + diasMasConcurridos);
        System.out.println("Medios de Pago: " + mediosDePago);
        System.out.println("Productos Más Vendidos: " + productosMasVendidos);

        model.addAttribute("horariosMasConcurridos", horariosMasConcurridos);
        model.addAttribute("diasMasConcurridos", diasMasConcurridos);
        model.addAttribute("mediosDePago", mediosDePago);
        model.addAttribute("productosMasVendidos", productosMasVendidos);

        model.addAttribute("horariosMasConcurridosJson", formatForChart(horariosMasConcurridos));
        model.addAttribute("diasMasConcurridosJson", formatForChart(diasMasConcurridos));
        model.addAttribute("mediosDePagoJson", formatForChart(mediosDePago));
        model.addAttribute("productosMasVendidosJson", formatForChart(productosMasVendidos));

        model.addAttribute("totalRecaudado", estadisticasService.obtenerTotalRecaudado());
        model.addAttribute("recaudadoHoy", estadisticasService.obtenerRecaudadoPorDia(LocalDate.now().toString()));

        return "estadisticas";
    }

    private String formatForChart(List<Object[]> data) {
        // Verifica que data no esté vacío
        if (data == null || data.isEmpty()) {
            return "[]";
        }

        // Define el formato de fecha
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy"); // Ajusta el formato según necesites

        return data.stream()
                .map(entry -> {
                    // Asegúrate de que entry tiene al menos dos elementos
                    if (entry.length < 2) {
                        return "{}"; // Manejo de error
                    }
                    String formattedDate;
                    try {
                        // Convierte el primer elemento a LocalDate y formatea
                        LocalDate date = LocalDate.parse(entry[0].toString());
                        formattedDate = date.format(formatter);
                    } catch (Exception e) {
                        formattedDate = entry[0].toString(); // Si ocurre un error, usa el valor original
                    }
                    return String.format("{\"label\":\"%s\",\"value\":%d}", formattedDate, entry[1]);
                })
                .collect(Collectors.joining(",", "[", "]"));
    }
}
