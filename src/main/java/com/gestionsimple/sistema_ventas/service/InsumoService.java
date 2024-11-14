package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.Categoria;
import com.gestionsimple.sistema_ventas.model.Insumo; // Cambiado

import java.util.List;

public interface InsumoService { // Cambiado
    List<Categoria> obtenerTodasLasCategorias();
    List<Insumo> obtenerInsumosPorCategoria(String categoria);
    Insumo obtenerInsumoPorId(Long id); // Cambiado
    void actualizarInsumo(Insumo insumo); // Cambiado
    void guardarInsumo(Insumo insumo); // Cambiado
    List<Insumo> obtenerTodosLosInsumos(); // Cambiado
    void eliminarInsumo(Long id); // Cambiado
    void actualizarStock(Long id, Double newStock); // Cambiado
    void actualizarPrecioCompra(Long id, double newPrecioCompra); // Cambiado
    boolean insumoInvolucradoEnVenta(Long id); // Cambiado
    List<Insumo> buscarPorCategoriaYActivo(Categoria categoria, boolean activo); // Cambiado
}
