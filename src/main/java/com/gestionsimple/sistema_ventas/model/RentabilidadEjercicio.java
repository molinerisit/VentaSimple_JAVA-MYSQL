package com.gestionsimple.sistema_ventas.model;
import java.math.BigDecimal;

public class RentabilidadEjercicio {
    private Long idProducto;
    private BigDecimal porcentajeRentabilidad;
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;
    private BigDecimal gananciaUnitaria;
    private BigDecimal gananciaTotal;
    private BigDecimal inversionTotal;
    private BigDecimal dineroTotalRecaudado;
    private BigDecimal grasaDesperdicio;
    private BigDecimal otrosDesperdicios;
    
    

    // Constructor
    public RentabilidadEjercicio(Long idProducto, BigDecimal porcentajeRentabilidad, BigDecimal precioCompra,
                        BigDecimal grasaDesperdicio, BigDecimal otrosDesperdicios) {
        this.idProducto = idProducto;
        this.porcentajeRentabilidad = porcentajeRentabilidad;
        this.precioCompra = precioCompra;
        this.grasaDesperdicio = grasaDesperdicio;
        this.otrosDesperdicios = otrosDesperdicios;
        calcularPrecioVenta();
    }

    // Métodos de cálculo
    private void calcularPrecioVenta() {
        this.precioVenta = precioCompra.multiply(BigDecimal.ONE.add(porcentajeRentabilidad.divide(BigDecimal.valueOf(100))));
    }

    public void calcularGanancias(int cantidadLote) {
        BigDecimal kilosUtilizables = BigDecimal.valueOf(Math.max(0, cantidadLote - grasaDesperdicio.doubleValue() - otrosDesperdicios.doubleValue()));
        BigDecimal precioCompraAjustado = precioCompra.multiply(BigDecimal.valueOf(cantidadLote)).divide(kilosUtilizables, BigDecimal.ROUND_HALF_UP);
        
        this.gananciaUnitaria = kilosUtilizables.compareTo(BigDecimal.ZERO) > 0 ? precioVenta.subtract(precioCompraAjustado) : BigDecimal.ZERO;
        this.gananciaTotal = kilosUtilizables.multiply(gananciaUnitaria);
        this.inversionTotal = precioCompra.multiply(BigDecimal.valueOf(cantidadLote));
        this.dineroTotalRecaudado = kilosUtilizables.multiply(precioVenta);
    }

    // Getters y Setters
    public Long getIdProducto() {
        return idProducto;
    }

    public BigDecimal getPorcentajeRentabilidad() {
        return porcentajeRentabilidad;
    }

    public void setPorcentajeRentabilidad(BigDecimal porcentajeRentabilidad) {
        this.porcentajeRentabilidad = porcentajeRentabilidad;
        calcularPrecioVenta(); // Recalcular precio de venta si cambia la rentabilidad
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        this.precioCompra = precioCompra;
        calcularPrecioVenta(); // Recalcular precio de venta si cambia el precio de compra
    }

    public BigDecimal getPrecioVenta() {
        return precioVenta;
    }

    public BigDecimal getGananciaUnitaria() {
        return gananciaUnitaria;
    }

    public BigDecimal getGananciaTotal() {
        return gananciaTotal;
    }

    public BigDecimal getInversionTotal() {
        return inversionTotal;
    }

    public BigDecimal getDineroTotalRecaudado() {
        return dineroTotalRecaudado;
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
}
