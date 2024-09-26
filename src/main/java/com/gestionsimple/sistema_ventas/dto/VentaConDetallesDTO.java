package com.gestionsimple.sistema_ventas.dto;

import com.gestionsimple.sistema_ventas.model.DetalleVenta;

import java.util.List;

public class VentaConDetallesDTO {
    private Long idVenta;
    private String fechaHoraFormatted;
    private String metodoPago;
    private double montoPagado;
    private double vuelto;
    private double total;
    private List<DetalleVenta> detallesVenta;

    // Getters y Setters
    public Long getIdVenta() {
        return idVenta;
    }

    public void setIdVenta(Long idVenta) {
        this.idVenta = idVenta;
    }

    public String getFechaHoraFormatted() {
        return fechaHoraFormatted;
    }

    public void setFechaHoraFormatted(String fechaHoraFormatted) {
        this.fechaHoraFormatted = fechaHoraFormatted;
    }

    public String getMetodoPago() {
        return metodoPago;
    }

    public void setMetodoPago(String metodoPago) {
        this.metodoPago = metodoPago;
    }

    public double getMontoPagado() {
        return montoPagado;
    }

    public void setMontoPagado(double montoPagado) {
        this.montoPagado = montoPagado;
    }

    public double getVuelto() {
        return vuelto;
    }

    public void setVuelto(double vuelto) {
        this.vuelto = vuelto;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }

    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }
}
