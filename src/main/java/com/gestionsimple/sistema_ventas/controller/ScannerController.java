package com.gestionsimple.sistema_ventas.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

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
    
    @GetMapping("/get-scanned-barcode")
    @ResponseBody
    public ResponseEntity<Map<String, String>> obtenerCodigoDeBarras() {
        String codigoDeBarras = scannerConfigService.readBarcode(); // Lógica para leer el código de barras

        // Agregar log para depuración
        System.out.println("Código de barras leído: " + codigoDeBarras);

        Map<String, String> response = new HashMap<>();
        response.put("codigoDeBarras", codigoDeBarras != null ? codigoDeBarras : "");
        return ResponseEntity.ok(response);
    }


    // Este método guarda la configuración seleccionada por el usuario
    @PostMapping("/configure")
    public String savePortConfiguration(@RequestParam("portName") String portName, Model model) {
        // Agregar log para verificar la configuración del puerto
        System.out.println("Puerto seleccionado: " + portName);

        scannerConfigService.saveConfig(portName);
        model.addAttribute("message", "Configuración guardada con éxito");

        return "redirect:/barcode/configure";
    }
}
