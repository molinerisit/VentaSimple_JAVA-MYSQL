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
 
    @PostMapping("/{id}/actualizarRentabilidadDatos")
    @ResponseBody
    public ResponseEntity<String> actualizarRentabilidadDatos(@PathVariable("id") Long idProducto, @RequestBody Map<String, Double> datos) {
        Producto producto = productoService.obtenerProductoPorId(idProducto);

        if (producto == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Producto no encontrado");
        }

        try {
            // Actualizar los datos de rentabilidad en el producto
            producto.setPorcentajeRentabilidad(BigDecimal.valueOf(datos.getOrDefault("porcentajeRentabilidad", 0.0)));
            producto.setPrecioVenta(BigDecimal.valueOf(datos.getOrDefault("precioVenta", 0.0)));
            producto.setGrasaDesperdicio(BigDecimal.valueOf(datos.getOrDefault("grasaDesperdicio", 0.0)));
            producto.setOtrosDesperdicios(BigDecimal.valueOf(datos.getOrDefault("otrosDesperdicios", 0.0)));

            // Obtener kilos utilizables
            int cantidadLote = producto.getStock();  // Stock original del producto
            Double grasaDesperdicio = datos.getOrDefault("grasaDesperdicio", 0.0);
            Double otrosDesperdicios = datos.getOrDefault("otrosDesperdicios", 0.0);
            Double kilosUtilizables = cantidadLote - grasaDesperdicio - otrosDesperdicios;
            kilosUtilizables = Math.max(kilosUtilizables, 0.0); // Asegurar que no sea negativo

            if (kilosUtilizables == 0) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("No hay kilos utilizables. Verifique los datos de desperdicio.");
            }
            
            

            // Calcular costo de insumos por kilo
            Double insumosVarios = datos.getOrDefault("insumosVarios", 0.0);
            Double valorInsumosVarios = datos.getOrDefault("valorInsumosVarios", 0.0);
            BigDecimal costoInsumosPorKilo = BigDecimal.valueOf(insumosVarios * valorInsumosVarios).divide(BigDecimal.valueOf(kilosUtilizables), RoundingMode.HALF_UP);

            // Calcular ganancia unitaria bruta y neta
            BigDecimal gananciaUnitariaBruta = producto.getPrecioVenta().subtract(producto.getPrecioCompra());
            producto.setGananciaUnitariaBruta(gananciaUnitariaBruta);

            BigDecimal gananciaUnitariaNeta = gananciaUnitariaBruta.subtract(costoInsumosPorKilo);
            producto.setGananciaUnitariaNeta(gananciaUnitariaNeta);

            // Actualizar el stock (kilos utilizables)
            producto.setStock(kilosUtilizables.intValue());

            // Guardar el producto con todos los cambios
            productoService.guardarProducto(producto);

            return ResponseEntity.ok("Datos de rentabilidad actualizados correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al actualizar los datos: " + e.getMessage());
        }
    }
}
