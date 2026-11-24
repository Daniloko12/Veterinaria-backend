package cl.duoc.veterinaria.usuarios.dto;

import cl.duoc.veterinaria.usuarios.modelo.Usuario;

/** Datos mínimos que el frontend guarda de la sesión */
public record SesionUsuario(Long id, String nombre, String apellidos, String correo, String rol) {

    public static SesionUsuario desde(Usuario usuario) {
        return new SesionUsuario(usuario.getId(), usuario.getNombre(), usuario.getApellidos(),
                usuario.getCorreo(), usuario.getRol().aTexto());
    }
}
