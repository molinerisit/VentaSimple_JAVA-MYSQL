package com.gestionsimple.sistema_ventas.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import com.gestionsimple.sistema_ventas.model.Insumo;

public class RentabilidadEjercicioDTO {
    private Long idVenta;
    private LocalDateTime fechaHora;
    private BigDecimal totalVenta;
    private List<DetalleVenta> detallesVenta; // Agregar este atributo
    private String metodoPago; // Asegúrate de que este campo existe
    private BigDecimal subtotal; // Nuevo campo
    private BigDecimal descuento; // Nuevo campo
    private BigDecimal gananciaTotal;
    private BigDecimal recargo; // Nuevo campo
    private Double total;
    private List<BigDecimal> gananciasPorProducto; 

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
	public List<DetalleVenta> getDetallesVenta() {
		return detallesVenta;
	}
	public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
		this.detallesVenta = detallesVenta;
	}
	public String getMetodoPago() {
		return metodoPago;
	}
	public void setMetodoPago(String metodoPago) {
		this.metodoPago = metodoPago;
	}
	public BigDecimal getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}
	public BigDecimal getDescuento() {
		return descuento;
	}
	public void setDescuento(BigDecimal descuento) {
		this.descuento = descuento;
	}
	public BigDecimal getRecargo() {
		return recargo;
	}
	public void setRecargo(BigDecimal recargo) {
		this.recargo = recargo;
	}
	public Double getTotal() {
		return total;
	}
	public void setTotal(Double total) {
		this.total = total;
	}
	public List<BigDecimal> getGananciasPorProducto() {
		return gananciasPorProducto;
	}
	public void setGananciasPorProducto(List<BigDecimal> gananciasPorProducto) {
		this.gananciasPorProducto = gananciasPorProducto;
	}

}
