package com.gestionsimple.sistema_ventas.service;

import java.util.List;
import com.gestionsimple.sistema_ventas.model.Categoria;

public interface CategoriaService {
    List<Categoria> getAllCategorias();
    Categoria getCategoriaById(Long id);
    Categoria saveCategoria(Categoria categoria);
    void deleteCategoria(Long id);
}
