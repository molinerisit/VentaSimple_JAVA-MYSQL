package com.gestionsimple.sistema_ventas.controller;

import com.gestionsimple.sistema_ventas.model.Compra;
import com.gestionsimple.sistema_ventas.model.CompraInsumo;
import com.gestionsimple.sistema_ventas.model.DeudaProveedor;
import com.gestionsimple.sistema_ventas.model.PagoProveedor;
import com.gestionsimple.sistema_ventas.model.Proveedor;
import com.gestionsimple.sistema_ventas.service.CompraInsumoService;
import com.gestionsimple.sistema_ventas.service.CompraService;
import com.gestionsimple.sistema_ventas.service.DeudaProveedorService;
import com.gestionsimple.sistema_ventas.service.PagoProveedorService;
import com.gestionsimple.sistema_ventas.service.ProveedorService;

import java.time.format.DateTimeFormatter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindingResult;
import jakarta.validation.Valid;
import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/proveedores")
public class ProveedorController {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorController.class);

    @Autowired
    private PagoProveedorService pagoProveedorService;

    @Autowired
    private CompraService compraService; 
    
    @Autowired
    private CompraInsumoService compraInsumoService; 

    @Autowired
    private ProveedorService proveedorService;
    
    @Autowired
    private DeudaProveedorService deudaProveedorService;

    // Método auxiliar para obtener un proveedor por ID
    private Proveedor fetchProveedor(Long id) {
        Proveedor proveedor = proveedorService.getProveedorById(id);
        if (proveedor == null) {
            logger.warn("Proveedor no encontrado con ID: {}", id);
        } else {
            logger.debug("Proveedor encontrado: {}", proveedor);
        }
        return proveedor;
    }

    // Listar todos los proveedores
    @GetMapping
    public String listProveedores(Model model) {
        logger.debug("Listando todos los proveedores");
        List<Proveedor> proveedores = proveedorService.getAllProveedores();

        // Para cada proveedor, calculamos el total de pagos y deudas
        for (Proveedor proveedor : proveedores) {
            // Obtener las deudas y pagos
            List<PagoProveedor> pagos = proveedor.getPagos();
            List<DeudaProveedor> deudas = proveedor.getDeudas();

         // Sumar solo las deudas que no están pagadas
            double totalDeuda = deudas.stream()
                                      .filter(deuda -> !deuda.isPagado())  // Filtrar las deudas no pagadas
                                      .mapToDouble(DeudaProveedor::getMonto)
                                      .sum();
            model.addAttribute("totalDeuda", totalDeuda);


            // Sumar todos los pagos
            double totalPagos = pagos != null ? pagos.stream().mapToDouble(PagoProveedor::getMonto).sum() : 0;

            // Puedes agregar estos totales al modelo si es necesario, pero también puedes mostrarlos directamente en la vista
            model.addAttribute("totalDeuda", totalDeuda);
            model.addAttribute("totalPagos", totalPagos);
        }

        logger.debug("Proveedores encontrados: {}", proveedores);
        model.addAttribute("proveedores", proveedores);
        
        return "listaProveedores";
    }


    // Mostrar formulario para crear un nuevo proveedor
    @GetMapping("/nuevo")
    public String createProveedorForm(Model model, HttpServletRequest request) {
        logger.debug("Creando formulario para nuevo proveedor");
        model.addAttribute("proveedor", new Proveedor());
        model.addAttribute("requestURI", request.getRequestURI());
        return "crearProveedor";
    }

    // Guardar nuevo proveedor
    @PostMapping
    public String saveProveedor(@Valid @ModelAttribute("proveedor") Proveedor proveedor, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Error de validación al guardar proveedor: {}", result.getAllErrors());
            return "crearProveedor";
        }
        logger.info("Guardando nuevo proveedor: {}", proveedor);
        proveedorService.saveProveedor(proveedor);
        return "redirect:/proveedores";
    }

    // Mostrar formulario para editar un proveedor existente
    @GetMapping("/editar/{id}")
    public String editProveedorForm(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        logger.debug("Editando proveedor con ID: {}", id);
        Proveedor proveedor = fetchProveedor(id);
        if (proveedor != null) {
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("requestURI", request.getRequestURI());
            return "editarProveedor";
        } else {
            logger.error("Proveedor no encontrado con ID: {}", id);
            return "redirect:/proveedores";
        }
    }

    // Actualizar proveedor existente
    @PostMapping("/actualizar/{id}")
    public String updateProveedor(@PathVariable("id") Long id, @Valid @ModelAttribute("proveedor") Proveedor proveedor, BindingResult result, Model model) {
        if (result.hasErrors()) {
            logger.error("Error de validación al actualizar proveedor: {}", result.getAllErrors());
            return "editarProveedor";
        }
        logger.info("Actualizando proveedor con ID: {}", id);
        Proveedor existingProveedor = fetchProveedor(id);
        if (existingProveedor != null) {
            existingProveedor.setNombre(proveedor.getNombre());
            existingProveedor.setContacto(proveedor.getContacto());
            existingProveedor.setDireccion(proveedor.getDireccion());
            existingProveedor.setTelefono(proveedor.getTelefono());
            existingProveedor.setEmail(proveedor.getEmail());
            //existingProveedor.setDeuda(proveedor.getDeuda());
            existingProveedor.setPagos(proveedor.getPagos());
            proveedorService.saveProveedor(existingProveedor);
            return "redirect:/proveedores";
        } else {
            logger.error("Proveedor no encontrado con ID: {}", id);
            return "redirect:/proveedores";
        }
    }

    // Eliminar un proveedor
    @GetMapping("/eliminar/{id}")
    public String deleteProveedor(@PathVariable("id") Long id) {
        logger.info("Eliminando proveedor con ID: {}", id);
        proveedorService.deleteProveedorById(id);
        return "redirect:/proveedores";
    }

    
    
    
 



    // Mostrar formulario para registrar un pago a un proveedor
    @GetMapping("/{id}/registrar-pago")
    public String mostrarFormularioPago(@PathVariable Long id, Model model) {
        // Obtener el proveedor por ID
        Proveedor proveedor = proveedorService.getProveedorById(id);
        model.addAttribute("proveedor", proveedor);

        // Inicializar un nuevo objeto PagoProveedor
        model.addAttribute("pago", new PagoProveedor());

        // Obtener las deudas pendientes por proveedor
        List<DeudaProveedor> deudas = deudaProveedorService.getDeudasPendientesPorProveedor(id);
        model.addAttribute("deudasPendientes", deudas);

        // Formatear la fecha de la primera deuda, si existe
        if (deudas != null && !deudas.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = deudas.get(0).getFechaDeuda().format(formatter);
            model.addAttribute("formattedFechaDeuda", formattedDate);
        } else {
            model.addAttribute("formattedFechaDeuda", "No hay deudas disponibles");
        }

        return "crear_pagoProveedores";
    }





 // Registrar un nuevo pago
    @PostMapping("/{id}/registrar-pago")
    public String registrarPago(@PathVariable Long id, 
                                 @Valid @ModelAttribute("pago") PagoProveedor pago, 
                                 BindingResult result, 
                                 @RequestParam List<Long> deudaIds, 
                                 Model model) {
        if (result.hasErrors()) {
            // Manejo de errores de validación
            List<DeudaProveedor> deudasPendientes = deudaProveedorService.getDeudasPendientesPorProveedor(id);
            
            model.addAttribute("deudasPendientes", deudasPendientes);
            model.addAttribute("proveedor", proveedorService.getProveedorById(id));

            return "crear_pagoProveedores";
        }

        // Crear un nuevo objeto PagoProveedor para asegurarte de que es un nuevo registro
        PagoProveedor nuevoPago = new PagoProveedor();
        nuevoPago.setMonto(pago.getMonto());
        nuevoPago.setDescripcion(pago.getDescripcion());
        nuevoPago.setMedioDePago(pago.getMedioDePago());
        nuevoPago.setPagado(true); // Establecer que el pago está realizado
        nuevoPago.setFecha_pago(pago.getFecha_pago()); // Mantener la fecha del pago
        nuevoPago.setProveedor(proveedorService.getProveedorById(id)); // Asignar el proveedor

        // Registrar el pago como un nuevo registro
        pagoProveedorService.registrarPago(id, nuevoPago);

        // Actualizar solo las deudas seleccionadas
        for (Long deudaId : deudaIds) {
            DeudaProveedor deuda = deudaProveedorService.getDeudaById(deudaId);
            if (deuda != null && !deuda.isPagado()) { 
                deuda.setPagado(true);
                deudaProveedorService.saveDeuda(deuda);
            }
        }

        return "redirect:/proveedores";
    }


 // Ver detalles de un proveedor
    @GetMapping("/detalles/{id}")
    public String verDetalleProveedor(@PathVariable Long id, Model model) {
        logger.info("Cargando detalles del proveedor con ID: {}", id);

        Proveedor proveedor = proveedorService.getProveedorById(id);
        if (proveedor == null) {
            logger.warn("Proveedor no encontrado con ID: {}", id);
            return "redirect:/proveedores";
        }

        model.addAttribute("proveedor", proveedor);

        // Obtener las deudas y pagos
        List<PagoProveedor> pagos = proveedor.getPagos();
        List<DeudaProveedor> deudas = proveedor.getDeudas(); // Asegúrate de tener esta lista

     // Sumar solo las deudas que no están pagadas
        double totalDeuda = deudas.stream()
                                  .filter(deuda -> !deuda.isPagado())  // Filtrar las deudas no pagadas
                                  .mapToDouble(DeudaProveedor::getMonto)
                                  .sum();
        model.addAttribute("totalDeuda", totalDeuda);


        // Sumar todos los pagos
        double totalPagos = pagos.stream().mapToDouble(PagoProveedor::getMonto).sum();
        model.addAttribute("totalPagos", totalPagos);

        // Formatear fecha de pagos
        if (pagos != null && !pagos.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = pagos.get(0).getFecha_pago().format(formatter);
            model.addAttribute("formattedFechaPago", formattedDate);
        } else {
            model.addAttribute("formattedFechaPago", "No hay pagos disponibles");
        }

        if (deudas != null && !deudas.isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            String formattedDate = deudas.get(0).getFechaDeuda().format(formatter);
            model.addAttribute("formattedFechaDeuda", formattedDate);
        } else {
            model.addAttribute("formattedFechaDeuda", "No hay deudas disponibles");
        }
        
        // Obtener compras y compras de insumos
        List<Compra> compras = proveedorService.obtenerComprasPorProveedor(id);
        logger.info("Número de compras encontradas para el proveedor {}: {}", proveedor.getNombre(), compras.size());
        model.addAttribute("compras", compras);

        List<CompraInsumo> comprasInsumo = proveedorService.obtenerCompraInsumoPorProveedor(id);
        logger.info("Número de insumos encontrados para el proveedor {}: {}", proveedor.getNombre(), comprasInsumo.size());
        model.addAttribute("comprasInsumos", comprasInsumo);

        return "detalleProveedor";
    }


    
    @GetMapping("/detalleProveedor/{id}")
    public String getDetalleProveedor(@PathVariable Long id, Model model) {
        Proveedor proveedor = proveedorService.findById(id);
        if (proveedor != null) {
            model.addAttribute("proveedor", proveedor);
            model.addAttribute("pagoproveedor", proveedor); // Esto es importante
        } else {
            model.addAttribute("error", "Proveedor no encontrado.");
        }
        return "detalleProveedor";
    }} 