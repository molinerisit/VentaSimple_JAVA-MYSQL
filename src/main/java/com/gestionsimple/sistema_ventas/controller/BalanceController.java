package com.gestionsimple.sistema_ventas.controller;

import com.gestionsimple.sistema_ventas.model.Balance;
import com.gestionsimple.sistema_ventas.service.BalanceService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.format.DateTimeFormatter;


@Controller
@RequestMapping("/rentabilidad-ejercicio")
public class BalanceController {

    private final BalanceService balanceService;

    @Autowired
    public BalanceController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @PostMapping("/guardarBalance")
    @ResponseBody
    public Balance guardarBalance(@RequestBody Balance balanceRequest) {
        double gananciaTotal = balanceRequest.getGananciaTotal();
        double inversionTotal = balanceRequest.getInversionTotal();
        double dineroTotalRecaudado = balanceRequest.getDineroTotalRecaudado();
        double gastoInsumos = balanceRequest.getGastoInsumos();
        double balanceNeto = balanceRequest.getBalanceNeto();

        return balanceService.guardarBalance(gananciaTotal, inversionTotal, dineroTotalRecaudado, gastoInsumos, balanceNeto);
    }

    @GetMapping("/listarBalances")
    @ResponseBody
    public List<Balance> listarBalances(@RequestParam(required = false) Double gananciaMinima) {
        List<Balance> balances = balanceService.obtenerTodosBalances();

        if (gananciaMinima != null) {
            balances = balances.stream()
                .filter(balance -> balance.getGananciaTotal() >= gananciaMinima)
                .collect(Collectors.toList());
        }
        return balances;
    }

    @GetMapping("/balances")
    public String mostrarBalances(Model model) {
        List<Balance> balances = balanceService.obtenerTodosBalances();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        List<Map<String, Object>> balancesFormateados = balances.stream().map(balance -> {
            Map<String, Object> balanceMap = new HashMap<>();
            balanceMap.put("id", balance.getId());
            balanceMap.put("fechaCreacion", balance.getFechaCreacion().format(formatter));
            balanceMap.put("gananciaTotal", balance.getGananciaTotal());
            balanceMap.put("inversionTotal", balance.getInversionTotal());
            balanceMap.put("dineroTotalRecaudado", balance.getDineroTotalRecaudado());
            balanceMap.put("gastoInsumos", balance.getGastoInsumos());
            balanceMap.put("balanceNeto", balance.getBalanceNeto());
            return balanceMap;
        }).collect(Collectors.toList());

        model.addAttribute("balances", balancesFormateados);
        return "balances";
    }
}
