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

    // Relación con ventas (ahora usando la entidad Venta en lugar del DTO)
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

    public void setInversionTotal(BigDecimal inversionTotal) {
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
}
