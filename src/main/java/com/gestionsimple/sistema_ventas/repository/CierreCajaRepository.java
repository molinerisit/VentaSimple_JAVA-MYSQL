package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.CierreCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CierreCajaRepository extends JpaRepository<CierreCaja, Long> {
    
    // Método para obtener el cierre de caja abierto (sin fecha de cierre)
    Optional<CierreCaja> findFirstByFechaCierreIsNull();
}
