package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.Categoria;
import com.gestionsimple.sistema_ventas.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoriaAndActivo(Categoria categoria, boolean activo);
}
