package com.gestionsimple.sistema_ventas.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gestionsimple.sistema_ventas.model.Balance;
import com.gestionsimple.sistema_ventas.repository.BalanceRepository;

import java.time.LocalDateTime;

@Service
public class BalanceService {

    @Autowired
    private BalanceRepository balanceRepository;

    public Balance guardarBalance(String imagen, Double totalDineroRecaudado) {
        Balance balance = new Balance();
        balance.setImagen(imagen);
        balance.setFechaCreacion(LocalDateTime.now());
        balance.setTotalDineroRecaudado(totalDineroRecaudado);

        return balanceRepository.save(balance);
    }

}
