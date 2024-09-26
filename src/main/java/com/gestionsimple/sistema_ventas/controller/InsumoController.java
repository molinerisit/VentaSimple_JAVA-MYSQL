package com.gestionsimple.sistema_ventas.controller;

import com.gestionsimple.sistema_ventas.model.Insumo;
import com.gestionsimple.sistema_ventas.model.Compra;
import com.gestionsimple.sistema_ventas.model.CompraInsumo;
import com.gestionsimple.sistema_ventas.service.InsumoService;
import com.gestionsimple.sistema_ventas.service.CategoriaService;
import com.gestionsimple.sistema_ventas.service.CompraInsumoService;
import com.gestionsimple.sistema_ventas.service.ProveedorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/insumos")
public class InsumoController {

    @Autowired
    private InsumoService insumoService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private CompraInsumoService compraInsumoService;

    @Autowired
    private ProveedorService proveedorService;

    // Mostrar todos los insumos
    @GetMapping
    public String mostrarInsumos(Model model) {
        List<Insumo> insumos = insumoService.obtenerTodosLosInsumos();
        model.addAttribute("insumos", insumos);
        return "insumos";
    }

    // Mostrar formulario de creación de insumo
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(Model model) {
        Insumo nuevoInsumo = new Insumo();
        nuevoInsumo.setActivo(true); // Establecer activo por defecto
        model.addAttribute("insumo", nuevoInsumo);
        model.addAttribute("categorias", categoriaService.getAllCategorias());
        return "crear_insumo";
    }

    // Guardar insumo
    @PostMapping("/guardar")
    public String guardarInsumo(@ModelAttribute("insumo") Insumo insumo) {
        insumo.setFechaRegistro(LocalDate.now());
        insumoService.guardarInsumo(insumo);
        return "redirect:/insumos";
    }

    // Mostrar formulario de edición de insumo
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        Insumo insumo = insumoService.obtenerInsumoPorId(id);
        if (insumo == null) {
            return "redirect:/insumos";
        }
        model.addAttribute("insumo", insumo);
        model.addAttribute("categorias", categoriaService.getAllCategorias());
        return "editar_insumo";
    }

    // Procesar edición de insumo
    @PostMapping("/editar")
    public String procesarEdicion(@ModelAttribute("insumo") Insumo insumo) {
        insumoService.actualizarInsumo(insumo);
        return "redirect:/insumos";
    }

    // Eliminar insumo
    @GetMapping("/eliminar/{id}")
    public String eliminarInsumo(@PathVariable Long id) {
        insumoService.eliminarInsumo(id);
        return "redirect:/insumos";
    }

    // Activar/desactivar insumo
    @PostMapping("/activar/{id}")
    public String actualizarEstadoActivo(@PathVariable("id") Long id, @RequestParam("activo") boolean activo) {
        Insumo insumo = insumoService.obtenerInsumoPorId(id);
        if (insumo != null) {
            insumo.setActivo(activo);
            insumoService.guardarInsumo(insumo);
        }
        return "redirect:/insumos";
    }

    @PostMapping("/{id}/importarCompras")
    public String importarCompras(@PathVariable Long id, @RequestParam("compraId") Long compraId) {
        Insumo insumo = insumoService.obtenerInsumoPorId(id);
        CompraInsumo compraInsumo = compraInsumoService.getCompraById(compraId); // Usar CompraInsumo
        if (insumo != null && compraInsumo != null) {
            insumo.setStock(insumo.getStock() + (int) compraInsumo.getCantidad()); // Asegúrate de que 'getCantidad()' exista
            insumoService.guardarInsumo(insumo);
        }
        return "redirect:/insumos";
    }


    // Actualizar stock de insumo
    @PostMapping("/{id}/actualizarStock")
    public String actualizarStock(@PathVariable Long id, @RequestParam("newStock") int newStock, RedirectAttributes redirectAttributes) {
        insumoService.actualizarStock(id, newStock);
        redirectAttributes.addFlashAttribute("message", "Stock actualizado correctamente.");
        return "redirect:/insumos";
    }

    // Mostrar rentabilidad de insumos
    @GetMapping("/rentabilidad")
    public String mostrarRentabilidad(Model model) {
        List<Insumo> insumos = insumoService.obtenerTodosLosInsumos();
        model.addAttribute("insumos", insumos);
        return "rentabilidad";
    }

    // Actualizar datos de rentabilidad de insumo
    @PostMapping("/{id}/actualizarRentabilidadDatos")
    public ResponseEntity<String> actualizarRentabilidadDatos(@PathVariable("id") Long id, @RequestBody Map<String, Double> datos) {
        Insumo insumo = insumoService.obtenerInsumoPorId(id);
        if (insumo == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Insumo no encontrado");
        }

        try {
            insumo.setPorcentajeRentabilidad(BigDecimal.valueOf(datos.getOrDefault("porcentajeRentabilidad", 0.0)));
            insumo.setPrecioVenta(BigDecimal.valueOf(datos.getOrDefault("precioVenta", 0.0)));
            insumo.setGananciaTotal(BigDecimal.valueOf(datos.getOrDefault("gananciaTotal", 0.0)));
            insumoService.guardarInsumo(insumo);
            return ResponseEntity.ok("Datos actualizados correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar datos");
        }
    }
}
