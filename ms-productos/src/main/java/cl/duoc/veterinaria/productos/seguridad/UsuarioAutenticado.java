package cl.duoc.veterinaria.productos.seguridad;

/** Datos del usuario que vienen dentro del token JWT */
public record UsuarioAutenticado(Long id, String correo, String rol) {
}
