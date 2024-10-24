package com.gestionsimple.sistema_ventas.controller;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.gestionsimple.sistema_ventas.model.Compra;
import com.gestionsimple.sistema_ventas.model.Producto;
import com.gestionsimple.sistema_ventas.service.CompraService;
import com.gestionsimple.sistema_ventas.service.ProductoService;

@Controller
@RequestMapping("/productos")
public class RentabilidadController {

    @Autowired
    private ProductoService productoService;
    
    @Autowired
    private CompraService compraService;

    // Mostrar rentabilidad
    @GetMapping("/rentabilidad")
    public String mostrarRentabilidad(Model model) {
        List<Producto> productos = productoService.obtenerTodosLosProductos();
        model.addAttribute("productos", productos);
        return "rentabilidad"; // Asegúrate de que esta vista exista
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar datos");
        }
    }

}
