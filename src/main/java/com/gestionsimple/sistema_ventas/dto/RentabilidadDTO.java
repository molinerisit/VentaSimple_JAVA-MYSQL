package com.gestionsimple.sistema_ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.gestionsimple.sistema_ventas.model.Insumo;

public class RentabilidadDTO {
    private Long idVenta;
    private LocalDateTime fechaHora;
    private BigDecimal totalVenta;
    private BigDecimal gananciaTotal;
	public LocalDateTime getFechaHora() {
		return fechaHora;
	}
	public void setFechaHora(LocalDateTime fechaHora) {
		this.fechaHora = fechaHora;
	}
	public Long getIdVenta() {
		return idVenta;
	}
	public void setIdVenta(Long idVenta) {
		this.idVenta = idVenta;
	}
	public BigDecimal getGananciaTotal() {
		return gananciaTotal;
	}
	public void setGananciaTotal(BigDecimal gananciaTotal) {
		this.gananciaTotal = gananciaTotal;
	}
	public BigDecimal getTotalVenta() {
		return totalVenta;
	}
	public void setTotalVenta(BigDecimal totalVenta) {
		this.totalVenta = totalVenta;
	}

}
