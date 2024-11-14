package com.gestionsimple.sistema_ventas.controller;

import com.gestionsimple.sistema_ventas.model.CompraInsumo;
import com.gestionsimple.sistema_ventas.model.DeudaProveedor;
import com.gestionsimple.sistema_ventas.model.Insumo;
import com.gestionsimple.sistema_ventas.model.PagoProveedor;
import com.gestionsimple.sistema_ventas.model.Proveedor;
import com.gestionsimple.sistema_ventas.model.contabilidad.AsientoContable;
import com.gestionsimple.sistema_ventas.model.contabilidad.CuentaContable;
import com.gestionsimple.sistema_ventas.service.CompraInsumoService;
import com.gestionsimple.sistema_ventas.service.DeudaProveedorService;
import com.gestionsimple.sistema_ventas.service.InsumoService;
import com.gestionsimple.sistema_ventas.service.PagoProveedorService;
import com.gestionsimple.sistema_ventas.service.ProveedorService;
import com.gestionsimple.sistema_ventas.service.contabilidad.AsientoContableService;
import com.gestionsimple.sistema_ventas.service.contabilidad.CuentaContableService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CompraInsumoController {

    private static final Logger logger = LoggerFactory.getLogger(CompraInsumoController.class);

    @Autowired
    private CompraInsumoService compraInsumoService;

    @Autowired
    private PagoProveedorService pagoProveedorService;

    @Autowired
    private DeudaProveedorService deudaProveedorService;

    
    @Autowired
    private InsumoService insumoService;

    @Autowired
    private ProveedorService proveedorService;

    @Autowired
    private AsientoContableService asientoContableService;

    @Autowired
    private CuentaContableService cuentaContableService;

    @GetMapping("/compras-insumos")
    public String listCompras(Model model) {
        logger.info("Listando todas las compras de insumos");
        model.addAttribute("compras", compraInsumoService.getAllCompras());
        return "comprasInsumos";
    }

    @GetMapping("/compras-insumos/nueva")
    public String createCompraForm(Model model) {
        logger.info("Creando formulario para nueva compra de insumo");
        model.addAttribute("compra", new CompraInsumo());
        model.addAttribute("insumos", insumoService.obtenerTodosLosInsumos());
        model.addAttribute("proveedores", proveedorService.getAllProveedores());
        return "crear_compraInsumo";
    }

    @PostMapping("/compras-insumos")
    public String saveCompraInsumo(@ModelAttribute("compra") CompraInsumo compraInsumo) {
        logger.info("Guardando nueva compra de insumo: {}", compraInsumo);

        Proveedor proveedor = proveedorService.getProveedorById(compraInsumo.getProveedor().getId());

        if (proveedor != null) {
        	 if (!compraInsumo.isPagado()) {
                 DeudaProveedor nuevaDeuda = new DeudaProveedor();
                 nuevaDeuda.setMonto(compraInsumo.getTotal());
                 nuevaDeuda.setProveedor(proveedor);
                 nuevaDeuda.setFechaRegistro(LocalDate.now());

                 // Conversión de java.sql.Date a LocalDate
                 java.sql.Date sqlDate = (java.sql.Date) compraInsumo.getFecha();
                 LocalDate localDate = sqlDate.toLocalDate();  // Convertir la fecha
                 nuevaDeuda.setFechaDeuda(localDate);  // Asignar la fecha convertida
                 nuevaDeuda.setPagado(false); // Aquí se establece que la deuda no está pagada

                 // Obtener el insumo para la descripción
                 Insumo insumo = insumoService.obtenerInsumoPorId(compraInsumo.getInsumo().getId());
                 if (insumo != null) {
                     nuevaDeuda.setDescripcion(insumo.getNombre());
                 } else {
                     nuevaDeuda.setDescripcion("Deuda por compra de insumo desconocido");
                 }

                 deudaProveedorService.registrarDeuda(proveedor.getId(), nuevaDeuda);
        	
            } else {
                // Si fue pagado completamente, registrar el pago
                PagoProveedor nuevoPago = new PagoProveedor();
                nuevoPago.setMonto(compraInsumo.getTotal());
                Insumo insumo = insumoService.obtenerInsumoPorId(compraInsumo.getInsumo().getId());
                if (insumo != null) {
                    nuevoPago.setDescripcion(insumo.getNombre());
                } else {
                    nuevoPago.setDescripcion("Pago por compra de insumo desconocido");
                }
                nuevoPago.setMedioDePago(compraInsumo.getMedioDePago());
                nuevoPago.setPagado(true);
                nuevoPago.setFecha_pago(LocalDate.now());
                nuevoPago.setProveedor(proveedor);

                // Registrar el nuevo pago
                pagoProveedorService.registrarPago(proveedor.getId(), nuevoPago);
            }

            proveedorService.saveProveedor(proveedor);
        } else {
            logger.error("Proveedor no encontrado: {}", compraInsumo.getProveedor().getId());
            return "error";
        }

        // Actualizar stock de insumo
        Insumo insumo = insumoService.obtenerInsumoPorId(compraInsumo.getInsumo().getId());
        if (insumo != null) {
            insumo.setStock(insumo.getStock() + (Double) compraInsumo.getCantidad());
            insumo.setPrecioCompra(BigDecimal.valueOf(compraInsumo.getPrecioCompra()));
            insumoService.guardarInsumo(insumo);
        } else {
            logger.error("Insumo no encontrado: {}", compraInsumo.getInsumo().getId());
            return "error";
        }

        compraInsumoService.saveCompra(compraInsumo);
        return "redirect:/compras-insumos";
    }

    // Nuevos métodos para manejar los pagos y deudas
    private void registrarDeudaProveedorInsumo(CompraInsumo compraInsumo, Proveedor proveedor) {
        logger.info("Registrando deuda de insumo de {} al proveedor {}", compraInsumo.getTotal(), proveedor.getNombre());
    }

    private void registrarPagoProveedorInsumo(CompraInsumo compraInsumo, Proveedor proveedor) {
        logger.info("Registrando pago de insumo de {} al proveedor {}", compraInsumo.getTotal(), proveedor.getNombre());
    } 


}
