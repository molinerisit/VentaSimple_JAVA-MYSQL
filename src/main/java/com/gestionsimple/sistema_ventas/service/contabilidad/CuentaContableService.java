package com.gestionsimple.sistema_ventas.service.contabilidad;

import com.gestionsimple.sistema_ventas.model.contabilidad.CuentaContable;
import com.gestionsimple.sistema_ventas.repository.contabilidad.CuentaContableRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class CuentaContableService {

	@Autowired
	private CuentaContableRepository cuentaContableRepository;

	public void cargarCuentasIniciales() {
		if (cuentaContableRepository.count() == 0) { // Verifica si ya existen cuentas
			List<CuentaContable> cuentas = Arrays.asList(
					new CuentaContable(1L, "Caja", "Ingreso"),
					new CuentaContable(47L, "Caja", "Egreso"),
					new CuentaContable(2L, "Proveedor", "Pasivo"), new CuentaContable(3L, "Ventas", "Ingreso"),
					new CuentaContable(4L, "Compras", "Egreso"), new CuentaContable(5L, "Banco", "Activo"),
					new CuentaContable(6L, "Clientes", "Activo"), new CuentaContable(7L, "Acreedores", "Pasivo"),
					new CuentaContable(8L, "Capital", "Pasivo"), new CuentaContable(9L, "Intereses", "Egreso"),
					new CuentaContable(10L, "Alquileres", "Egreso"),
					new CuentaContable(11L, "Ingresos por Servicios", "Ingreso"),
					new CuentaContable(12L, "Préstamos Recibidos", "Pasivo"),
					new CuentaContable(13L, "Inversiones", "Activo"), new CuentaContable(14L, "Mobiliario", "Activo"),
					new CuentaContable(15L, "Vehículos", "Activo"),
					new CuentaContable(16L, "Gastos Generales", "Egreso"), new CuentaContable(17L, "Seguros", "Egreso"),
					new CuentaContable(18L, "Sueldos y Salarios", "Egreso"), // Pago de salarios a empleados
					new CuentaContable(19L, "Bonificaciones", "Egreso"), // Bonificaciones pagadas a empleados
					new CuentaContable(20L, "Bonificaciones", "Ingreso"), // Bonificaciones recibidas
					new CuentaContable(21L, "Intereses", "Ingreso"), // Puede ser ingreso también
					new CuentaContable(22L, "Devoluciones sobre ventas", "Egreso"),
					new CuentaContable(23L, "Devoluciones sobre compras", "Ingreso"),
					new CuentaContable(24L, "Ajustes por Inflación", "Ingreso"),
					new CuentaContable(25L, "Ajustes por Inflación", "Egreso"), // Puede ser egreso también
					new CuentaContable(26L, "Descuentos Otorgados", "Egreso"),
					new CuentaContable(27L, "Descuentos Recibidos", "Ingreso"),
					new CuentaContable(28L, "Préstamos Otorgados", "Activo"),
					new CuentaContable(29L, "Amortización Acumulada", "Activo"),
					new CuentaContable(30L, "Dividendos Pagados", "Egreso"),
					new CuentaContable(31L, "Dividendos Recibidos", "Ingreso"),
					new CuentaContable(32L, "Seguridad Social", "Egreso"), // Contribuciones a la seguridad social
					new CuentaContable(33L, "Impuestos sobre la Nómina", "Egreso"), // Impuestos relacionados con la
																					// nómina
					new CuentaContable(34L, "Beneficios a Empleados", "Egreso"), // Beneficios adicionales como seguro
																					// de salud, etc.
					new CuentaContable(35L, "Gastos de Capacitación", "Egreso"), // Gastos en la capacitación de
																					// empleados
					new CuentaContable(36L, "Vacaciones Pagadas", "Egreso"), // Vacaciones pagadas a empleados
					new CuentaContable(37L, "Indemnización", "Egreso"), // Indemnización por despido o similares
					new CuentaContable(38L, "Provisión para Bonificaciones", "Pasivo"), // Provisión para futuros pagos
																						// de bonificaciones
					new CuentaContable(39L, "Provisión para Vacaciones", "Pasivo"), // Provisión para futuros pagos de
					new CuentaContable(40L, "Inventario", "Activo"), // Agregado
					new CuentaContable(41L, "Ganancia", "Ingreso"), // Agregado
					new CuentaContable(42L, "Gasto", "Egreso"),
					new CuentaContable(43L, "Transferencia", "Ingreso"),
					new CuentaContable(44L, "Transferencia", "Egreso"),
					new CuentaContable(45L, "Qr", "Ingreso"),
					new CuentaContable(46L, "Qr", "Egreso")

			);

			cuentaContableRepository.saveAll(cuentas);
		}
	}

	public Optional<CuentaContable> obtenerCuentaPorId(Long id) {
		return cuentaContableRepository.findById(id);
	}

	public CuentaContable obtenerCuentaPorNombre(String nombre) {
		return cuentaContableRepository.findByNombre(nombre)
				.orElseThrow(() -> new RuntimeException("Cuenta contable no encontrada: " + nombre));
	}

	public List<CuentaContable> obtenerCuentasPorTipo(String tipo) {
		return cuentaContableRepository.findByTipo(tipo);
	}
}
