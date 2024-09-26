package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.dto.BalanceDTO;
import com.gestionsimple.sistema_ventas.dto.RentabilidadDTO;
import com.gestionsimple.sistema_ventas.model.BalanceHistorico;

import java.math.BigDecimal;
import java.util.List;

public interface RentabilidadEjercicioService {

    void guardarBalanceHistorico(List<RentabilidadDTO> rentabilidadPorVenta, BigDecimal gananciaTotal);
    
    List<BalanceHistorico> obtenerBalancesHistoricos();
    
    void guardarBalanceDesdeDTO(BalanceDTO balanceDTO);
}
