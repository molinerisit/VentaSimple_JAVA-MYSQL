package com.gestionsimple.sistema_ventas.repository.contabilidad;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionsimple.sistema_ventas.model.contabilidad.AsientoContable;

@Repository
public interface AsientoContableRepository extends JpaRepository<AsientoContable, Long> {
}