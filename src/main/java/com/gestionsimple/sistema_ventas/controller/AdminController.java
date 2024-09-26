package com.gestionsimple.sistema_ventas.controller;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    @GetMapping("/admin")
    public String adminPanel() {
        return "adminPanel"; // Nombre del archivo HTML (panel de administrador)
    }
}
