package com.gestionsimple.sistema_ventas.controller;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.view.RedirectView;

import com.gestionsimple.sistema_ventas.model.Categoria;
import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import com.gestionsimple.sistema_ventas.model.Producto;
import com.gestionsimple.sistema_ventas.model.Venta;
import com.gestionsimple.sistema_ventas.model.contabilidad.AsientoContable;
import com.gestionsimple.sistema_ventas.model.contabilidad.CuentaContable;
import com.gestionsimple.sistema_ventas.service.CategoriaService;
import com.gestionsimple.sistema_ventas.service.ProductoService;
import com.gestionsimple.sistema_ventas.service.VentaService;
import com.gestionsimple.sistema_ventas.service.contabilidad.AsientoContableService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import com.gestionsimple.sistema_ventas.service.contabilidad.CuentaContableService; // Importar el servicio

@Controller
public class CajaController {

	@Autowired
	private ProductoService productoService;

	@Autowired
	private VentaService ventaService;

	@Autowired
	private CategoriaService categoriaService;

	@Autowired
	private AsientoContableService asientoContableService;

	@Autowired
	private CuentaContableService cuentaContableService; // Inyecta el servicio de cuentas contables

	@GetMapping("/caja")
	public String showCajaPage(Model model, HttpSession session) {
		String nombreUsuario = (String) session.getAttribute("nombreUsuario");
		if (nombreUsuario != null) {
			model.addAttribute("nombreUsuario", nombreUsuario);
		} else {
			model.addAttribute("nombreUsuario", "Invitado");
		}

		List<Categoria> categorias = categoriaService.getAllCategorias();
		model.addAttribute("categorias", categorias);

		return "caja";
	}

	@GetMapping("/filtrarProductos/{categoriaId}")
	@ResponseBody
	public List<Producto> filtrarProductosPorCategoria(@PathVariable Long categoriaId) {
		Categoria categoria = categoriaService.getCategoriaById(categoriaId);
		if (categoria != null) {
			return productoService.buscarPorCategoriaYActivo(categoria, true);
		} else {
			return List.of();
		}
	}

	@PostMapping("/ventas")
	@ResponseBody
	public String registrarVenta(@RequestBody Venta venta) {
		venta.setFechaHora(LocalDateTime.now());
		double totalVenta = 0.0;

		// Calcular el subtotal de cada detalle de la venta
		for (DetalleVenta detalle : venta.getDetallesVenta()) {
			Producto producto = productoService.obtenerProductoPorId(detalle.getProducto().getId());
			detalle.setNombreProducto(producto.getNombre());
			detalle.setPrecioUnitario(producto.getPrecioVenta().doubleValue()); // Usa getPrecioVenta() en lugar de
																				// getPrecioUnitario()
			detalle.setSubtotal(producto.getPrecioVenta().doubleValue() * detalle.getCantidad());
			totalVenta += detalle.getSubtotal();
		}

		venta.setTotal(totalVenta);

		// Calcular vuelto si el método de pago es efectivo
		if ("EFECTIVO".equalsIgnoreCase(venta.getMetodoPago())) {
			double vuelto = venta.getMontoPagado() - totalVenta;
			venta.setVuelto(vuelto);
		} else {
			venta.setMontoPagado(0);
			venta.setVuelto(0);
		}

		ventaService.guardarVenta(venta);

		// Actualizar el stock de los productos vendidos
		for (DetalleVenta detalle : venta.getDetallesVenta()) {
			productoService.actualizarStock(detalle.getProducto().getId(), -detalle.getCantidad());
		}

		try {
			// Obtener cuentas contables
			CuentaContable cuentaCaja = cuentaContableService.obtenerCuentaPorNombre("Caja");
			CuentaContable cuentaInventario = cuentaContableService.obtenerCuentaPorNombre("Inventario");
			CuentaContable cuentaGanancia = cuentaContableService.obtenerCuentaPorNombre("Ganancia");

			// Registro de entrada de caja
			asientoContableService.registrarAsiento(cuentaCaja.getId(), totalVenta,
					"Ingreso por venta. Total: " + totalVenta, "Venta realizada. Total: " + totalVenta, "VENTA");

			// Registro de disminución del inventario
			asientoContableService.registrarAsiento(cuentaInventario.getId(), -totalVenta, // Nota: El signo negativo
																							// representa una
																							// disminución
					"Disminución de inventario por venta. Total: " + totalVenta,
					"Venta realizada. Disminución de inventario", "VENTA");

			// Registro de ganancia (si aplica)
			double ganancia = calcularGanancia(venta); // Implementa la lógica para calcular la ganancia
			asientoContableService.registrarAsiento(cuentaGanancia.getId(), ganancia,
					"Ganancia por venta. Total: " + ganancia, "Venta realizada. Ganancia", "VENTA");
		} catch (RuntimeException e) {
			System.err.println("Error al obtener cuentas contables: " + e.getMessage());
			return "Error al registrar la venta: " + e.getMessage();
		} catch (Exception e) {
			System.err.println("Error al registrar el asiento contable: " + e.getMessage());
			return "Error al registrar la venta: " + e.getMessage();
		}

		return "Venta registrada con éxito";
	}

	private CuentaContable obtenerCuentaContablePorNombre(String nombre) {
		// Implementa la lógica para obtener la cuenta contable por nombre
		return cuentaContableService.obtenerCuentaPorNombre(nombre); // Cambia según tu lógica
	}

	private double calcularGanancia(Venta venta) {
		double costoTotal = 0.0;
		for (DetalleVenta detalle : venta.getDetallesVenta()) {
			Producto producto = productoService.obtenerProductoPorId(detalle.getProducto().getId());
			double costoProducto = producto.getPrecioCompra().doubleValue() * detalle.getCantidad();
			costoTotal += costoProducto;
		}
		return venta.getTotal() - costoTotal;
	}
	
	

	@GetMapping("/logout")
	public RedirectView logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return new RedirectView("/login");
	}

	@GetMapping("/barcode/{codigo}")
	public RedirectView handleBarcodeScan(@PathVariable String codigo) {
		return new RedirectView("/nuevo_producto?codigo=" + codigo);
	}
}
