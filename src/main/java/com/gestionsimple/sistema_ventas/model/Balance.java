package com.gestionsimple.sistema_ventas.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.*;

@Entity
@Table(name = "balances")
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "inversion_total")
    private double inversionTotal;

    @Column(name = "dinero_total_recaudado")
    private double dineroTotalRecaudado;
    
    @Column(name = "gasto_insumos")
    private double gastoInsumos;

    @Column(name = "ganancia_total")
    private double gananciaTotal;
    
    @Column(name = "balance_neto") // Nuevo campo
    private double balanceNeto;

    
    @ElementCollection
    private Set<LocalDate> fechasCompras; // Este es el nuevo campo para las fechas
    
    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getGananciaTotal() {
        return gananciaTotal;
    }

    public void setGananciaTotal(double gananciaTotal) {
        this.gananciaTotal = gananciaTotal;
    }

    public double getInversionTotal() {
        return inversionTotal;
    }

    public void setInversionTotal(double inversionTotal) {
        this.inversionTotal = inversionTotal;
    }

    public double getDineroTotalRecaudado() {
        return dineroTotalRecaudado;
    }

    public void setDineroTotalRecaudado(double dineroTotalRecaudado) {
        this.dineroTotalRecaudado = dineroTotalRecaudado;
    }

    public double getGastoInsumos() {
        return gastoInsumos;
    }

    public void setGastoInsumos(double gastoInsumos) {
        this.gastoInsumos = gastoInsumos;
    }

    public double getBalanceNeto() {
        return balanceNeto;
    }

    public void setBalanceNeto(double balanceNeto) {
        this.balanceNeto = balanceNeto;
    }
    
    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }
    
    public Set<LocalDate> getFechasCompras() {
        return fechasCompras;
    }

    public void setFechasCompras(Set<LocalDate> fechasCompras) {
        this.fechasCompras = fechasCompras;
    }
}
