package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.Balance;
import com.gestionsimple.sistema_ventas.model.Venta;
import com.gestionsimple.sistema_ventas.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;
    private final VentaService ventaService;

    @Autowired
    public BalanceService(BalanceRepository balanceRepository, VentaService ventaService) {
        this.balanceRepository = balanceRepository;
        this.ventaService = ventaService;
    }

    public Balance guardarBalance(double gananciaTotal, double inversionTotal, double dineroTotalRecaudado, 
                                  double gastoInsumos, double balanceNeto) {
        Balance balance = new Balance();
        balance.setGananciaTotal(gananciaTotal);
        balance.setInversionTotal(inversionTotal);
        balance.setDineroTotalRecaudado(dineroTotalRecaudado);
        balance.setGastoInsumos(gastoInsumos);
        balance.setBalanceNeto(balanceNeto);

        // Obtener las fechas de las ventas relacionadas con este balance
        Set<LocalDate> fechasCompras = obtenerFechasDeCompras();
        balance.setFechasCompras(fechasCompras);

        return balanceRepository.save(balance);
    }

    // Método para obtener las fechas de las compras (ventas) y eliminar duplicados
    private Set<LocalDate> obtenerFechasDeCompras() {
        List<Venta> ventas = ventaService.obtenerTodasVentas(); // Asegúrate de que este método devuelva todas las ventas
        Set<LocalDate> fechas = new HashSet<>();

        for (Venta venta : ventas) {
            LocalDate fecha = venta.getFechaHora().toLocalDate(); // Asumiendo que tienes la fecha de la venta
            fechas.add(fecha);
        }

        return fechas;
    }

    public Balance obtenerBalancePorId(Long id) {
        return balanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Balance no encontrado con ID: " + id));
    }

    public List<Balance> obtenerTodosBalances() {
        return balanceRepository.findAll();
    }
}
