package com.gestionsimple.sistema_ventas.controller;

import com.gestionsimple.sistema_ventas.model.Producto;
import com.gestionsimple.sistema_ventas.model.RentabilidadCalculadora;
import com.gestionsimple.sistema_ventas.repository.ProductoRepository;
import com.gestionsimple.sistema_ventas.repository.RentabilidadCalculadoraRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/rentabilidad-calculadora")
public class RentabilidadCalculadoraController {

    @Autowired
    private RentabilidadCalculadoraRepository rentabilidadCalculadoraRepository;

    @Autowired
    private ProductoRepository productoRepository; // Agregado el repositorio de productos

    @GetMapping
    public String listarelementos(Model model) {
        List<RentabilidadCalculadora> rentabilidades = rentabilidadCalculadoraRepository.findAll();
        model.addAttribute("rentabilidadCalculadoras", rentabilidades);
        model.addAttribute("RoundingMode", RoundingMode.class);
        return "calculadora_rentabilidad"; // Nombre del archivo HTML
    }
    
    @PostMapping("/clonar")
    public ResponseEntity<?> clonarProductos() {
        System.out.println("Iniciando clonación de productos...");

        List<Producto> productosExistentes = productoRepository.findAll();
        List<RentabilidadCalculadora> productosClonados = rentabilidadCalculadoraRepository.findAll();

        // Crear un conjunto con los nombres de productos ya clonados para evitar duplicados
        Set<String> nombresProductosClonados = productosClonados.stream()
            .map(RentabilidadCalculadora::getNombreProducto)
            .collect(Collectors.toSet());

        for (Producto producto : productosExistentes) {
            // Solo clonar el producto si aún no está en la tabla rentabilidad_calculadora
            if (!nombresProductosClonados.contains(producto.getNombre())) {
                RentabilidadCalculadora nuevoProducto = new RentabilidadCalculadora();
                nuevoProducto.setNombreProducto(producto.getNombre());
                nuevoProducto.setPrecioCompra(producto.getPrecioCompra() != null ? producto.getPrecioCompra() : BigDecimal.ZERO);
                nuevoProducto.setPrecioVenta(producto.getPrecioVenta() != null ? producto.getPrecioVenta() : BigDecimal.ZERO);
                nuevoProducto.setCostoProduccion(producto.getCostosFijos() != null ? producto.getCostosFijos() : BigDecimal.ZERO);
                nuevoProducto.setPorcentajeRentabilidad(producto.getPorcentajeRentabilidad() != null ? producto.getPorcentajeRentabilidad() : BigDecimal.ZERO);
                nuevoProducto.setStock(producto.getStock()); // Clonando el valor de stock
                
                // Suponiendo que Producto tiene un método para obtener el desperdicio
                BigDecimal desperdicio = producto.getGrasaDesperdicio() != null ? producto.getGrasaDesperdicio() : BigDecimal.ZERO;
                nuevoProducto.setDesperdicio(desperdicio);

                // Calcular kilos netos
                BigDecimal kilosNetos = BigDecimal.valueOf(producto.getStock()).subtract(desperdicio);
                nuevoProducto.setKilosNetos(kilosNetos);

                // Guardar el nuevo producto
                rentabilidadCalculadoraRepository.save(nuevoProducto);
                System.out.println("Producto clonado: " + nuevoProducto.getNombreProducto());
            } else {
                System.out.println("Producto ya existe en rentabilidad_calculadora: " + producto.getNombre());
            }
        }

        return ResponseEntity.ok().body(Map.of("success", true));
    }

    @PostMapping("/actualizar")
    public String actualizarRentabilidad(RentabilidadCalculadora rentabilidadCalculadora) {
        // Calcular ganancia unitaria neta
        BigDecimal gananciaBruta = rentabilidadCalculadora.getPrecioVenta()
            .subtract(rentabilidadCalculadora.getPrecioCompra());

        BigDecimal costosTotales = rentabilidadCalculadora.getCostoInsumos()
            .add(rentabilidadCalculadora.getGastosAdicionales())
            .add(rentabilidadCalculadora.getCostoProduccion());

        BigDecimal kilosUtilizables = rentabilidadCalculadora.getKilosUtilizables();
        BigDecimal costoPorKilo = kilosUtilizables != null && kilosUtilizables.compareTo(BigDecimal.ZERO) > 0
            ? costosTotales.divide(kilosUtilizables, 2, RoundingMode.HALF_UP)
            : BigDecimal.ZERO;

        BigDecimal gananciaUnitNeta = gananciaBruta.subtract(costoPorKilo);
        rentabilidadCalculadora.setGananciaUnitariaNeta(gananciaUnitNeta);

        // Calcular rentabilidad neta como porcentaje
        if (rentabilidadCalculadora.getPrecioCompra().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal rentabilidadPorcentaje = gananciaUnitNeta
                .divide(rentabilidadCalculadora.getPrecioCompra(), 2, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            rentabilidadCalculadora.setPorcentajeRentabilidad(rentabilidadPorcentaje);
        } else {
            rentabilidadCalculadora.setPorcentajeRentabilidad(BigDecimal.ZERO);
        }

        // Guardar en la base de datos
        rentabilidadCalculadoraRepository.save(rentabilidadCalculadora);
        return "redirect:/rentabilidad-calculadora";
    }



    @GetMapping("/nueva-rentabilidad")
    public String mostrarFormulario(Model model) {
        model.addAttribute("rentabilidadCalculadora", new RentabilidadCalculadora());
        return "nueva_rentabilidad"; // Nombre de la plantilla Thymeleaf
    }

    @PostMapping
    public String guardarNuevaRentabilidad(RentabilidadCalculadora rentabilidadCalculadora) {
        if (rentabilidadCalculadora.getPrecioCompra().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = rentabilidadCalculadora.getPrecioVenta().subtract(rentabilidadCalculadora.getPrecioCompra());
            BigDecimal rentabilidadPorcentaje = margen.divide(rentabilidadCalculadora.getPrecioCompra(), 2, RoundingMode.HALF_UP)
                                                      .multiply(BigDecimal.valueOf(100));
            rentabilidadCalculadora.setPorcentajeRentabilidad(rentabilidadPorcentaje);
        } else {
            rentabilidadCalculadora.setPorcentajeRentabilidad(BigDecimal.ZERO);
        }

        // Guardar la nueva rentabilidad en la base de datos
        rentabilidadCalculadoraRepository.save(rentabilidadCalculadora);
        return "redirect:/rentabilidad-calculadora";
    }
}
