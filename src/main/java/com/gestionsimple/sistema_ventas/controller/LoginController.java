package com.gestionsimple.sistema_ventas.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.gestionsimple.sistema_ventas.model.Login;
import com.gestionsimple.sistema_ventas.service.LoginService;

import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {

    @Autowired
    private LoginService loginService;

    @GetMapping("/login")
    public String loginPage(Model model, @RequestParam(value = "error", required = false) String error) {
        if (error != null) {
            model.addAttribute("error", "Credenciales incorrectas");
        }
        return "login"; // Asegúrate de que esta vista existe en tus plantillas
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Login user = loginService.autenticarUsuario(username, password);
        if (user != null) {
            session.setAttribute("nombreUsuario", user.getNombreUsuario());
            return "redirect:/caja"; // Redirige a la ruta /caja después del inicio de sesión
        } else {
            model.addAttribute("error", "Credenciales incorrectas");
            return "login"; // Retorna la vista de login con un mensaje de error
        }
    }

}

