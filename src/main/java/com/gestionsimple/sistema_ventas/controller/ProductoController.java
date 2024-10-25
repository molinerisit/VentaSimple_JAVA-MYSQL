package com.gestionsimple.sistema_ventas.controller;

import com.gestionsimple.sistema_ventas.model.Producto;
import com.gestionsimple.sistema_ventas.repository.HistorialPrecioRepository;
import com.gestionsimple.sistema_ventas.model.Compra;
import com.gestionsimple.sistema_ventas.model.DetalleVenta;
import com.gestionsimple.sistema_ventas.model.HistorialPrecio;
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
    private HistorialPrecioRepository historialPrecioRepository;
    
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
        List<Producto> productos = productoService.obtenerTodosLosProductos();

        boolean alertaBajoStock = false;
        boolean alertaAgotado = false;

        for (Producto producto : productos) {
            // Alertas de stock bajo o agotado
            if (producto.getStock() <= 1) {
                alertaAgotado = true;
            } else if (producto.getStock() <= 3) {
                alertaBajoStock = true;
            }

            // Calcular rentabilidad si el precio de compra es válido
            if (producto.getPrecioCompra() != null && producto.getPrecioCompra().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal diferencia = producto.getPrecioVenta().subtract(producto.getPrecioCompra());
                BigDecimal porcentajeRentabilidad = diferencia
                        .divide(producto.getPrecioCompra(), RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));

                producto.setPorcentajeRentabilidad(porcentajeRentabilidad);
            }

            // Pasar el precio de venta como argumento a calcularGananciaUnitaria
            producto.calcularGananciaUnitaria(producto.getPrecioVenta());
            producto.calcularGananciaTotal();
            producto.calcularDineroTotalRecaudado();
        }

        model.addAttribute("productos", productos);
        model.addAttribute("alertaBajoStock", alertaBajoStock);
        model.addAttribute("alertaAgotado", alertaAgotado);
        return "productos";
    }


    // Mostrar formulario de creación de producto
    @GetMapping("/crear")
    public String mostrarFormularioCreacion(@RequestParam(name = "codigoDeBarras", required = false) String codigoDeBarras, Model model) {
        Producto nuevoProducto = new Producto();
        nuevoProducto.setActivo(true);

        if (codigoDeBarras != null && !codigoDeBarras.isEmpty()) {
            nuevoProducto.setCodigoDeBarras(codigoDeBarras);
        }

        model.addAttribute("producto", nuevoProducto);
        model.addAttribute("categorias", categoriaService.getAllCategorias());

        return "crear_producto";
    }

    // Guardar producto
    @PostMapping("/guardar")
    public String guardarProducto(@ModelAttribute("producto") Producto producto, Model model) {
        producto.setFechaRegistro(LocalDate.now());
        producto.setActivo(true);
        if (producto.getCodigoDeBarras() == null || producto.getCodigoDeBarras().isEmpty()) {
            String scannedBarcode = scannerConfigService.readBarcode();
            producto.setCodigoDeBarras(scannedBarcode);
        }
        productoService.guardarProducto(producto);
        model.addAttribute("mensaje", "Producto guardado exitosamente");
        return "redirect:/productos";
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

    @PostMapping("/editar")
    public String procesarEdicion(@ModelAttribute("producto") Producto producto, Model model) {
        logger.info("Actualizando producto: {}", producto);

        // Obtener el producto actual antes de la actualización
        Producto productoExistente = productoService.obtenerProductoPorId(producto.getId());

        if (productoExistente != null) {
            // Verificar si el precio de compra ha cambiado
            if (productoExistente.getPrecioCompra().compareTo(producto.getPrecioCompra()) != 0) {
                // Crear un nuevo registro de historial de precios solo para precio de compra
                HistorialPrecio historialPrecio = new HistorialPrecio();
                historialPrecio.setProducto(productoExistente); // Asignar el producto existente
                historialPrecio.setPrecioCompra(productoExistente.getPrecioCompra()); // Precio anterior
                historialPrecio.setPrecioVenta(null); // No se actualiza el precio de venta
                historialPrecio.setFechaModificacion(LocalDate.now());

                // Guardar en el historial de precios
                historialPrecioRepository.save(historialPrecio);
                logger.info("Historial de precios (compra) actualizado para el producto con ID: {}", producto.getId());
            }

            // Verificar si el precio de venta ha cambiado
            if (productoExistente.getPrecioVenta().compareTo(producto.getPrecioVenta()) != 0) {
                // Crear un nuevo registro de historial de precios solo para precio de venta
                HistorialPrecio historialPrecio = new HistorialPrecio();
                historialPrecio.setProducto(productoExistente); // Asignar el producto existente
                historialPrecio.setPrecioCompra(null); // No se actualiza el precio de compra
                historialPrecio.setPrecioVenta(productoExistente.getPrecioVenta()); // Precio anterior
                historialPrecio.setFechaModificacion(LocalDate.now());

                // Guardar en el historial de precios
                historialPrecioRepository.save(historialPrecio);
                logger.info("Historial de precios (venta) actualizado para el producto con ID: {}", producto.getId());
            }

            // Actualizar el producto en la base de datos
            productoService.actualizarProducto(producto);
            model.addAttribute("mensaje", "Producto actualizado exitosamente");
        } else {
            model.addAttribute("error", "Producto no encontrado");
        }

        return "redirect:/productos";
    }

 // Actualizar precios y manejar el historial
    @PostMapping("/{id}/actualizarPrecio")
    public String actualizarPrecio(
            @PathVariable Long id,
            @RequestParam("newPrecioCompra") BigDecimal newPrecioCompra,
            @RequestParam("newPrecioVenta") BigDecimal newPrecioVenta,
            RedirectAttributes redirectAttributes) {

        Producto producto = productoService.obtenerProductoPorId(id);

        if (producto != null) {
            // Verificar si ha habido un cambio de precio de compra
            if (newPrecioCompra != null && newPrecioCompra.compareTo(producto.getPrecioCompra()) != 0) {
                producto.setPrecioCompra(newPrecioCompra);
                producto.setPrecioCompraCambio(true);
                producto.setFechaCambioPrecio(LocalDate.now());
            }

            // Actualizar precio de venta
            producto.setPrecioVenta(newPrecioVenta);

            productoService.guardarProducto(producto);

            // Crear el historial de precios
            HistorialPrecio historialPrecio = new HistorialPrecio();
            historialPrecio.setProducto(producto);
            historialPrecio.setPrecioCompra(newPrecioCompra);
            historialPrecio.setPrecioVenta(newPrecioVenta);
            historialPrecio.setFechaModificacion(LocalDate.now());

            // Guardar en el repositorio
            historialPrecioRepository.save(historialPrecio);

            redirectAttributes.addFlashAttribute("mensaje", "Precios actualizados correctamente.");
        } else {
            redirectAttributes.addFlashAttribute("error", "Producto no encontrado.");
        }

        return "redirect:/productos";
    }


    // Verificar cambio de precio
    @PostMapping("/{id}/verificarCambioPrecio")
    public String verificarCambioPrecio(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        Producto producto = productoService.obtenerProductoPorId(id);
        if (producto != null && producto.isPrecioCompraCambio()) {
            producto.setPrecioCompraCambio(false); // Marcar como verificado
            productoService.guardarProducto(producto);
            redirectAttributes.addFlashAttribute("message", "Cambio de precio verificado.");
        }
        return "redirect:/productos";
    }


    @PostMapping("/{id}/actualizarPrecioCompra")
    public String actualizarPrecioCompra(@RequestParam("id") Long id, @RequestParam("newPrecioCompra") BigDecimal newPrecioCompra, RedirectAttributes redirectAttributes) {
        Producto productoActual = productoService.obtenerProductoPorId(id);
        BigDecimal precioAnterior = productoActual.getPrecioCompra();

        // Comprobar si el precio ha cambiado antes de la actualización
        if (!precioAnterior.equals(newPrecioCompra)) {
            // Marcar el cambio de precio
            productoActual.setPrecioCompraCambio(true); 

            // Actualizar el precio de compra
            productoActual.setPrecioCompra(newPrecioCompra); 
            
            // Aquí se compara si el precio actual es mayor o menor al anterior
            if (productoActual.getPrecioCompraActual().compareTo(precioAnterior) > 0) {
                redirectAttributes.addFlashAttribute("mensaje", "El precio de compra ha subido.");
            } else {
                redirectAttributes.addFlashAttribute("mensaje", "El precio de compra ha bajado.");
            }
            
            productoService.guardarProducto(productoActual); // Guardar el producto con el cambio
            redirectAttributes.addFlashAttribute("alertaCambioPrecio", true);
        } else {
            productoActual.setPrecioCompraCambio(false); // No ha cambiado el precio
            productoService.guardarProducto(productoActual); // Asegúrate de guardar cualquier cambio
        }

        redirectAttributes.addFlashAttribute("message", "Precio de compra actualizado correctamente.");
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
    
    @GetMapping("/gestionPrecios")
    public String mostrarGestionPrecios(Model model) {
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
        return "GestionPrecios"; // Retorna la vista adecuada
    }

    
}
