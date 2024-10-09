package com.gestionsimple.sistema_ventas.controller;

import java.sql.Date;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

import com.gestionsimple.sistema_ventas.dto.ProductoDTO;
import com.gestionsimple.sistema_ventas.model.Categoria;
import com.gestionsimple.sistema_ventas.model.Cliente;
import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import com.gestionsimple.sistema_ventas.model.Login;
import com.gestionsimple.sistema_ventas.model.Producto;
import com.gestionsimple.sistema_ventas.model.Venta;
import com.gestionsimple.sistema_ventas.model.contabilidad.AsientoContable;
import com.gestionsimple.sistema_ventas.model.contabilidad.CuentaContable;
import com.gestionsimple.sistema_ventas.service.CategoriaService;
import com.gestionsimple.sistema_ventas.service.ClienteService;
import com.gestionsimple.sistema_ventas.service.PrintConfigService;
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
    private PrintConfigService printConfigService; // Inyección del servicio
	  
	@Autowired
	private ClienteService clienteService;

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
	    // Establecer fecha y hora actual
	    venta.setFechaHora(LocalDateTime.now());
	    double totalVenta = 0.0;
	    double descuentoCliente = 0.0;
	    double recargo = 0.0; // Inicializar el recargo

	    // Obtener el nombre de usuario del contexto de seguridad
	    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
	    String nombreUsuario = authentication != null ? authentication.getName() : "Desconocido";

	    StringBuilder recibo = new StringBuilder();
	    // Formatear la fecha y hora
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
	    String fechaFormateada = venta.getFechaHora().format(formatter);
	    recibo.append("\n");
	    recibo.append("-------- RESUMEN DE COMPRA --------\n");
	    recibo.append("Fecha: ").append(fechaFormateada).append("\n");
	    recibo.append("Metodo de Pago: ").append(venta.getMetodoPago()).append("\n");
	    recibo.append("Cajero: ").append(nombreUsuario).append("\n");

	    // Verificar si hay un cliente asociado a través del DNI
	    Cliente cliente = null;
	    if (venta.getDniCliente() != null && !venta.getDniCliente().isEmpty()) {
	        Optional<Cliente> clienteOpt = clienteService.obtenerClientePorDni(venta.getDniCliente());

	        if (clienteOpt.isPresent()) {
	            cliente = clienteOpt.get(); // Obtener el cliente existente
	            descuentoCliente = cliente.getDescuento();
	            recibo.append("Cliente: ").append(cliente.getDni()).append("\n");
	        } else {
	            // Si el cliente no existe, guardarlo primero
	            cliente = new Cliente(); // Crear un nuevo cliente
	            cliente.setDni(venta.getDniCliente()); // Establecer el DNI
	            clienteService.guardarCliente(cliente); // Guardar el nuevo cliente
	            recibo.append("Cliente: ").append(cliente.getDni()).append(" (Nuevo cliente registrado)\n");
	        }
	    } else {
	        recibo.append("Cliente: No registrado\n");
	    }

	    recibo.append("Detalles de la compra:\n");

	    // Procesar detalles de la venta
	    for (DetalleVenta detalle : venta.getDetallesVenta()) {
	        // Si el producto es manual o no registrado
	        if (detalle.getProducto() == null || detalle.getProducto().getId() == null) {
	            if (detalle.getNombreProducto() == null || detalle.getNombreProducto().isEmpty()) {
	                detalle.setNombreProducto("Producto manual");
	            }
	            detalle.setSubtotal(detalle.getPrecioUnitario() * detalle.getCantidad());
	            totalVenta += detalle.getSubtotal();

	            recibo.append("Producto: ").append(detalle.getNombreProducto()).append("\n");
	            recibo.append("Cantidad: ").append(detalle.getCantidad()).append("\n");
	            recibo.append("Precio Unitario: ").append(String.format("%.2f", detalle.getPrecioUnitario())).append("\n");
	            recibo.append("Subtotal: ").append(String.format("%.2f", detalle.getSubtotal())).append("\n");
	            recibo.append("-------------------------------\n");
	        } else {
	            // Producto registrado
	            Producto producto = productoService.obtenerProductoPorId(detalle.getProducto().getId());
	            if (producto != null) {
	                detalle.setNombreProducto(producto.getNombre());
	                detalle.setPrecioUnitario(producto.getPrecioVenta().doubleValue());
	                detalle.setSubtotal(producto.getPrecioVenta().doubleValue() * detalle.getCantidad());
	                totalVenta += detalle.getSubtotal();

	                recibo.append("Producto: ").append(detalle.getNombreProducto()).append("\n");
	                recibo.append("Cantidad: ").append(detalle.getCantidad()).append("\n");
	                recibo.append("Precio Unitario: ").append(String.format("%.2f", detalle.getPrecioUnitario())).append("\n");
	                recibo.append("Subtotal: ").append(String.format("%.2f", detalle.getSubtotal())).append("\n");
	                recibo.append("-------------------------------\n");
	            }
	        }
	    }

	    // Aplicar el descuento del cliente
	    double descuentoAplicado = (totalVenta * descuentoCliente) / 100;
	    double totalConDescuento = totalVenta - descuentoAplicado;

	    // Calcular el recargo si el método de pago es Tarjeta de Crédito
	    if ("TARJETACREDITO".equalsIgnoreCase(venta.getMetodoPago())) {
	        recargo = totalConDescuento * 0.15; // 15% de recargo
	    }

	    // Guardar valores en la venta
	    venta.setSubtotalSinDescuentos(totalVenta); // Guardar el subtotal sin descuentos
	    venta.setMontoDescuento(descuentoAplicado); // Guardar el descuento en la venta
	    venta.setRecargo(recargo);
	    venta.setTotal(totalConDescuento + recargo); // Guardar el total con el descuento y el recargo

	    // Agregar totales al recibo
	    recibo.append("Subtotal sin descuentos: ").append(String.format("%.2f", totalVenta)).append("\n");
	    recibo.append("Descuentos: ").append(String.format("%.2f", descuentoAplicado)).append("\n");
	    recibo.append("Recargo: ").append(String.format("%.2f", recargo)).append("\n"); // Añadir el recargo al recibo
	    recibo.append("Total: ").append(String.format("%.2f", totalConDescuento + recargo)).append("\n"); // Mostrar el total con recargo

	    // Calcular vuelto si el método de pago es efectivo
	    if ("EFECTIVO".equalsIgnoreCase(venta.getMetodoPago())) {
	        double vuelto = venta.getMontoPagado() - (totalConDescuento + recargo);
	        venta.setVuelto(vuelto);
	        
	        recibo.append("Monto Pagado: ").append(String.format("%.2f", venta.getMontoPagado())).append("\n");
	        recibo.append("Vuelto: ").append(String.format("%.2f", venta.getVuelto())).append("\n");
	    }

	    recibo.append("\n\n"); // Espacio en blanco al final para el corte del ticket
	    recibo.append("-------- GRACIAS POR SU COMPRA --------\n");
	    recibo.append("\n\n"); // Espacio en blanco al final para el corte del ticket

	    // Guardar la venta en la base de datos
	    ventaService.guardarVenta(venta);

	    // Actualizar el stock de los productos vendidos
	    for (DetalleVenta detalle : venta.getDetallesVenta()) {
	        if (detalle.getProducto() != null && detalle.getProducto().getId() != null) {
	            productoService.actualizarStock(detalle.getProducto().getId(), -detalle.getCantidad());
	        }
	    }

	    // Imprimir el recibo
	    try {
	        printConfigService.printReceipt(recibo.toString());
	    } catch (Exception e) {
	        System.err.println("Error al imprimir el recibo: " + e.getMessage());
	    }

	    return "Venta registrada con éxito\n" + recibo.toString(); // Devuelve el detalle del recibo también
	}



	private CuentaContable obtenerCuentaContablePorNombre(String nombre) {
		// Implementa la lógica para obtener la cuenta contable por nombre
		return cuentaContableService.obtenerCuentaPorNombre(nombre); // Cambia según tu lógica
	}

	private double calcularGanancia(Venta venta) {
	    double costoTotal = 0.0;
	    for (DetalleVenta detalle : venta.getDetallesVenta()) {
	        // Verificar si el producto es nulo
	        if (detalle.getProducto() != null) {
	            Producto producto = productoService.obtenerProductoPorId(detalle.getProducto().getId());
	            if (producto != null) {
	                double costoProducto = producto.getPrecioCompra().doubleValue() * detalle.getCantidad();
	                costoTotal += costoProducto;
	            }
	        } else {
	            System.out.println("Producto en detalle de venta es nulo. No se puede calcular el costo.");
	        }
	    }
	    return venta.getTotal() - costoTotal;
	}

	
	@GetMapping("/buscarProductoPorCodigo")
	@ResponseBody
	public ProductoDTO buscarProductoPorCodigo(@RequestParam String codigoDeBarras) {
	    // Log para depuración
	    System.out.println("Buscando producto con código de barras: " + codigoDeBarras);

	    // Verifica que el código no sea nulo o vacío
	    if (codigoDeBarras == null || codigoDeBarras.trim().isEmpty()) {
	        throw new RuntimeException("Código de barras vacío");
	    }

	    Producto producto = productoService.getProductoByCodigoDeBarras(codigoDeBarras)
	        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

	    // Log para verificar el producto encontrado
	    System.out.println("Producto encontrado: " + producto.getNombre());

	    // Convertir la entidad Producto a ProductoDTO
	    return ProductoDTO.fromEntity(producto);
	}


    @GetMapping("/productos/codigo/{codigo}")
    @ResponseBody
    public ResponseEntity<Producto> getProductoByCodigo(@PathVariable String codigo) {
        // Log for debugging
        System.out.println("Buscando producto con código: " + codigo);
        
        // Retrieve the product by barcode
        Producto producto = productoService.getProductoByCodigoDeBarras(codigo)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Log for verification
        System.out.println("Producto encontrado: " + producto.getNombre());

        return ResponseEntity.ok(producto);
    }


	@GetMapping("/logout")
	public RedirectView logout(HttpServletRequest request, HttpServletResponse response) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null) {
			new SecurityContextLogoutHandler().logout(request, response, auth);
		}
		return new RedirectView("/login");
	}


}
