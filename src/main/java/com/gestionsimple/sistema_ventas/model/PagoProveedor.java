package com.gestionsimple.sistema_ventas.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class PagoProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double monto;

    private String descripcion;

    @Column(nullable = false)
    private String medioDePago; // Efectivo, transferencia, etc.

    @Column(nullable = false)
    private boolean pagado; // Indica si el pago ya fue realizado o está pendiente

    @Column(nullable = false)
    private LocalDate fecha_pago; // Mantener esta propiedad

    
    
    // Relación con Proveedor
    @ManyToOne
    @JoinColumn(name = "proveedor_id", nullable = false)
    private Proveedor proveedor;

    // Relación con Compra (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_id")
    private Compra compra;

    // Relación con CompraInsumo (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "compra_insumo_id")
    private CompraInsumo compraInsumo;

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public LocalDate getFecha_pago() {
        return fecha_pago;
    }

    public void setFecha_pago(LocalDate fecha_pago) {
        this.fecha_pago = fecha_pago;
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

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Compra getCompra() {
        return compra;
    }

    public void setCompra(Compra compra) {
        this.compra = compra;
    }

    public CompraInsumo getCompraInsumo() {
        return compraInsumo;
    }

    public void setCompraInsumo(CompraInsumo compraInsumo) {
        this.compraInsumo = compraInsumo;
    }
}
