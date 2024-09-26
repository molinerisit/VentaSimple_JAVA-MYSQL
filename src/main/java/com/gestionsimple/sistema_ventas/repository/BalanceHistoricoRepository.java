package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.BalanceHistorico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceHistoricoRepository extends JpaRepository<BalanceHistorico, Long> {
}
