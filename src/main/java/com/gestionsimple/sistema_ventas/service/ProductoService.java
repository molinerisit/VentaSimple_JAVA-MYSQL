package com.gestionsimple.sistema_ventas.service;

import com.gestionsimple.sistema_ventas.model.Categoria;
import com.gestionsimple.sistema_ventas.model.HistorialPrecio;
import com.gestionsimple.sistema_ventas.model.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoService {
    List<Categoria> obtenerTodasLasCategorias(); // Cambiar a List<Categoria>
    List<Producto> obtenerProductosPorCategoria(String categoria);
    Producto obtenerProductoPorId(Long id);
    void actualizarProducto(Producto producto);
    void guardarProducto(Producto producto);
    List<Producto> obtenerTodosLosProductos();
    void eliminarProducto(Long id);
    void actualizarPrecioCompra(Long id, double newPrecioCompra);
    boolean productoInvolucradoEnVenta(Long id); // Nuevo método
	List<Producto> buscarPorCategoriaYActivo(Categoria categoria, boolean activo);
    List<HistorialPrecio> obtenerHistorialPrecios(Long id);
	Optional<Producto> getProductoByCodigoDeBarras(String codigoDeBarras);
    void actualizarStock(Long id, double cantidad);  // Cambiar int por double
}


