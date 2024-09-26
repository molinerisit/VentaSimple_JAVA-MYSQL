package com.gestionsimple.sistema_ventas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.gestionsimple.sistema_ventas.model.Categoria;
import com.gestionsimple.sistema_ventas.service.CategoriaService;

@Controller
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @GetMapping("/categorias/nueva")
    public String showCrearCategoriaForm(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "crear_categoria";
    }

    @PostMapping("/categorias")
    public String crearCategoria(Categoria categoria, RedirectAttributes redirectAttributes) {
        categoriaService.saveCategoria(categoria);
        redirectAttributes.addFlashAttribute("mensaje", "Categoría creada exitosamente");
        return "redirect:/productos"; // Redirige a la página de productos
    }

    @PostMapping("/categorias/eliminar")
    public String eliminarCategoria(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        categoriaService.deleteCategoria(id);
        redirectAttributes.addFlashAttribute("mensaje", "Categoría eliminada exitosamente");
        return "redirect:/productos"; // Redirige a la página de productos
    }
}
