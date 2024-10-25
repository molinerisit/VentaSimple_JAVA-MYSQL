package com.gestionsimple.sistema_ventas.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.gestionsimple.sistema_ventas.model.Insumo;

import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;

public class BalanceDTO {
    private BigDecimal gananciaTotal;
    private BigDecimal gananciaUnitaria;
    private BigDecimal inversionTotal;
    private BigDecimal dineroTotalRecaudado;
    private  BigDecimal InsumosConsumidos;
    // Getters y setters
    public BigDecimal getGananciaTotal() {
        return gananciaTotal;
    }

    public void setGananciaTotal(BigDecimal gananciaTotal) {
        this.gananciaTotal = gananciaTotal;
    }

    public BigDecimal getGananciaUnitaria() {
        return gananciaUnitaria;
    }
    
    @OneToMany(mappedBy = "balance", cascade = CascadeType.ALL)
    private List<Insumo> insumosConsumidos;


    
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
		return InsumosConsumidos;
	}

	public void setInsumosConsumidos(BigDecimal insumosConsumidos) {
		InsumosConsumidos = insumosConsumidos;
	}

}
