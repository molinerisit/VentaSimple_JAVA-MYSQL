package com.gestionsimple.sistema_ventas.service.impl;

import com.gestionsimple.sistema_ventas.model.Categoria;
import com.gestionsimple.sistema_ventas.model.Producto;
import com.gestionsimple.sistema_ventas.repository.ProductoRepository;
import com.gestionsimple.sistema_ventas.service.ProductoService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImpl implements ProductoService {

    private static final Logger logger = LoggerFactory.getLogger(ProductoServiceImpl.class);

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
        logger.info("Guardando producto: {}", producto);
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public void actualizarProducto(Producto producto) {
        logger.info("Actualizando producto: {}", producto);
        producto.actualizarInversionTotal();
        productoRepository.save(producto);
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
        logger.info("Actualizando precio de compra para producto con ID: {}", id);
        Producto producto = productoRepository.findById(id).orElse(null);
        if (producto != null) {
            producto.setPrecioCompra(BigDecimal.valueOf(newPrecioCompra)); // Convertir double a BigDecimal
            productoRepository.save(producto);
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

}
