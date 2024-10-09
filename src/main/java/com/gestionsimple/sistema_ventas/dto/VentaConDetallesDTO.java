package com.gestionsimple.sistema_ventas.dto;

import com.gestionsimple.sistema_ventas.model.DetalleVenta;

import java.util.List;

public class VentaConDetallesDTO {
    private Long idVenta;
    private String fechaHoraFormatted;
    private String metodoPago;
    private double montoPagado; // Correspondiente al 'monto-pagado-input'
    private double vuelto; // Correspondiente al 'vuelto'
    private double total; // Correspondiente al 'total-venta'
    private List<DetalleVenta> detallesVenta;
    private String dniCliente; // Correspondiente al 'dni-cliente'
    private double montoDescuento; // Correspondiente al 'monto-descuento'
    private double recargo; // Asegúrate de que este campo exista
    private double subtotalSinDescuentos; // Correspondiente al 'subtotal-sin-descuentos'

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

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public double getMontoDescuento() {
        return montoDescuento;
    }

    public void setMontoDescuento(double montoDescuento) {
        this.montoDescuento = montoDescuento;
    }

    public double getSubtotalSinDescuentos() {
        return subtotalSinDescuentos;
    }

    public void setSubtotalSinDescuentos(double subtotalSinDescuentos) {
        this.subtotalSinDescuentos = subtotalSinDescuentos;
    }

    public List<DetalleVenta> getDetallesVenta() {
        return detallesVenta;
    }

    public void setDetallesVenta(List<DetalleVenta> detallesVenta) {
        this.detallesVenta = detallesVenta;
    }

    // Método de validación
    public boolean esValido() {
        if (detallesVenta == null || detallesVenta.isEmpty()) {
            return false; // No hay detalles de venta
        }

        for (DetalleVenta detalle : detallesVenta) {
            // Verifica que el detalle tenga una cantidad válida y un precio unitario
            if (detalle.getCantidad() <= 0 || detalle.getPrecioUnitario() < 0) {
                return false; // Cantidad o precio inválido
            }
            // Adicionalmente, puedes verificar el producto
            if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
                return false; // Producto nulo o sin ID
            }
        }
        return true; // Todo es válido
    }

	public double getRecargo() {
		return recargo;
	}

	public void setRecargo(double recargo) {
		this.recargo = recargo;
	}
}
