package com.gestionsimple.sistema_ventas.controller.contabilidad;

import com.gestionsimple.sistema_ventas.service.contabilidad.CuentaContableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.PostConstruct;

@RestController
@RequestMapping("/api/cuentas")
public class CuentaContableController {

    @Autowired
    private CuentaContableService cuentaContableService;

    @PostConstruct
    public void init() {
        cuentaContableService.cargarCuentasIniciales();
    }

    @GetMapping("/cargar")
    public String cargarCuentas() {
        cuentaContableService.cargarCuentasIniciales();
        return "Cuentas cargadas exitosamente";
    }
}
