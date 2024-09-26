package com.gestionsimple.sistema_ventas.service.impl;

import com.gestionsimple.sistema_ventas.model.Login;
import com.gestionsimple.sistema_ventas.repository.LoginRepository;
import com.gestionsimple.sistema_ventas.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private LoginRepository loginRepository;

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostConstruct
    public void init() {
        // Verifica si el usuario 'admin' ya está en la base de datos
        if (loginRepository.findByNombreUsuario("admin") == null) {
            Login defaultUser = new Login();
            defaultUser.setNombreUsuario("admin");
            defaultUser.setPassword(passwordEncoder.encode("1234"));
            loginRepository.save(defaultUser);
        }
    }

    @Override
    public Login autenticarUsuario(String nombreUsuario, String password) {
        Login user = loginRepository.findByNombreUsuario(nombreUsuario);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            return user;
        }
        return null;
    }
}
