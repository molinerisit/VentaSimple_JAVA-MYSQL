package com.gestionsimple.sistema_ventas.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gestionsimple.sistema_ventas.model.Compra;


@Repository
public interface CompraRepository extends JpaRepository<Compra, Long> {

    Optional<Compra> findFirstByProductoIdOrderByFechaDesc(Long productoId);

	List<Compra> findByProveedorId(Long proveedorId);
    List<Compra> findByProveedorIdAndPagadoFalse(Long proveedorId);

    
    @Query("SELECT c FROM Compra c WHERE c.producto.id = :productoId ORDER BY c.id ASC")
    List<Compra> findAllByProductoId(Long productoId);

    List<Compra> findByProductoIdOrderByIdAsc(Long productoId);

}


