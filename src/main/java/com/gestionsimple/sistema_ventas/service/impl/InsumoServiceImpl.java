package com.gestionsimple.sistema_ventas.service.impl;

import com.gestionsimple.sistema_ventas.model.Categoria;
import com.gestionsimple.sistema_ventas.model.Insumo;
import com.gestionsimple.sistema_ventas.repository.InsumoRepository;
import com.gestionsimple.sistema_ventas.service.InsumoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class InsumoServiceImpl implements InsumoService {

    private static final Logger logger = LoggerFactory.getLogger(InsumoServiceImpl.class);

    @Autowired
    private InsumoRepository insumoRepository;

    @Override
    public List<Categoria> obtenerTodasLasCategorias() {
        return insumoRepository.findAll()
                .stream()
                .map(Insumo::getCategoria)
                .filter(categoria -> categoria != null)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Insumo obtenerInsumoPorId(Long id) {
        return insumoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void guardarInsumo(Insumo insumo) {
        insumoRepository.save(insumo);
    }

    @Override
    @Transactional
    public void actualizarInsumo(Insumo insumo) {
        insumoRepository.save(insumo);
    }

    @Override
    public List<Insumo> obtenerTodosLosInsumos() {
        return insumoRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarInsumo(Long id) {
        insumoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void actualizarStock(Long id, int newStock) {
        Insumo insumo = obtenerInsumoPorId(id);
        if (insumo != null) {
            insumo.setStock(newStock);
            guardarInsumo(insumo);
        }
    }

    @Override
    @Transactional
    public void actualizarPrecioCompra(Long id, double newPrecioCompra) {
        Insumo insumo = obtenerInsumoPorId(id);
        if (insumo != null) {
            // Convertir el double a BigDecimal
            insumo.setPrecioCompra(BigDecimal.valueOf(newPrecioCompra));
            guardarInsumo(insumo);
        }
    }

	@Override
	public List<Insumo> obtenerInsumosPorCategoria(String categoria) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean insumoInvolucradoEnVenta(Long id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<Insumo> buscarPorCategoriaYActivo(Categoria categoria, boolean activo) {
		// TODO Auto-generated method stub
		return null;
	}
}
