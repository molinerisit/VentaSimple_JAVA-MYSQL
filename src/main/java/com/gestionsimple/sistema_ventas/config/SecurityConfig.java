package com.gestionsimple.sistema_ventas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable() // Desactiva CSRF solo si estás seguro que no es necesario
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    .requestMatchers("/login").permitAll() // Permite el acceso a la ruta de login
                    .anyRequest().authenticated() // Requiere autenticación para cualquier otra ruta
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login") // Configura la página de login personalizada
                    .permitAll() // Permite el acceso a la página de login
            )
            .logout(logout ->
                logout
                    .permitAll() // Permite el acceso a la funcionalidad de logout
            );

        return http.build();
    }
}
