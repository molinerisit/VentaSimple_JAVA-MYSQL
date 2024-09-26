package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.DeudaProveedor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeudaProveedorRepository extends JpaRepository<DeudaProveedor, Long> {
    List<DeudaProveedor> findByProveedorId(Long proveedorId);
    DeudaProveedor findByCompraId(Long compraId);
	List<DeudaProveedor> findByProveedorIdAndPagadoFalse(Long proveedorId);

}


