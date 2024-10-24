package com.gestionsimple.sistema_ventas.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cierres_caja")
public class CierreCaja {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime fechaCierre;
    
    private LocalDateTime fechaInicio; // Add this line
    
    private double montoInicial;
    
    private double totalVentas;
    
    private double totalDescuentos;
    
    private double totalRecargos;
    
    private double totalPagosEfectivo;

    private double totalVuelto;
    
 // Add new fields for debit, credit, and QR payments
    private double totalPagosDebito;
    private double totalPagosCredito;
    private double totalPagosQR;

    // Constructor vacío necesario para JPA
    public CierreCaja() {
    }

    public CierreCaja(LocalDateTime fechaInicio, LocalDateTime fechaCierre, double montoInicial, 
            double totalVentas, double totalDescuentos, double totalRecargos, 
            double totalPagosEfectivo, double totalVuelto,
            double totalPagosDebito, double totalPagosCredito, double totalPagosQR) {
this.fechaInicio = fechaInicio;
this.fechaCierre = fechaCierre;
this.montoInicial = montoInicial;
this.totalVentas = totalVentas;
this.totalDescuentos = totalDescuentos;
this.totalRecargos = totalRecargos;
this.totalPagosEfectivo = totalPagosEfectivo;
this.totalVuelto = totalVuelto;
this.setTotalPagosDebito(totalPagosDebito);
this.setTotalPagosCredito(totalPagosCredito);
this.setTotalPagosQR(totalPagosQR);
}
    // Getters and setters
    public LocalDateTime getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(LocalDateTime fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getFechaCierre() {
        return fechaCierre;
    }

    public void setFechaCierre(LocalDateTime fechaCierre) {
        this.fechaCierre = fechaCierre;
    }

    public double getMontoInicial() {
        return montoInicial;
    }

    public void setMontoInicial(double montoInicial) {
        this.montoInicial = montoInicial;
    }

    public double getTotalVentas() {
        return totalVentas;
    }

    public void setTotalVentas(double totalVentas) {
        this.totalVentas = totalVentas;
    }

    public double getTotalDescuentos() {
        return totalDescuentos;
    }

    public void setTotalDescuentos(double totalDescuentos) {
        this.totalDescuentos = totalDescuentos;
    }

    public double getTotalRecargos() {
        return totalRecargos;
    }

    public void setTotalRecargos(double totalRecargos) {
        this.totalRecargos = totalRecargos;
    }

    public double getTotalPagosEfectivo() {
        return totalPagosEfectivo;
    }

    public void setTotalPagosEfectivo(double totalPagosEfectivo) {
        this.totalPagosEfectivo = totalPagosEfectivo;
    }

    public double getTotalVuelto() {
        return totalVuelto;
    }

    public void setTotalVuelto(double totalVuelto) {
        this.totalVuelto = totalVuelto;
    }

	public double getTotalPagosDebito() {
		return totalPagosDebito;
	}

	public void setTotalPagosDebito(double totalPagosDebito) {
		this.totalPagosDebito = totalPagosDebito;
	}

	public double getTotalPagosCredito() {
		return totalPagosCredito;
	}

	public void setTotalPagosCredito(double totalPagosCredito) {
		this.totalPagosCredito = totalPagosCredito;
	}

	public double getTotalPagosQR() {
		return totalPagosQR;
	}

	public void setTotalPagosQR(double totalPagosQR) {
		this.totalPagosQR = totalPagosQR;
	}
}
