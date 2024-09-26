package com.gestionsimple.sistema_ventas.repository;

import com.gestionsimple.sistema_ventas.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginRepository extends JpaRepository<Login, Long> {
    Login findByNombreUsuario(String nombreUsuario);
}
