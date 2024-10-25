package com.gestionsimple.sistema_ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestionsimple.sistema_ventas.model.Balance;

public interface BalanceRepository extends JpaRepository<Balance, Long> {
    // Aquí puedes agregar métodos personalizados si es necesario
}
