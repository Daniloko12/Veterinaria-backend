package cl.duoc.veterinaria.usuarios.dto;

import cl.duoc.veterinaria.usuarios.modelo.Usuario;

/** Usuario tal como lo ve el frontend (nunca incluye la contraseña) */
public record UsuarioResponse(
        Long id,
        String run,
        String nombre,
        String apellidos,
        String correo,
        String fechaNacimiento,
        String rol,
        String region,
        String comuna,
        String direccion,
        boolean activo) {

    public static UsuarioResponse desde(Usuario usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getRun(),
                usuario.getNombre(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getFechaNacimiento() == null ? "" : usuario.getFechaNacimiento().toString(),
                usuario.getRol().aTexto(),
                usuario.getRegion(),
                usuario.getComuna(),
                usuario.getDireccion(),
                usuario.isActivo());
    }
}
