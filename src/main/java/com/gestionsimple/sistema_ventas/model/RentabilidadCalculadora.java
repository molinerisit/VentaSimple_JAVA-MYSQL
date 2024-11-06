package com.gestionsimple.sistema_ventas.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.Column;
import java.math.BigDecimal;

@Entity
public class RentabilidadCalculadora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreProducto;

    @Column(nullable = false)
    private BigDecimal precioCompra;

    @Column(nullable = false)
    private BigDecimal precioVenta;

    @Column(nullable = false)
    private BigDecimal porcentajeRentabilidad;

    @Column(nullable = false)
    private BigDecimal costoInsumos = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal desperdicio = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal costoProduccion = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal gastosAdicionales = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal margenDeseado = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal rentabilidadCalculada = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal gananciaUnitariaNeta = BigDecimal.ZERO;
    
    @Column(nullable = false)
    private BigDecimal kilosNetos = BigDecimal.ZERO; // Kilos después de restar el desperdicio

    @Column(nullable = false)
    @NotNull(message = "La cantidad es obligatoria")
    @Min(value = 0, message = "La cantidad debe ser mayor o igual a cero")
    private int stock;

    @NotNull(message = "La cantidad de kilos utilizables es obligatoria")
    private BigDecimal kilosUtilizables; // Nuevo campo para la cantidad de kilos utilizables

    private BigDecimal gananciaTotal; // Nuevo campo para la ganancia total

    // Constructor sin argumentos para inicializar valores por defecto
    public RentabilidadCalculadora() {
        this.costoInsumos = BigDecimal.ZERO;
        this.desperdicio = BigDecimal.ZERO;
        this.costoProduccion = BigDecimal.ZERO;
        this.gastosAdicionales = BigDecimal.ZERO;
        this.margenDeseado = BigDecimal.ZERO;
        this.rentabilidadCalculada = BigDecimal.ZERO;
        this.gananciaUnitariaNeta = BigDecimal.ZERO;
        this.kilosUtilizables = BigDecimal.ZERO; // Inicializar kilosUtilizables

    }

    // Getters y setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
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

    public BigDecimal getPorcentajeRentabilidad() {
        return porcentajeRentabilidad;
    }

    public void setPorcentajeRentabilidad(BigDecimal porcentajeRentabilidad) {
        this.porcentajeRentabilidad = porcentajeRentabilidad;
    }

    public BigDecimal getCostoInsumos() {
        return costoInsumos;
    }

    public void setCostoInsumos(BigDecimal costoInsumos) {
        this.costoInsumos = costoInsumos;
    }

  

    public BigDecimal getCostoProduccion() {
        return costoProduccion;
    }

    public void setCostoProduccion(BigDecimal costoProduccion) {
        this.costoProduccion = costoProduccion;
    }

    public BigDecimal getGastosAdicionales() {
        return gastosAdicionales;
    }

    public void setGastosAdicionales(BigDecimal gastosAdicionales) {
        this.gastosAdicionales = gastosAdicionales;
    }

    public BigDecimal getMargenDeseado() {
        return margenDeseado;
    }

    public void setMargenDeseado(BigDecimal margenDeseado) {
        this.margenDeseado = margenDeseado;
    }

    public BigDecimal getRentabilidadCalculada() {
        return rentabilidadCalculada;
    }

    public void setRentabilidadCalculada(BigDecimal rentabilidadCalculada) {
        this.rentabilidadCalculada = rentabilidadCalculada;
    }
    
    public BigDecimal getGananciaUnitariaNeta() {
        return gananciaUnitariaNeta;
    }

    public void setGananciaUnitariaNeta(BigDecimal gananciaUnitariaNeta) {
        this.gananciaUnitariaNeta = gananciaUnitariaNeta;
    }

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
	
	

	public void calcularKilosNetos() {
	    this.kilosNetos = BigDecimal.valueOf(this.stock).subtract(this.desperdicio);
	}
	
	public BigDecimal getDesperdicio() {
	    return desperdicio;
	}

	public void setDesperdicio(BigDecimal desperdicio) {
	    this.desperdicio = desperdicio;
	}

	public BigDecimal getKilosNetos() {
	    return kilosNetos;
	}

	public void setKilosNetos(BigDecimal kilosNetos) {
	    this.kilosNetos = kilosNetos;
	}
	
	  public BigDecimal getKilosUtilizables() {
	        return kilosUtilizables;
	    }

	    public void setKilosUtilizables(BigDecimal kilosUtilizables) {
	        this.kilosUtilizables = kilosUtilizables;
	    }

	    public BigDecimal getGananciaTotal() {
	        return gananciaTotal;
	    }

	    public void setGananciaTotal(BigDecimal gananciaTotal) {
	        this.gananciaTotal = gananciaTotal;
	    }
}
