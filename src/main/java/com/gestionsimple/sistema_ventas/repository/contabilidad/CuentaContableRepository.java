package com.gestionsimple.sistema_ventas.repository.contabilidad;

import com.gestionsimple.sistema_ventas.model.contabilidad.AsientoContable;
import com.gestionsimple.sistema_ventas.model.contabilidad.CuentaContable;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CuentaContableRepository extends JpaRepository<CuentaContable, Long> {
    Optional<CuentaContable> findByNombre(String nombre);
    List<CuentaContable> findByTipo(String tipo);
}
