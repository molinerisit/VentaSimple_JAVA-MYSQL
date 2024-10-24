package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.CierreCaja;
import com.gestionsimple.sistema_ventas.repository.CierreCajaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CierreCajaService {

    private final CierreCajaRepository cierreCajaRepository;

    @Autowired
    public CierreCajaService(CierreCajaRepository cierreCajaRepository) {
        this.cierreCajaRepository = cierreCajaRepository;
    }

    // Crear o guardar un nuevo cierre de caja
    public CierreCaja saveCierreCaja(CierreCaja cierreCaja) {
        return cierreCajaRepository.save(cierreCaja);
    }

    // Obtener un cierre de caja por ID
    public Optional<CierreCaja> getCierreCajaById(Long id) {
        return cierreCajaRepository.findById(id);
    }

    // Obtener todos los cierres de caja
    public List<CierreCaja> getAllCierresCaja() {
        return cierreCajaRepository.findAll();
    }

    // Actualizar un cierre de caja
    public CierreCaja updateCierreCaja(CierreCaja cierreCaja) {
        return cierreCajaRepository.save(cierreCaja);
    }

    // Eliminar un cierre de caja
    public void deleteCierreCaja(Long id) {
        cierreCajaRepository.deleteById(id);
    }

    // Obtener el cierre de caja abierto (si existe uno sin fecha de cierre)
    public CierreCaja obtenerCierreCajaAbierto() {
        return cierreCajaRepository.findFirstByFechaCierreIsNull()
                .orElseThrow(() -> new IllegalStateException("No hay un cierre de caja abierto."));
    }
}
