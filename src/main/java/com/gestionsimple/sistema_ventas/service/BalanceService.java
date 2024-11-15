package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.Balance;
import com.gestionsimple.sistema_ventas.repository.BalanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BalanceService {

    private final BalanceRepository balanceRepository;

    @Autowired
    public BalanceService(BalanceRepository balanceRepository) {
        this.balanceRepository = balanceRepository;
    }

    public Balance guardarBalance(double gananciaTotal, double inversionTotal, double dineroTotalRecaudado, double gastoInsumos, double balanceNeto) {
        Balance balance = new Balance();
        balance.setGananciaTotal(gananciaTotal);
        balance.setInversionTotal(inversionTotal);
        balance.setDineroTotalRecaudado(dineroTotalRecaudado);
        balance.setGastoInsumos(gastoInsumos);
        balance.setBalanceNeto(balanceNeto);

        return balanceRepository.save(balance);
    }
    
    public Balance obtenerBalancePorId(Long id) {
        return balanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Balance no encontrado con ID: " + id));
    }



public List<Balance> obtenerTodosBalances() {
    return balanceRepository.findAll();
}

}
