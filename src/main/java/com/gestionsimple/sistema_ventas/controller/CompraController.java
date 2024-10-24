package com.gestionsimple.sistema_ventas.controller;

import com.gestionsimple.sistema_ventas.model.Compra;
import com.gestionsimple.sistema_ventas.model.DeudaProveedor;
import com.gestionsimple.sistema_ventas.model.HistorialPrecio;
import com.gestionsimple.sistema_ventas.model.PagoProveedor;
import com.gestionsimple.sistema_ventas.model.Producto;
import com.gestionsimple.sistema_ventas.model.Proveedor;
import com.gestionsimple.sistema_ventas.model.contabilidad.AsientoContable;
import com.gestionsimple.sistema_ventas.model.contabilidad.CuentaContable;
import com.gestionsimple.sistema_ventas.repository.HistorialPrecioRepository;
import com.gestionsimple.sistema_ventas.service.CompraService;
import com.gestionsimple.sistema_ventas.service.DeudaProveedorService;
import com.gestionsimple.sistema_ventas.service.PagoProveedorService;
import com.gestionsimple.sistema_ventas.service.ProductoService;
import com.gestionsimple.sistema_ventas.service.ProveedorService;
import com.gestionsimple.sistema_ventas.service.contabilidad.AsientoContableService;
import com.gestionsimple.sistema_ventas.service.contabilidad.CuentaContableService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CompraController {

    private static final Logger logger = LoggerFactory.getLogger(CompraController.class);

    @Autowired
    private CompraService compraService;

    @Autowired
    private PagoProveedorService pagoProveedorService;

    @Autowired
    private HistorialPrecioRepository historialPrecioRepository;
    
    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private AsientoContableService asientoContableService;

    @Autowired
    private CuentaContableService cuentaContableService;

    @Autowired
    private DeudaProveedorService deudaProveedorService;

    
    @GetMapping("/compras")
    public String listCompras(Model model) {
        logger.info("Listando todas las compras");
        model.addAttribute("compras", compraService.getAllCompras());
        return "compras";
    }

    @GetMapping("/compras/nueva")
    public String createCompraForm(Model model) {
        logger.info("Creando formulario para nueva compra");
        model.addAttribute("compra", new Compra());
        model.addAttribute("productos", productoService.obtenerTodosLosProductos());
        model.addAttribute("proveedores", proveedorService.getAllProveedores());
        return "crear_compra";
    }

    
    @GetMapping("/compras/buscarProducto")
    public String buscarProductoPorCodigoDeBarras(@RequestParam("codigoDeBarras") String codigoDeBarras, Model model) {
        logger.info("Buscando producto por código de barras: {}", codigoDeBarras);
        Optional<Producto> productoOpt = productoService.getProductoByCodigoDeBarras(codigoDeBarras);

        if (productoOpt.isPresent()) {
            model.addAttribute("producto", productoOpt.get());
        } else {
            model.addAttribute("error", "Producto no encontrado con el código de barras: " + codigoDeBarras);
        }
        
        return "resultado_busqueda_producto"; // Página que muestra el resultado de la búsqueda
    }

    @PostMapping("/compras")
    public String saveCompra(@ModelAttribute("compra") Compra compra) {
        logger.info("Guardando nueva compra: {}", compra);

        Proveedor proveedor = proveedorService.getProveedorById(compra.getProveedor().getId());

        if (proveedor != null) {
            if (!compra.isPagado()) {
                DeudaProveedor nuevaDeuda = new DeudaProveedor();
                nuevaDeuda.setMonto(compra.getTotal());
                nuevaDeuda.setProveedor(proveedor);
                nuevaDeuda.setFechaRegistro(LocalDate.now());

                // Conversión de java.sql.Date a LocalDate
                java.sql.Date sqlDate = (java.sql.Date) compra.getFecha();
                LocalDate localDate = sqlDate.toLocalDate(); // Convertir la fecha
                nuevaDeuda.setFechaDeuda(localDate); // Asignar la fecha convertida
                nuevaDeuda.setPagado(false); // Aquí se establece que la deuda no está pagada

                Producto producto = productoService.obtenerProductoPorId(compra.getProducto().getId());
                if (producto != null) {
                    nuevaDeuda.setDescripcion("Deuda por compra de producto: " + producto.getNombre());
                } else {
                    nuevaDeuda.setDescripcion("Deuda por compra de producto desconocido");
                }

                deudaProveedorService.registrarDeuda(proveedor.getId(), nuevaDeuda);
            } else {
                PagoProveedor nuevoPago = new PagoProveedor();
                nuevoPago.setMonto(compra.getTotal());
                Producto producto = productoService.obtenerProductoPorId(compra.getProducto().getId());
                if (producto != null) {
                    nuevoPago.setDescripcion("Pago por compra de producto: " + producto.getNombre());
                } else {
                    nuevoPago.setDescripcion("Pago por compra de producto desconocido");
                }
                nuevoPago.setMedioDePago(compra.getMedioDePago());
                nuevoPago.setPagado(true);
                nuevoPago.setFecha_pago(LocalDate.now());
                nuevoPago.setProveedor(proveedor);

                pagoProveedorService.registrarPago(proveedor.getId(), nuevoPago);
            }

            proveedorService.saveProveedor(proveedor);
        } else {
            logger.error("Proveedor no encontrado: {}", compra.getProveedor().getId());
            return "error";
        }

        // Actualizar stock del producto
        Producto producto = productoService.obtenerProductoPorId(compra.getProducto().getId());
        if (producto != null) {
            // Actualiza el stock
            producto.setStock(producto.getStock() + (int) compra.getCantidad());

            // Verificar si el precio de compra ha cambiado
         // Verificar si el precio de compra ha cambiado
            if (producto.getPrecioCompraActual().compareTo(BigDecimal.valueOf(compra.getPrecioCompraActual())) != 0) {
                // Guardar el historial de precios
                HistorialPrecio historialPrecio = new HistorialPrecio();
                historialPrecio.setProducto(producto);
                historialPrecio.setPrecioCompra(BigDecimal.valueOf(compra.getPrecioCompraActual())); // Guardar precio anterior
                historialPrecio.setFechaModificacion(LocalDate.now());

                // Guardar el historial y manejar excepciones
                try {
                    historialPrecioRepository.save(historialPrecio);
                    logger.info("Historial de precios guardado para el producto con ID: {}", producto.getId());
                } catch (Exception e) {
                    logger.error("Error al guardar el historial de precios: {}", e.getMessage());
                }

                // Actualizar el precioCompraActual con el nuevo valor
                producto.actualizarPrecioCompraActual(BigDecimal.valueOf(compra.getPrecioCompraActual()));
                logger.info("Historial de precios actualizado para el producto con ID: {}", producto.getId());
            }


            productoService.guardarProducto(producto);
        } else {
            logger.error("Producto no encontrado: {}", compra.getProducto().getId());
            return "error";
        }

        compraService.saveCompra(compra);
        return "redirect:/compras";
    }


    // Nuevos métodos para manejar los pagos y deudas
    private void registrarDeudaProveedor(Compra compra, Proveedor proveedor) {
        // Aquí puedes implementar la lógica para registrar el movimiento de deuda
        logger.info("Registrando deuda de {} al proveedor {}", compra.getTotal(), proveedor.getNombre());
    }

    private void registrarPagoProveedor(Compra compra, Proveedor proveedor) {
        // Implementa la lógica para registrar el pago
        logger.info("Registrando pago de {} al proveedor {}", compra.getTotal(), proveedor.getNombre());
    }  


    private double calcularGastoTotal(Compra compra) {
        // Verifica si el total es null y retorna 0.0 si lo es, de lo contrario retorna el valor del total
        return compra.getTotal() != null ? compra.getTotal() : 0.0;
    }

}
