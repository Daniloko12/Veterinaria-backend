package cl.duoc.veterinaria.usuarios.dto;

/**
 * Respuesta del login, del registro y de la renovación.
 * - tokenAcceso: JWT que se envía en la cabecera Authorization (dura pocos minutos).
 * - tokenRenovacion: sirve para pedir un tokenAcceso nuevo sin volver a iniciar sesión.
 */
public record TokenResponse(
        String tokenAcceso,
        String tokenRenovacion,
        String tipo,
        long expiraEnSegundos,
        SesionUsuario usuario) {
}
