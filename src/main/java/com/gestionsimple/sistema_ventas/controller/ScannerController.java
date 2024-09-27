package com.gestionsimple.sistema_ventas.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gestionsimple.sistema_ventas.model.ScannerConfig;
import com.gestionsimple.sistema_ventas.service.ScannerConfigService;

@Controller
@RequestMapping("/barcode")
public class ScannerController {

    @Autowired
    private ScannerConfigService scannerConfigService;

    // Este método configura el escáner de código de barras, obteniendo los puertos disponibles
    @GetMapping("/configure")
    public String configureBarcodeScanner(Model model) {
        // Usamos el servicio para obtener los puertos disponibles
        List<String> availablePorts = scannerConfigService.getAvailablePorts();
        ScannerConfig currentConfig = scannerConfigService.getConfig();
        
        model.addAttribute("ports", availablePorts);
        model.addAttribute("currentPort", currentConfig != null ? currentConfig.getPortName() : null);
        
        return "barcode_configure";
    }

    // Este método guarda la configuración seleccionada por el usuario
    @PostMapping("/configure")
    public String savePortConfiguration(@RequestParam("portName") String portName, Model model) {
        scannerConfigService.saveConfig(portName);
        model.addAttribute("message", "Configuración guardada con éxito");
        return "redirect:/barcode/configure";
    }
}
