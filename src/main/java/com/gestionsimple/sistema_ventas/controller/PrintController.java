package com.gestionsimple.sistema_ventas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.gestionsimple.sistema_ventas.model.PrintConfig;
import com.gestionsimple.sistema_ventas.service.PrintConfigService;

@Controller
@RequestMapping("/print")
public class PrintController {

    @Autowired
    private PrintConfigService printConfigService;

    // Método para mostrar el formulario de configuración de impresora
    @GetMapping("/configure")
    public String configurePrint(Model model) {
        try {
            List<String> availablePorts = printConfigService.getAvailablePorts();
            PrintConfig currentConfig = printConfigService.getConfig();
            
            // Añadir la lista de puertos y la configuración actual al modelo
            model.addAttribute("ports", availablePorts);
            model.addAttribute("currentPort", currentConfig != null ? currentConfig.getPortName() : null);
        } catch (Exception e) {
            // Si ocurre un error, se añade un mensaje al modelo
            model.addAttribute("error", "Error al obtener la configuración de impresión.");
        }

        return "print_configure"; // El archivo Thymeleaf debe estar en src/main/resources/templates/print_configure.html
    }

    // Método para guardar la configuración seleccionada por el usuario
    @PostMapping("/configure")
    public String savePortConfiguration(@RequestParam("portName") String portName, RedirectAttributes redirectAttributes) {
        try {
            // Guardar la configuración seleccionada
            printConfigService.saveConfig(portName);
            // Añadir mensaje de éxito usando RedirectAttributes para que sobreviva la redirección
            redirectAttributes.addFlashAttribute("message", "Configuración guardada con éxito.");
        } catch (Exception e) {
            // Manejar cualquier excepción que ocurra durante el guardado
            redirectAttributes.addFlashAttribute("error", "Error al guardar la configuración de impresión.");
        }

        // Redirigir a la página de configuración de nuevo
        return "redirect:/print/configure";
    }

    // Método para imprimir un ticket de prueba
    @PostMapping("/printTest")
    public String printTest(@RequestParam("receiptData") String receiptData, RedirectAttributes redirectAttributes) {
        System.out.println("Recibido el texto del recibo para impresión: " + receiptData);
        try {
            // Llamar al servicio para imprimir el recibo
            printConfigService.printReceipt(receiptData); // Aquí envías el texto recibido a la impresora
            printConfigService.printReceipt(" "); // Esto imprime un texto fijo para comprobar
            printConfigService.printReceipt(" "); // Esto imprime un texto fijo para comprobar
            printConfigService.printReceipt(" "); // Esto imprime un texto fijo para comprobar
            // Imprimir un texto de prueba adicional
            printConfigService.printReceipt("Prueba de impresión simple"); // Esto imprime un texto fijo para comprobar
            printConfigService.printReceipt(" "); // Esto imprime un texto fijo para comprobar
            printConfigService.printReceipt(" "); // Esto imprime un texto fijo para comprobar
            printConfigService.printReceipt(" "); // Esto imprime un texto fijo para comprobar

            redirectAttributes.addFlashAttribute("message", "Recibo impreso con éxito.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al imprimir el recibo: " + e.getMessage());
            System.err.println("Error al imprimir el recibo: " + e.getMessage());
        }

        // Redirigir a la página de configuración de nuevo
        return "redirect:/print/configure";
    }
}
