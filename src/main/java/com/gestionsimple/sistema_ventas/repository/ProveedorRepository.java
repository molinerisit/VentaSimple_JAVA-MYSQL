package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.Proveedor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorRepository extends JpaRepository<Proveedor, Long> {
}
