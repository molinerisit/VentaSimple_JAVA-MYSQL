package com.gestionsimple.sistema_ventas.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class DeudaProveedor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double monto; // Cantidad adeudada

    private String descripcion;

    @Column(nullable = false)
    private LocalDate fechaDeuda; // Fecha de la deuda

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
    
    @Column(nullable = false)
    private LocalDate fechaRegistro; // Nueva columna para la fecha de la deuda

    @Column(nullable = false)
    private boolean pagado; // Indica si el pago ya fue realizado o está pendiente

    // Getters y Setters
    public Long getId() {
        return id;
    }
    
    public boolean isPagado() {
        return pagado;
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

    public LocalDate getFechaDeuda() {
        return fechaDeuda;
    }

    public void setFechaDeuda(LocalDate fechaDeuda) {
        this.fechaDeuda = fechaDeuda;
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
    
    
    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }
}
