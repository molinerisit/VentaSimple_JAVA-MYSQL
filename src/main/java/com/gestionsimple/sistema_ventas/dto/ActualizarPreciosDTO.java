package com.gestionsimple.sistema_ventas.dto;

import java.math.BigDecimal;

public class ActualizarPreciosDTO {
    private BigDecimal precioCompra;
    private BigDecimal precioVenta;

    // Getters y setters
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
}
