package com.gestionsimple.sistema_ventas.controller;

import com.gestionsimple.sistema_ventas.model.HistorialPrecio;
import com.gestionsimple.sistema_ventas.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class HistorialPreciosController {

    @Autowired
    private ProductoService productoService;

    @GetMapping("/historial/{id}")
    public String verHistorialPrecios(@PathVariable Long id, Model model) {
        // Obtener historial de precios del producto
        List<HistorialPrecio> historialPrecios = productoService.obtenerHistorialPrecios(id);
        model.addAttribute("historialPrecios", historialPrecios);

        // Retornar la vista
        return "ver_historial_precios";
    }
}
