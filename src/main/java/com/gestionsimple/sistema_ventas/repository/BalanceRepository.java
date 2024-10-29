package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.Balance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, Long> {
}
