package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.RentabilidadCalculadora;
import com.gestionsimple.sistema_ventas.repository.RentabilidadCalculadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class RentabilidadCalculadoraService {

    @Autowired
    private RentabilidadCalculadoraRepository rentabilidadCalculadoraRepository;

    /**
     * Obtiene todos los registros de RentabilidadCalculadora y calcula la rentabilidad para cada uno.
     *
     * @return Lista de RentabilidadCalculadora con la rentabilidad calculada.
     */
    public List<RentabilidadCalculadora> obtenerConRentabilidadCalculada() {
        List<RentabilidadCalculadora> rentabilidadCalculadoras = rentabilidadCalculadoraRepository.findAll();

        // Calcular la rentabilidad para cada registro
        rentabilidadCalculadoras.forEach(rentabilidadCalculadora -> {
            BigDecimal margen = rentabilidadCalculadora.getPrecioVenta().subtract(rentabilidadCalculadora.getPrecioCompra());
            BigDecimal rentabilidad = margen.divide(rentabilidadCalculadora.getPrecioCompra(), 2, BigDecimal.ROUND_HALF_UP)
                                            .multiply(BigDecimal.valueOf(100));
            rentabilidadCalculadora.setPorcentajeRentabilidad(rentabilidad);
        });

        return rentabilidadCalculadoras;
    }
}
