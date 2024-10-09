package com.gestionsimple.sistema_ventas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionsimple.sistema_ventas.model.PrintConfig;

@Repository
public interface PrintConfigRepository extends JpaRepository<PrintConfig, Long> {
}

