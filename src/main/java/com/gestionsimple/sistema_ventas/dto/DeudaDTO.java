package com.gestionsimple.sistema_ventas.dto;

public class DeudaDTO {
    private Long proveedorId;
    private Double monto;

    public DeudaDTO(Long proveedorId, Double monto) {
        this.proveedorId = proveedorId;
        this.monto = monto;
    }

    // Getters y Setters
    public Long getProveedorId() {
        return proveedorId;
    }

    public void setProveedorId(Long proveedorId) {
        this.proveedorId = proveedorId;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }
}
