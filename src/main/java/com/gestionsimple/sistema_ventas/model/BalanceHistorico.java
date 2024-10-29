package com.gestionsimple.sistema_ventas.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class BalanceHistorico {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private BigDecimal gananciaTotal;
    private BigDecimal gananciaUnitaria;
    private LocalDateTime fechaBalance;
    private BigDecimal costoTotalInsumosConsumidos; // Nuevo campo para el costo total de insumos consumidos
    private BigDecimal balanceNeto; // Nuevo campo para el balance neto
    private BigDecimal inversionTotal; // Campo para inversión total

    @OneToMany
    private List<Venta> ventas;

    private BigDecimal dineroTotalRecaudado;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaBalance() {
        return fechaBalance;
    }

    public void setFechaBalance(LocalDateTime fechaBalance) {
        this.fechaBalance = fechaBalance;
    }

    public List<Venta> getVentas() {
        return ventas;
    }

    public void setVentas(List<Venta> ventas) {
        this.ventas = ventas;
    }
    
    public BigDecimal getGananciaTotal() {
        return gananciaTotal;
    }

    public void setGananciaTotal(BigDecimal gananciaTotal) {
        this.gananciaTotal = gananciaTotal;
    }

    public BigDecimal getDineroTotalRecaudado() {
        return dineroTotalRecaudado;
    }

    public void setDineroTotalRecaudado(BigDecimal dineroTotalRecaudado) {
        this.dineroTotalRecaudado = dineroTotalRecaudado;
    }

    public BigDecimal getGananciaUnitaria() {
        return gananciaUnitaria;
    }

    public void setGananciaUnitaria(BigDecimal gananciaUnitaria) {
        this.gananciaUnitaria = gananciaUnitaria;
    }

    public BigDecimal getCostoTotalInsumosConsumidos() {
        return costoTotalInsumosConsumidos;
    }

    public void setCostoTotalInsumosConsumidos(BigDecimal costoTotalInsumosConsumidos) {
        this.costoTotalInsumosConsumidos = costoTotalInsumosConsumidos;
    }

    public BigDecimal getBalanceNeto() {
        return balanceNeto;
    }

    public void setBalanceNeto(BigDecimal balanceNeto) {
        this.balanceNeto = balanceNeto;
    }

    public BigDecimal getInversionTotal() {
        return inversionTotal;
    }

    public void setInversionTotal(BigDecimal inversionTotal) {
        this.inversionTotal = inversionTotal;
    }
}
