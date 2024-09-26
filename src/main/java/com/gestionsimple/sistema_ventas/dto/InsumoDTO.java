package com.gestionsimple.sistema_ventas.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.gestionsimple.sistema_ventas.model.Insumo;

public class InsumoDTO {
    private Long id;
    private String nombre;

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

    public static InsumoDTO fromEntity(Insumo insumo) {
        InsumoDTO dto = new InsumoDTO();
        dto.setId(insumo.getId());
        dto.setNombre(insumo.getNombre());
        return dto;
    }

    public static List<InsumoDTO> fromEntities(List<Insumo> insumos) {
        return insumos.stream()
                .map(InsumoDTO::fromEntity)
                .collect(Collectors.toList());
    }
}
