package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.Insumo; // Cambiado
import com.gestionsimple.sistema_ventas.model.Proveedor;
import com.gestionsimple.sistema_ventas.model.Categoria; // Cambiado
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InsumoRepository extends JpaRepository<Insumo, Long> {
    List<Insumo> findByCategoriaAndActivo(Categoria categoria, boolean activo);
    List<Insumo> findByProveedorId(Long proveedorId);
    List<Insumo> findByProveedor(Proveedor proveedor);

}
