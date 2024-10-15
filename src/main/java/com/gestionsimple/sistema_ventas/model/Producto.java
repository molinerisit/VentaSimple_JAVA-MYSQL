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
public class Producto {

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
    private int stock;

    private BigDecimal grasaDesperdicio = BigDecimal.ZERO;
    private BigDecimal otrosDesperdicios = BigDecimal.ZERO;

    private LocalDate fechaRegistro;

    @Column(nullable = true, unique = true) // nullable si es opcional, unique si quieres que sea único
    private String codigoDeBarras;

    private BigDecimal porcentajeRentabilidad = BigDecimal.ZERO;
    private BigDecimal gananciaTotal = BigDecimal.ZERO;
    private BigDecimal gananciaUnitaria = BigDecimal.ZERO;
    private BigDecimal inversionTotal = BigDecimal.ZERO;
    private BigDecimal dineroTotalRecaudado = BigDecimal.ZERO;

    private Boolean esPesable;
    private String descripcion;
    private boolean activo;

    //@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id") // Asegúrate de que la columna coincida con tu base de datos
    @ManyToOne(fetch = FetchType.EAGER)

    private Categoria categoria;

    //@OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true)
    //@JsonIgnore
    //private List<Compra> compras;
    
    //@OneToMany(mappedBy = "producto", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    //private List<Compra> compras;

    
    @OneToMany(mappedBy = "producto", fetch = FetchType.LAZY)
    private List<Compra> compras;

    
    

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

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }
    
    public String getCodigoDeBarras() {
        return codigoDeBarras;
    }

    public void setCodigoDeBarras(String codigoDeBarras) {
        this.codigoDeBarras = codigoDeBarras;
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

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
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
