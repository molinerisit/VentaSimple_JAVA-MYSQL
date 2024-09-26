package com.gestionsimple.sistema_ventas.service.impl;

import com.gestionsimple.sistema_ventas.dto.BalanceDTO;
import com.gestionsimple.sistema_ventas.dto.RentabilidadDTO;
import com.gestionsimple.sistema_ventas.model.BalanceHistorico;
import com.gestionsimple.sistema_ventas.repository.BalanceHistoricoRepository;
import com.gestionsimple.sistema_ventas.service.RentabilidadEjercicioService; // Asegúrate de importar la interfaz
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class RentabilidadEjercicioServiceImpl implements RentabilidadEjercicioService { // Implementar la interfaz

    @Autowired
    private BalanceHistoricoRepository balanceHistoricoRepository;

    @Override
    public void guardarBalanceHistorico(List<RentabilidadDTO> rentabilidadPorVenta, BigDecimal gananciaTotal) {
        BalanceHistorico balanceHistorico = new BalanceHistorico();
        balanceHistorico.setFechaBalance(LocalDateTime.now());
        balanceHistorico.setGananciaTotal(gananciaTotal);

        // Aquí puedes agregar la lógica para asociar las ventas con el balance, si es necesario
        // balanceHistorico.setVentas(...);

        balanceHistoricoRepository.save(balanceHistorico);
    }

    @Override
    public List<BalanceHistorico> obtenerBalancesHistoricos() {
        return balanceHistoricoRepository.findAll();
    }

    @Override
    public void guardarBalanceDesdeDTO(BalanceDTO balanceDTO) {
        BalanceHistorico balanceHistorico = new BalanceHistorico();
        balanceHistorico.setFechaBalance(LocalDateTime.now());
        balanceHistorico.setGananciaTotal(balanceDTO.getGananciaTotal());
        balanceHistorico.setGananciaUnitaria(balanceDTO.getGananciaUnitaria());
        balanceHistorico.setInversionTotal(balanceDTO.getInversionTotal());
        balanceHistorico.setDineroTotalRecaudado(balanceDTO.getDineroTotalRecaudado());

        balanceHistoricoRepository.save(balanceHistorico);
    }
}
