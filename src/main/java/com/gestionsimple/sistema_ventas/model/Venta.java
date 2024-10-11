package com.gestionsimple.sistema_ventas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "venta_id")
    private List<DetalleVenta> detallesVenta;

    private String metodoPago;
    
    private String dniCliente;  // Correspondiente al 'dni-cliente'

    private LocalDateTime fechaHora;
    private double total; // Correspondiente al 'total-venta'
    private double montoPagado; // Correspondiente al 'monto-pagado-input'
    private double vuelto; // Correspondiente al 'vuelto'
    private String fechaHoraFormatted; // Campo para la fecha formateada
    private double montoDescuento; // Correspondiente al 'monto-descuento'
    private double recargo; // Correspondiente al monto de recargo
    private double subtotalSinDescuentos; // Correspondiente al 'subtotal-sin-descuentos'
    private boolean esCuentaCorriente; // Nuevo campo para cuenta corriente

    @ManyToMany
    private List<Producto> productos;
    
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = true)
    private Cliente cliente;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
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

    public String getFechaHoraFormatted() {
        return fechaHoraFormatted;
    }

    public void setFechaHoraFormatted(String fechaHoraFormatted) {
        this.fechaHoraFormatted = fechaHoraFormatted;
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

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }

    public List<Producto> getProductos() {
        return productos;
    }

    public void setProductos(List<Producto> productos) {
        this.productos = productos;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

	public double getRecargo() {
		return recargo;
	}

	public void setRecargo(double recargo) {
		this.recargo = recargo;
	}
	
	// Getters y Setters
    public boolean isEsCuentaCorriente() {
        return esCuentaCorriente;
    }

    public void setEsCuentaCorriente(boolean esCuentaCorriente) {
        this.esCuentaCorriente = esCuentaCorriente;
    }
}
