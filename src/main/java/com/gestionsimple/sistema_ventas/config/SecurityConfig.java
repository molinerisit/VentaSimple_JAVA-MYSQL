package com.gestionsimple.sistema_ventas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // Rutas restringidas solo para el rol ADMIN
                    .requestMatchers("/rentabilidad-ejercicio", "/rentabilidad-calculadora").hasRole("ADMIN")
                    // Permitir acceso a todos los demás a cualquier otra ruta
                    .anyRequest().authenticated()
            )
            .formLogin(formLogin ->
                formLogin
                    .loginPage("/login")
                    .permitAll()
            )
            .logout(logout ->
                logout
                    .permitAll()
            );

        return http.build();
    }

    // Definición de usuarios en memoria
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withDefaultPasswordEncoder()
            .username("admin")
            .password("1234")
            .roles("ADMIN")
            .build();

        UserDetails user1 = User.withDefaultPasswordEncoder()
            .username("Luciana")
            .password("1234")
            .roles("USER")
            .build();

        UserDetails user2 = User.withDefaultPasswordEncoder()
            .username("Veronica")
            .password("1234")
            .roles("USER")
            .build();

        UserDetails user3 = User.withDefaultPasswordEncoder()
                .username("Delfina")
                .password("1234")
                .roles("USER")
                .build();

        UserDetails user4 = User.withDefaultPasswordEncoder()
                .username("Juliana")
                .password("1234")
                .roles("USER")
                .build();
        
        UserDetails user5 = User.withDefaultPasswordEncoder()
                .username("Julian")
                .password("1234")
                .roles("USER")
                .build();

        UserDetails user6 = User.withDefaultPasswordEncoder()
                .username("Graciela")
                .password("1234")
                .roles("USER")
                .build();


        return new InMemoryUserDetailsManager(admin, user1, user2, user3, user4, user5, user6);
    }
}
