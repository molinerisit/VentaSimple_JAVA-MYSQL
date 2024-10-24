package com.gestionsimple.sistema_ventas.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.sql.Date;

@Entity
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Date fecha;

    private Double total; // Cambiado a Double
    private double precioCompraActual;
    @Column(nullable = false)
    private Boolean esPesable;
    private double cantidad;
    private String medioDePago;
    private boolean pagado;

    private BigDecimal precioVenta = BigDecimal.ZERO;
    private BigDecimal porcentajeRentabilidad = BigDecimal.ZERO;
    private BigDecimal gananciaTotal = BigDecimal.ZERO;
    private BigDecimal gananciaUnitaria = BigDecimal.ZERO;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "producto_id", nullable = false)
    private Producto producto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    // Getters y Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getTotal() { // Cambiado a Double
        return total;
    }

    public void setTotal(Double total) { // Cambiado a Double
        this.total = total;
    }

    public double getPrecioCompraActual() {
        return precioCompraActual;
    }

    public void setPrecioCompraActual(double precioCompraActual) {
        this.precioCompraActual = precioCompraActual;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public String getMedioDePago() {
        return medioDePago;
    }

    public void setMedioDePago(String medioDePago) {
        this.medioDePago = medioDePago;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Boolean getEsPesable() {
        return esPesable;
    }

    public void setEsPesable(Boolean esPesable) {
        this.esPesable = esPesable;
    }
    
    
    public BigDecimal getPorcentajeRentabilidad() {
        return porcentajeRentabilidad;
    }

    public void setPorcentajeRentabilidad(BigDecimal porcentajeRentabilidad) {
        this.porcentajeRentabilidad = porcentajeRentabilidad;
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

	public BigDecimal getPrecioVenta() {
		return precioVenta;
	}

	public void setPrecioVenta(BigDecimal precioVenta) {
		this.precioVenta = precioVenta;
	}
}
