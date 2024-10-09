package com.gestionsimple.sistema_ventas.controller;

import com.gestionsimple.sistema_ventas.model.Producto;
import com.gestionsimple.sistema_ventas.model.Compra;
import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import com.gestionsimple.sistema_ventas.model.Categoria;
import com.gestionsimple.sistema_ventas.service.ProductoService;
import com.gestionsimple.sistema_ventas.service.ScannerConfigService;
import com.gestionsimple.sistema_ventas.service.CategoriaService;
import com.gestionsimple.sistema_ventas.service.CompraService;
import com.gestionsimple.sistema_ventas.service.DetalleVentaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/productos")
public class ProductoController {

    private static final Logger logger = LoggerFactory.getLogger(ProductoController.class);

    @Autowired
    private ProductoService productoService;

    @Autowired
    private CompraService compraService;

    @Autowired
    private CategoriaService categoriaService;

    @Autowired
    private DetalleVentaService detalleVentaService;
    
    private final ScannerConfigService scannerConfigService;  // Asegúrate de declarar el servicio

    @Autowired
    public ProductoController(ProductoService productoService, 
                              CategoriaService categoriaService,
                              ScannerConfigService scannerConfigService) {
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.scannerConfigService = scannerConfigService;  // Inyección del servicio
    }
    // Mostrar todos los productos
    @GetMapping
    public String mostrarProductos(Model model) {
        logger.info("Mostrando todos los productos");
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        
        boolean alertaBajoStock = false; // Variable para verificar si hay productos con bajo stock
        boolean alertaAgotado = false; // Variable para verificar si hay productos agotados

        for (Producto producto : productos) {
            logger.info("Producto: {} - Precio de venta: {}", producto.getNombre(), producto.getPrecioVenta());

            // Verificar stock y establecer alertas
            if (producto.getStock() <= 1) {
                alertaAgotado = true; // Hay productos agotados
            } else if (producto.getStock() <= 3) {
                alertaBajoStock = true; // Hay productos con bajo stock
            }

            if (producto.getPrecioCompra().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal diferencia = producto.getPrecioVenta().subtract(producto.getPrecioCompra());
                BigDecimal porcentajeRentabilidad = diferencia
                        .divide(producto.getPrecioCompra(), RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                producto.setPorcentajeRentabilidad(porcentajeRentabilidad);
            }

            producto.calcularGananciaUnitaria();
            producto.calcularGananciaTotal();
            producto.calcularDineroTotalRecaudado();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("alertaBajoStock", alertaBajoStock);
        model.addAttribute("alertaAgotado", alertaAgotado);
        return "productos";
    }

    // Mostrar formulario de creación de producto
 // Mostrar formulario de creación de producto
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(@RequestParam(name = "codigoDeBarras", required = false) String codigoDeBarras, Model model) {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setActivo(true);

        if (codigoDeBarras != null && !codigoDeBarras.isEmpty()) {
            nuevoProducto.setCodigoDeBarras(codigoDeBarras);
        }

        System.out.println("Código de Barras: " + nuevoProducto.getCodigoDeBarras()); // Verificar el valor aquí
        
        model.addAttribute("producto", nuevoProducto);
        model.addAttribute("categorias", categoriaService.getAllCategorias());

        return "crear_producto";
    }

    
    @GetMapping("/buscarPorCodigoDeBarras")
    public ResponseEntity<Producto> buscarProductoPorCodigoDeBarras(@RequestParam String codigoDeBarras) {
        Optional<Producto> producto = productoService.getProductoByCodigoDeBarras(codigoDeBarras);
        
        return producto.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/api/get-scanned-barcode")
    @ResponseBody
    public ResponseEntity<String> getScannedBarcode() {
        // Lógica para obtener el código de barras escaneado
        String scannedBarcode = scannerConfigService.readBarcode(); // Asegúrate de que este método esté bien implementado
        return ResponseEntity.ok(scannedBarcode);
    }

    
    // Guardar producto
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto producto, Model model) {
        logger.info("Guardando producto: {}", producto);
        producto.setFechaRegistro(LocalDate.now());
        producto.setActivo(true); // Asegurar que esté activo al guardar
        if (producto.getCodigoDeBarras() == null || producto.getCodigoDeBarras().isEmpty()) {
            // Obtener el código de barras escaneado desde el escáner configurado
            String scannedBarcode = scannerConfigService.readBarcode();
            producto.setCodigoDeBarras(scannedBarcode);
        }
        productoService.guardarProducto(producto);
        model.addAttribute("mensaje", "Producto guardado exitosamente");
        return "redirect:/productos";
    }

    // Mostrar formulario de edición de producto
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEdicion(@PathVariable Long id, Model model) {
        logger.info("Mostrando formulario de edición para el producto con ID: {}", id);
        Producto producto = productoService.obtenerProductoPorId(id);
        if (producto == null) {
            logger.error("Producto con ID {} no encontrado", id);
            model.addAttribute("error", "Producto no encontrado");
            return "redirect:/productos";
        }
        model.addAttribute("producto", producto);
        model.addAttribute("categorias", categoriaService.getAllCategorias());
        return "editar_producto";
    }

    // Procesar edición de producto
    @PostMapping("/editar")
    public String procesarEdicion(@ModelAttribute("producto") Producto producto, Model model) {
        logger.info("Actualizando producto: {}", producto);
        productoService.actualizarProducto(producto);
        model.addAttribute("mensaje", "Producto actualizado exitosamente");
        return "redirect:/productos";
    }

    // Eliminar producto
    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Long id, Model model) {
        logger.info("Eliminando producto con ID: {}", id);

        // Verificar si el producto está asociado a detalles de venta
        List<DetalleVenta> detallesVenta = detalleVentaService.obtenerDetallesVentaPorProductoId(id);
        if (!detallesVenta.isEmpty()) {
            // Producto asociado a detalles de venta, mostrar mensaje de advertencia
            model.addAttribute("mensaje", "No se puede eliminar el producto porque está asociado a ventas.");
            // Obtener y retornar la lista actualizada de productos
            List<Producto> productos = productoService.obtenerTodosLosProductos();
            model.addAttribute("productos", productos);
            logger.debug("Productos obtenidos: {}", productos);
            return "productos";
        }

        productoService.eliminarProducto(id);
        model.addAttribute("mensaje", "Producto eliminado exitosamente");
        return "redirect:/productos";
    }
    
    // Activar/desactivar producto
    @PostMapping("/activar/{id}")
    public String actualizarEstadoActivo(@PathVariable("id") Long id, @RequestParam("activo") boolean activo) {
        Producto producto = productoService.obtenerProductoPorId(id);
        if (producto != null) {
            producto.setActivo(activo);
            productoService.guardarProducto(producto);
        }
        return "redirect:/productos";
    }

    // Importar compras
    @PostMapping("/{id}/importarCompras")
    public String importarCompras(@PathVariable Long id, @RequestParam("compraId") Long compraId) {
        Producto producto = productoService.obtenerProductoPorId(id);
        Compra compra = compraService.getCompraById(compraId);

        if (producto != null && compra != null) {
            if (compra.getEsPesable()) {
                producto.setStock(producto.getStock() + (int) compra.getCantidad());
            } else {
                producto.setStock(producto.getStock() + (int) compra.getCantidad());
            }
            productoService.guardarProducto(producto);
        }

        return "redirect:/productos";
    }

    // Actualizar stock
    @PostMapping("/{id}/actualizarStock")
    public String actualizarStock(@RequestParam("id") Long id, @RequestParam("newStock") int newStock, RedirectAttributes redirectAttributes) {
        productoService.actualizarStock(id, newStock);
        redirectAttributes.addFlashAttribute("message", "Stock actualizado correctamente.");
        return "redirect:/productos";
    }

    // Actualizar precio de compra
    @PostMapping("/{id}/actualizarPrecioCompra")
    public String actualizarPrecioCompra(@RequestParam("id") Long id, @RequestParam("newPrecioCompra") Double newPrecioCompra, RedirectAttributes redirectAttributes) {
        productoService.actualizarPrecioCompra(id, newPrecioCompra);
        redirectAttributes.addFlashAttribute("message", "Precio de compra actualizado correctamente.");
        return "redirect:/productos";
    }

    
    // Mostrar rentabilidad
    @GetMapping("/rentabilidad")
    public String mostrarRentabilidad(Model model) {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        model.addAttribute("productos", productos);
        return "rentabilidad";
    }
    
    
    @GetMapping("/buscarProductoPorCodigo/{codigo}")
    @ResponseBody
    public ResponseEntity<Producto> buscarProductoPorCodigo(@PathVariable String codigo) {
        Optional<Producto> productoOpt = productoService.getProductoByCodigoDeBarras(codigo);
        if (productoOpt.isPresent()) {
            return ResponseEntity.ok(productoOpt.get()); // Devuelve el producto encontrado
        } else {
            return ResponseEntity.notFound().build(); // Devuelve un 404 si no se encuentra el producto
        }
    }



 // Actualizar datos de rentabilidad
    @PostMapping("/{id}/actualizarRentabilidadDatos")
    @ResponseBody
    public ResponseEntity<String> actualizarRentabilidadDatos(@PathVariable("id") Long idProducto, @RequestBody Map<String, Double> datos) {
        Producto producto = productoService.obtenerProductoPorId(idProducto);

        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }

        try {
            // Actualizar datos de rentabilidad en el producto
            producto.setPorcentajeRentabilidad(BigDecimal.valueOf(datos.getOrDefault("porcentajeRentabilidad", 0.0)));
            producto.setPrecioVenta(BigDecimal.valueOf(datos.getOrDefault("precioVenta", 0.0)));
            producto.setGananciaTotal(BigDecimal.valueOf(datos.getOrDefault("gananciaTotal", 0.0)));
            producto.setGananciaUnitaria(BigDecimal.valueOf(datos.getOrDefault("gananciaUnitaria", 0.0)));
            producto.setInversionTotal(BigDecimal.valueOf(datos.getOrDefault("inversionTotal", 0.0)));
            producto.setDineroTotalRecaudado(BigDecimal.valueOf(datos.getOrDefault("dineroTotalRecaudado", 0.0)));
            producto.setGrasaDesperdicio(BigDecimal.valueOf(datos.getOrDefault("grasaDesperdicio", 0.0)));
            producto.setOtrosDesperdicios(BigDecimal.valueOf(datos.getOrDefault("otrosDesperdicios", 0.0)));

            // Obtener el stock original como int
            int cantidadLote = producto.getStock(); // Stock inicial del producto

            // Obtener los desperdicios
            Double grasaDesperdicio = datos.getOrDefault("grasaDesperdicio", 0.0);
            Double otrosDesperdicios = datos.getOrDefault("otrosDesperdicios", 0.0);

            // Calcular el nuevo stock (kilos utilizables)
            Double nuevoStockDouble = cantidadLote - grasaDesperdicio - otrosDesperdicios;
            if (nuevoStockDouble < 0) {
                nuevoStockDouble = 0.0; // Asegurar que no sea negativo
            }

            // Convertir a int si el stock debe ser entero
            int nuevoStock = nuevoStockDouble.intValue();

            // Actualizar el stock del producto
            producto.setStock(nuevoStock);
            
            // Obtener el precio de compra original
            BigDecimal precioCompraOriginal = producto.getPrecioCompra(); // Suponiendo que tienes un campo de precio de compra en el producto

            // Calcular el nuevo precio de compra basado en los kilos utilizables
            if (nuevoStock > 0) {
                BigDecimal precioCompraAjustado = precioCompraOriginal.multiply(BigDecimal.valueOf(cantidadLote)).divide(BigDecimal.valueOf(nuevoStock), RoundingMode.HALF_UP);
                producto.setPrecioCompra(precioCompraAjustado); // Actualizar el precio de compra del producto
            } else {
                // Si no hay stock utilizable, el precio de compra ajustado podría ser 0 o un valor que definas
                producto.setPrecioCompra(BigDecimal.ZERO);
            }

            // Guardar el producto con todos los cambios
            productoService.guardarProducto(producto);

            // Buscar la última compra no actualizada del producto
            List<Compra> compras = compraService.findComprasByProductoIdOrderByIdAsc(producto.getId());

            for (Compra compra : compras) {
                if (compra.getGananciaTotal().compareTo(BigDecimal.ZERO) == 0 || 
                    compra.getGananciaUnitaria().compareTo(BigDecimal.ZERO) == 0) {
                    
                    // Actualizar los valores de rentabilidad solo si no se han actualizado antes
                    compra.setPorcentajeRentabilidad(producto.getPorcentajeRentabilidad());
                    compra.setGananciaTotal(producto.getGananciaTotal().multiply(BigDecimal.valueOf(compra.getCantidad())));
                    compra.setGananciaUnitaria(producto.getGananciaUnitaria());
                    // Actualizar el precio de venta también
                    compra.setPrecioVenta(producto.getPrecioVenta());
                    // Guardar la compra actualizada
                    compraService.saveCompra(compra);
                    break; // Salir del bucle después de actualizar la primera compra no modificada
                }
            }

            return ResponseEntity.ok("Datos de rentabilidad y stock actualizados correctamente.");
        } catch (Exception e) {
            logger.error("Error al actualizar datos de rentabilidad y stock", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar datos");
        }
    }


    @GetMapping("/gestionStock")
    public String mostrarGestionStock(Model model) {
        logger.info("Mostrando gestión de stock");
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        
        boolean alertaBajoStock = false; // Variable para verificar si hay productos con bajo stock
        boolean alertaAgotado = false; // Variable para verificar si hay productos agotados

        for (Producto producto : productos) {
            logger.info("Producto: {} - Precio de venta: {}", producto.getNombre(), producto.getPrecioVenta());

            // Verificar stock y establecer alertas
            if (producto.getStock() <= 1) {
                alertaAgotado = true; // Hay productos agotados
            } else if (producto.getStock() <= 3) {
                alertaBajoStock = true; // Hay productos con bajo stock
            }

            // Otros cálculos relacionados con el producto si es necesario
        }

        model.addAttribute("productos", productos);
        model.addAttribute("alertaBajoStock", alertaBajoStock);
        model.addAttribute("alertaAgotado", alertaAgotado);
        return "GestionStock"; // Retorna la vista adecuada
    }

    
}
