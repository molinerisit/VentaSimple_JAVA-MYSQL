package com.gestionsimple.sistema_ventas.controller.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gestionsimple.sistema_ventas.model.contabilidad.AsientoContable;
import com.gestionsimple.sistema_ventas.service.contabilidad.AsientoContableService;

@Controller
@RequestMapping("/asientos")
public class AsientoContableController {

    @Autowired
    private AsientoContableService asientoContableService;

    @PostMapping("/registrar")
    public AsientoContable registrarAsiento(
            @RequestParam Long cuentaId,
            @RequestParam double monto,
            @RequestParam String detalles,
            @RequestParam String descripcion,
            @RequestParam String tipo) {
        return asientoContableService.registrarAsiento(cuentaId, monto, detalles, descripcion, tipo);
    }

    @GetMapping
    public String mostrarAsientos(Model model) {
        List<AsientoContable> asientos = asientoContableService.obtenerTodosLosAsientos();
        model.addAttribute("asientos", asientos); // Agrega la lista de asientos al modelo
        return "listar_asientos"; // Retorna el nombre de la vista Thymeleaf (listar_asientos.html)
    }
}
