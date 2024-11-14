package com.gestionsimple.sistema_ventas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Insumo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotNull(message = "El precio de compra es obligatorio")
    @Min(value = 0, message = "El precio de compra debe ser mayor o igual a cero")
    private BigDecimal precioCompra = BigDecimal.ZERO;

    @NotNull(message = "El precio de venta es obligatorio")
    @Min(value = 0, message = "El precio de venta debe ser mayor o igual a cero")
    private BigDecimal precioVenta = BigDecimal.ZERO;
    
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser mayor o igual a cero")
    private BigDecimal cantidad = BigDecimal.ZERO;

    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser mayor o igual a cero")
    private Double stock = 0.0;

    private BigDecimal grasaDesperdicio = BigDecimal.ZERO;
    private BigDecimal otrosDesperdicios = BigDecimal.ZERO;

    private LocalDate fechaRegistro;

    private BigDecimal porcentajeRentabilidad = BigDecimal.ZERO;
    private BigDecimal gananciaTotal = BigDecimal.ZERO;
    private BigDecimal gananciaUnitaria = BigDecimal.ZERO;
    private BigDecimal inversionTotal = BigDecimal.ZERO;
    private BigDecimal dineroTotalRecaudado = BigDecimal.ZERO;

    private Boolean esPesable;
    private String descripcion;
    private boolean activo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id") // Asegúrate de que la columna coincida con tu base de datos
    private Categoria categoria;

 // Relación con CompraInsumo
    @OneToMany(mappedBy = "insumo")
    private List<CompraInsumo> comprasInsumo;

    @ManyToOne
    @JoinColumn(name = "proveedor_id") // Ensure this matches your database schema
    private Proveedor proveedor;
    
    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public void setPrecioVenta(BigDecimal precioVenta) {
        this.precioVenta = precioVenta;
    }

    public Double getStock() {
        return stock;
    }

    public void setStock(Double stock) {
        this.stock = stock;
    }

    public BigDecimal getGrasaDesperdicio() {
        return grasaDesperdicio;
    }

    public void setGrasaDesperdicio(BigDecimal grasaDesperdicio) {
        this.grasaDesperdicio = grasaDesperdicio;
    }

    public BigDecimal getOtrosDesperdicios() {
        return otrosDesperdicios;
    }

    public void setOtrosDesperdicios(BigDecimal otrosDesperdicios) {
        this.otrosDesperdicios = otrosDesperdicios;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
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

    public Boolean getEsPesable() {
        return esPesable;
    }

    public void setEsPesable(Boolean esPesable) {
        this.esPesable = esPesable;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public List<CompraInsumo> getCompras() {
        return comprasInsumo;
    }

    public void setCompras(List<CompraInsumo> comprasInsumo) {
        this.comprasInsumo = comprasInsumo;
    }

    // Métodos adicionales
    public void actualizarInversionTotal() {
        this.inversionTotal = this.precioCompra.multiply(BigDecimal.valueOf(this.stock));
    }

    public void calcularGananciaUnitaria() {
        this.gananciaUnitaria = this.precioVenta.subtract(this.precioCompra);
    }

    public void calcularGananciaTotal() {
        this.gananciaTotal = this.gananciaUnitaria.multiply(BigDecimal.valueOf(this.stock));
    }

    public void calcularDineroTotalRecaudado() {
        this.dineroTotalRecaudado = this.precioVenta.multiply(BigDecimal.valueOf(this.stock));
    }
}
