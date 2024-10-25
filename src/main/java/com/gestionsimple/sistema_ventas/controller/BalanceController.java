package com.gestionsimple.sistema_ventas.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.gestionsimple.sistema_ventas.service.BalanceService;

@RestController
@RequestMapping("/rentabilidad-ejercicio")
public class BalanceController {

    @Autowired
    private BalanceService balanceService;

    @PostMapping("/guardarImagenBalance")
    public void guardarImagenBalance(@RequestBody BalanceRequest request) {
        balanceService.guardarBalance(request.getImagen(), request.getTotalDineroRecaudado());
    }

    // Clase interna para recibir datos en el cuerpo de la solicitud
    public static class BalanceRequest {
        private String imagen;
        private Double totalDineroRecaudado;

        // Getters y Setters
        public String getImagen() {
            return imagen;
        }

        public void setImagen(String imagen) {
            this.imagen = imagen;
        }

        public Double getTotalDineroRecaudado() {
            return totalDineroRecaudado;
        }

        public void setTotalDineroRecaudado(Double totalDineroRecaudado) {
            this.totalDineroRecaudado = totalDineroRecaudado;
        }
    }
}
