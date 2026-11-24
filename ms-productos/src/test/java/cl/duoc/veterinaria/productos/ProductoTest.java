package cl.duoc.veterinaria.productos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import cl.duoc.veterinaria.productos.modelo.Producto;

/** Pruebas unitarias de las reglas del modelo Producto */
class ProductoTest {

    private Producto producto(int precio, int descuento, int stock, int stockCritico) {
        Producto producto = new Producto();
        producto.setPrecio(precio);
        producto.setDescuento(descuento);
        producto.setStock(stock);
        producto.setStockCritico(stockCritico);
        return producto;
    }

    @Test
    void elPrecioFinalAplicaElDescuentoYRedondea() {
        assertEquals(2392, producto(2990, 20, 10, 5).precioFinal());
        assertEquals(6291, producto(6990, 10, 10, 5).precioFinal());
    }

    @Test
    void sinDescuentoElPrecioFinalEsElPrecio() {
        assertEquals(15990, producto(15990, 0, 10, 5).precioFinal());
    }

    @Test
    void detectaElStockCritico() {
        assertTrue(producto(1000, 0, 4, 5).esStockCritico());
        assertTrue(producto(1000, 0, 5, 5).esStockCritico());
        assertFalse(producto(1000, 0, 6, 5).esStockCritico());
    }
}
