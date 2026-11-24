package cl.duoc.veterinaria.productos.dto;

import cl.duoc.veterinaria.productos.modelo.Producto;

/** Producto con su precio final ya calculado por el backend */
public record ProductoResponse(
        Long id,
        String codigo,
        String nombre,
        String descripcion,
        Integer precio,
        Integer descuento,
        int precioFinal,
        Integer stock,
        Integer stockCritico,
        boolean stockCriticoAlcanzado,
        Long categoriaId,
        String categoriaNombre,
        String imagen) {

    public static ProductoResponse desde(Producto producto) {
        return new ProductoResponse(
                producto.getId(),
                producto.getCodigo(),
                producto.getNombre(),
                producto.getDescripcion() == null ? "" : producto.getDescripcion(),
                producto.getPrecio(),
                producto.getDescuento(),
                producto.precioFinal(),
                producto.getStock(),
                producto.getStockCritico(),
                producto.esStockCritico(),
                producto.getCategoria().getId(),
                producto.getCategoria().getNombre(),
                producto.getImagen());
    }
}
