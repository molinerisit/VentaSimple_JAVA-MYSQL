package com.gestionsimple.sistema_ventas.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;
    private String contacto;
    private String direccion;
    private String telefono;
    private String email;
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    private List<DeudaProveedor> deudas; // Nueva relación con las deudas
    
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    private List<Compra> compras;

    
    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL)
    private List<PagoProveedor> pagos;
   
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

    public String getContacto() {
        return contacto;
    }

    public void setContacto(String contacto) {
        this.contacto = contacto;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Compra> getCompras() {
        return compras;
    }

    public void setCompras(List<Compra> compras) {
        this.compras = compras;
    }

    public List<PagoProveedor> getPagos() {
        return pagos;
    }

    public void setPagos(List<PagoProveedor> pagos) {
        this.pagos = pagos;
    }

    public List<DeudaProveedor> getDeudas() {
        return deudas;
    }

    public void setDeudas(List<DeudaProveedor> deudas) {
        this.deudas = deudas;
    }

    // Método para calcular la deuda actual (ya tienes algo similar)
    public Double calcularDeudaActual() {
        Double totalDeudas = deudas.stream().mapToDouble(DeudaProveedor::getMonto).sum();
        Double totalPagos = pagos.stream().mapToDouble(PagoProveedor::getMonto).sum();
        return totalDeudas - totalPagos;
    }
}
