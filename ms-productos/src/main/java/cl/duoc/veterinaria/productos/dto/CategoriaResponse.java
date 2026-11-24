package cl.duoc.veterinaria.productos.dto;

import cl.duoc.veterinaria.productos.modelo.Categoria;

public record CategoriaResponse(Long id, String nombre, String descripcion, String imagen, long cantidadProductos) {

    public static CategoriaResponse desde(Categoria categoria, long cantidadProductos) {
        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.getDescripcion(),
                categoria.getImagen(), cantidadProductos);
    }
}
