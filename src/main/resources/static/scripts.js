document.addEventListener('DOMContentLoaded', () => {
    const productosSeleccionados = [];

    document.querySelectorAll('.btn-producto').forEach(button => {
        button.addEventListener('click', () => {
            const productoId = button.getAttribute('data-id');
            const productoNombre = button.getAttribute('data-nombre');
            const productoPrecio = button.getAttribute('data-precio');

            const productoExistente = productosSeleccionados.find(p => p.id === productoId);
            if (productoExistente) {
                productoExistente.cantidad++;
            } else {
                productosSeleccionados.push({ id: productoId, nombre: productoNombre, precio: productoPrecio, cantidad: 1 });
            }

            renderizarProductosSeleccionados();
        });
    });

    function renderizarProductosSeleccionados() {
        const ul = document.getElementById('productos-seleccionados');
        ul.innerHTML = '';

        productosSeleccionados.forEach(producto => {
            const li = document.createElement('li');
            li.textContent = `${producto.nombre} - ${producto.cantidad} x $${producto.precio} = $${producto.cantidad * producto.precio}`;
            const btnEliminar = document.createElement('button');
            btnEliminar.textContent = 'Eliminar';
            btnEliminar.addEventListener('click', () => {
                producto.cantidad--;
                if (producto.cantidad === 0) {
                    const index = productosSeleccionados.indexOf(producto);
                    productosSeleccionados.splice(index, 1);
                }
                renderizarProductosSeleccionados();
            });
            li.appendChild(btnEliminar);
            ul.appendChild(li);
        });
    }

    document.getElementById('procesar-compra').addEventListener('click', () => {
        // Lógica para procesar la compra
        console.log('Compra procesada:', productosSeleccionados);
    });
});
