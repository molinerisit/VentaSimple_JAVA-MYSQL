package com.gestionsimple.sistema_ventas.service.impl;

import com.gestionsimple.sistema_ventas.repository.DetalleVentaRepository;
import com.gestionsimple.sistema_ventas.service.EstadisticasService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class EstadisticasServiceImpl implements EstadisticasService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Override
    public List<Object[]> obtenerHorariosMasConcurridos() {
        return detalleVentaRepository.obtenerHorariosMasConcurridos();
    }

    @Override
    public List<Object[]> obtenerDiasMasConcurridos() {
        return detalleVentaRepository.obtenerDiasMasConcurridos();
    }

    @Override
    public List<Object[]> obtenerMediosDePago() {
        return detalleVentaRepository.obtenerMediosDePago();
    }

    @Override
    public List<Object[]> obtenerProductosMasVendidos() {
        return detalleVentaRepository.obtenerProductosMasVendidos();
    }

    @Override
    public double obtenerTotalRecaudado() {
        return detalleVentaRepository.obtenerTotalRecaudado();
    }

    @Override
    public double obtenerRecaudadoPorDia(String dia) {
        LocalDate date = LocalDate.parse(dia);
        Double recaudado = detalleVentaRepository.obtenerRecaudadoPorDia(date);
        return recaudado != null ? recaudado : 0.0;
    }

}
