package com.gestionsimple.sistema_ventas.service.impl;

import com.gestionsimple.sistema_ventas.model.Categoria;
import com.gestionsimple.sistema_ventas.model.HistorialPrecio;
import com.gestionsimple.sistema_ventas.model.Producto;
import com.gestionsimple.sistema_ventas.repository.HistorialPrecioRepository;
import com.gestionsimple.sistema_ventas.repository.ProductoRepository;
import com.gestionsimple.sistema_ventas.service.ProductoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoServiceImpl.class);

    @Autowired
    private HistorialPrecioRepository historialPrecioRepository;
    
    @Autowired
    private ProductoRepository productoRepository;

    @Override
    public List<Categoria> obtenerTodasLasCategorias() {
        logger.info("Obteniendo todas las categorías de productos");
        return productoRepository.findAll()
                .stream()
                .map(Producto::getCategoria)
                .filter(categoria -> categoria != null)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Producto> obtenerProductosPorCategoria(String categoriaNombre) {
        logger.info("Obteniendo productos por categoría: {}", categoriaNombre);
        Categoria categoria = new Categoria();
        categoria.setNombre(categoriaNombre);
        return productoRepository.findByCategoriaAndActivo(categoria, true);
    }
    
    @Override
    public Producto obtenerProductoPorId(Long id) {
        logger.info("Obteniendo producto por ID: {}", id);
        return productoRepository.findById(id).orElse(null);
    }
    
    
    @Override
    @Transactional
    public void guardarProducto(Producto producto) {
        if (producto.getId() == null) {
            if (producto.getCodigoDeBarras() != null && productoRepository.findByCodigoDeBarras(producto.getCodigoDeBarras()).isPresent()) {
                throw new RuntimeException("El código de barras ya está asociado a otro producto.");
            }
        }

        // Establecer precioCompra con valor predeterminado de 0 si no se proporciona
        if (producto.getPrecioCompraActual() != null) {
            producto.setPrecioCompra(producto.getPrecioCompraActual());
        } else {
            logger.warn("El precio de compra actual es null, usando el precio de compra predeterminado de 0.");
            producto.setPrecioCompra(BigDecimal.ZERO); // Valor predeterminado
        }

        logger.info("Guardando producto: {}", producto);
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void actualizarProducto(Producto producto) {
        logger.info("Actualizando producto: {}", producto);

        Producto productoExistente = productoRepository.findById(producto.getId())
            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Actualizando solo los campos necesarios
        productoExistente.setNombre(producto.getNombre());
        productoExistente.setDescripcion(producto.getDescripcion());
        productoExistente.setPrecioVenta(producto.getPrecioVenta());
        productoExistente.setStock(producto.getStock());
        productoExistente.setEsPesable(producto.getEsPesable());
        productoExistente.setGrasaDesperdicio(producto.getGrasaDesperdicio());
        productoExistente.setOtrosDesperdicios(producto.getOtrosDesperdicios());
        productoExistente.setCodigoDeBarras(producto.getCodigoDeBarras());
        productoExistente.setPorcentajeRentabilidad(producto.getPorcentajeRentabilidad());

        // Actualizar el precio de compra solo si es proporcionado
        if (producto.getPrecioCompra() != null) {
            productoExistente.actualizarPrecioCompra(producto.getPrecioCompra());
        }

        if (producto.getPrecioCompraActual() != null) {
            productoExistente.setPrecioCompraActual(producto.getPrecioCompraActual());
        }

        productoExistente.actualizarInversionTotal(productoExistente.getStock());

        productoRepository.save(productoExistente);
    }




    @Override
    public List<Producto> obtenerTodosLosProductos() {
        logger.info("Obteniendo todos los productos");
        return productoRepository.findAll();
    }

    @Override
    @Transactional
    public void eliminarProducto(Long id) {
        logger.info("Eliminando producto con ID: {}", id);
        productoRepository.deleteById(id);
    }

    @Override
    public void actualizarStock(Long id, int cantidad) {
        Producto producto = productoRepository.findById(id).orElseThrow(() -> new RuntimeException("Producto no encontrado"));
        int nuevoStock = producto.getStock() + cantidad; // Se suma la cantidad para aumentar o disminuir el stock
        if (nuevoStock < 0) {
            throw new RuntimeException("Stock insuficiente");
        }
        producto.setStock(nuevoStock);
        productoRepository.save(producto);
    }
    
    @Override
    @Transactional
    public void actualizarPrecioCompra(Long id, double newPrecioCompra) {
        logger.info("Actualizando precio de compra para el producto con ID: {}", id);
        Producto producto = productoRepository.findById(id).orElse(null);

        if (producto != null) {
            BigDecimal nuevoPrecio = BigDecimal.valueOf(newPrecioCompra);

            if (producto.getPrecioCompraActual() == null) {
                producto.setPrecioCompraAnterior(null);
                producto.setPrecioCompra(nuevoPrecio);
                producto.setPrecioCompraActual(nuevoPrecio);
            } else {
                producto.setPrecioCompraAnterior(producto.getPrecioCompra());
                producto.setPrecioCompra(producto.getPrecioCompraActual());
                producto.setPrecioCompraActual(nuevoPrecio);
            }

            productoRepository.save(producto);
            logger.info("Precios actualizados para el producto con ID: {}", id);
        } else {
            logger.error("Producto con ID {} no encontrado.", id);
            throw new RuntimeException("Producto no encontrado");
        }
    }

    
    @Override
    public boolean productoInvolucradoEnVenta(Long id) {
        // Implementación para verificar si un producto está involucrado en alguna venta
        logger.info("Verificando si el producto con ID {} está involucrado en alguna venta", id);
        // Lógica para verificar si el producto está en ventas
        return false;
    }

    @Override
    public List<Producto> buscarPorCategoriaYActivo(Categoria categoria, boolean activo) {
        logger.info("Buscando productos por categoría: {} y activo: {}", categoria.getNombre(), activo);
        return productoRepository.findByCategoriaAndActivo(categoria, activo);
    }
    
  
    public Optional<Producto> getProductoByCodigoDeBarras(String codigoDeBarras) {
        logger.info("Buscando producto con código de barras: {}", codigoDeBarras);

        // Búsqueda en el repositorio
        Optional<Producto> producto = productoRepository.findByCodigoDeBarras(codigoDeBarras);

        if (producto.isPresent()) {
            logger.info("Producto encontrado: {}", producto.get().getNombre());
        } else {
            logger.warn("Producto con código de barras {} no encontrado", codigoDeBarras);
        }

        return producto;
    }

    @Override
    public List<HistorialPrecio> obtenerHistorialPrecios(Long id) {
        logger.info("Obteniendo historial de precios para el producto con ID: {}", id);
        return historialPrecioRepository.findByProductoId(id); // Asegúrate de que el repositorio tenga este método
    }

	
   

}