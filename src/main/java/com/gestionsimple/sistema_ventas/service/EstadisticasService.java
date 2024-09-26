package com.gestionsimple.sistema_ventas.service;

import java.util.List;

public interface EstadisticasService {
    List<Object[]> obtenerHorariosMasConcurridos();
    List<Object[]> obtenerDiasMasConcurridos();
    List<Object[]> obtenerMediosDePago();
    List<Object[]> obtenerProductosMasVendidos();
    double obtenerTotalRecaudado();
    double obtenerRecaudadoPorDia(String dia);
}
