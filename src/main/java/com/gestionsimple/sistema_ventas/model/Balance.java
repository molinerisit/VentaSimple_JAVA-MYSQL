package com.gestionsimple.sistema_ventas.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "balances")
public class Balance {

	
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "imagen", nullable = false)
    private String imagen; // Almacena la imagen en formato Base64

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion; // Para registrar cuándo se creó el balance

    @Column(name = "total_dinero_recaudado", nullable = false)
    private Double totalDineroRecaudado; // Total recaudado en la venta


    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Double getTotalDineroRecaudado() {
        return totalDineroRecaudado;
    }

    public void setTotalDineroRecaudado(Double totalDineroRecaudado) {
        this.totalDineroRecaudado = totalDineroRecaudado;
    }
}
