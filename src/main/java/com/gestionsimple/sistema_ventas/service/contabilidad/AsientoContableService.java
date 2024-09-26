package com.gestionsimple.sistema_ventas.service.contabilidad;


import com.gestionsimple.sistema_ventas.model.contabilidad.AsientoContable;
import com.gestionsimple.sistema_ventas.model.contabilidad.CuentaContable;
import com.gestionsimple.sistema_ventas.repository.contabilidad.AsientoContableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AsientoContableService {

    @Autowired
    private AsientoContableRepository asientoContableRepository;

    @Autowired
    private CuentaContableService cuentaContableService;

    public AsientoContable registrarAsiento(Long cuentaContableId, double monto, String detalles, String descripcion, String tipo) {
        Optional<CuentaContable> cuentaOpt = cuentaContableService.obtenerCuentaPorId(cuentaContableId);
        if (cuentaOpt.isEmpty()) {
            throw new IllegalArgumentException("Cuenta Contable no encontrada");
        }

        CuentaContable cuentaContable = cuentaOpt.get();

        AsientoContable asiento = new AsientoContable();
        asiento.setCuentaContable(cuentaContable);
        asiento.setFecha(new Date()); // Establece la fecha actual
        asiento.setMonto(monto);
        asiento.setDetalles(detalles);
        asiento.setDescripcion(descripcion);
        asiento.setTipo(tipo);

        return asientoContableRepository.save(asiento);
    }

    public List<AsientoContable> obtenerTodosLosAsientos() {
        return asientoContableRepository.findAll();
    }
}
