package com.gestionsimple.sistema_ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.gestionsimple.sistema_ventas.model.Categoria;

public interface CategoriaRepository extends JpaRepository<Categoria, Long> {
    Categoria findByNombre(String nombre);
}
