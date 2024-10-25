package com.gestionsimple.sistema_ventas.dto;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.gestionsimple.sistema_ventas.model.Insumo;

public class InsumoDTO {
    private Long id;
    private String nombre;
    private BigDecimal cantidad; // Nuevo campo para la cantidad

    public InsumoDTO() {}

    public InsumoDTO(Long id, String nombre, BigDecimal cantidad) {
        this.id = id;
        this.nombre = nombre;
        this.cantidad = cantidad; // Inicializar la cantidad
    }

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

    public BigDecimal getCantidad() {
        return cantidad; // Getter para la cantidad
    }

    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad; // Setter para la cantidad
    }

    public static InsumoDTO fromEntity(Insumo insumo) {
        InsumoDTO dto = new InsumoDTO();
        dto.setId(insumo.getId());
        dto.setNombre(insumo.getNombre());
        // Aquí podrías agregar la cantidad actual si fuera necesario
        return dto;
    }

    public static List<InsumoDTO> fromEntities(List<Insumo> insumos) {
        return insumos.stream()
                .map(InsumoDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return "InsumoDTO{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", cantidad=" + cantidad + // Agregar cantidad en el toString
                '}';
    }
}
