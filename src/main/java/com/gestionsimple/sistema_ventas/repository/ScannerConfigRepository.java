package com.gestionsimple.sistema_ventas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestionsimple.sistema_ventas.model.ScannerConfig;

@Repository
public interface ScannerConfigRepository extends JpaRepository<ScannerConfig, Long> {
}

