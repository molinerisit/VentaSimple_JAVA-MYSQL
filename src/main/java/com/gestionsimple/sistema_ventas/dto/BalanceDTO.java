package com.gestionsimple.sistema_ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import com.gestionsimple.sistema_ventas.model.Insumo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

public class BalanceDTO {
    private Long id; // Identificador único del balance
    private LocalDateTime fechaCreacion; // Fecha de creación del balance
    private BigDecimal gananciaTotal; // Ganancia total en el balance
    private BigDecimal gananciaUnitaria; // Ganancia por unidad (si aplica)
    private BigDecimal inversionTotal; // Total invertido
    private BigDecimal dineroTotalRecaudado; // Dinero total recaudado en el balance
    private BigDecimal insumosConsumidos; // Total de insumos consumidos (valor en dinero)
    private BigDecimal gastoInsumos; // Total del gasto en insumos

    @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL)
    private List<Insumo> insumosConsumidosList; // Relación con los insumos consumidos
    private Set<LocalDate> fechasCompras; // Cambio a Set<LocalDate> para las fechas de compras


    // Getters y Setters
    
    public Set<LocalDate> getFechasCompras() {
        return fechasCompras;
    }

    public void setFechasCompras(Set<LocalDate> fechasCompras) {
        this.fechasCompras = fechasCompras;
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(LocalDateTime fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public BigDecimal getGananciaTotal() {
        return gananciaTotal;
    }

    public void setGananciaTotal(BigDecimal gananciaTotal) {
        this.gananciaTotal = gananciaTotal;
    }

    public BigDecimal getGananciaUnitaria() {
        return gananciaUnitaria;
    }

    public void setGananciaUnitaria(BigDecimal gananciaUnitaria) {
        this.gananciaUnitaria = gananciaUnitaria;
    }

    public BigDecimal getInversionTotal() {
        return inversionTotal;
    }

    public void setInversionTotal(BigDecimal inversionTotal) {
        this.inversionTotal = inversionTotal;
    }

    public BigDecimal getDineroTotalRecaudado() {
        return dineroTotalRecaudado;
    }

    public void setDineroTotalRecaudado(BigDecimal dineroTotalRecaudado) {
        this.dineroTotalRecaudado = dineroTotalRecaudado;
    }

    public BigDecimal getInsumosConsumidos() {
        return insumosConsumidos;
    }

    public void setInsumosConsumidos(BigDecimal insumosConsumidos) {
        this.insumosConsumidos = insumosConsumidos;
    }

    public BigDecimal getGastoInsumos() {
        return gastoInsumos;
    }

    public void setGastoInsumos(BigDecimal gastoInsumos) {
        this.gastoInsumos = gastoInsumos;
    }

    public List<Insumo> getInsumosConsumidosList() {
        return insumosConsumidosList;
    }

    public void setInsumosConsumidosList(List<Insumo> insumosConsumidosList) {
        this.insumosConsumidosList = insumosConsumidosList;
    }
}
