package cl.duoc.veterinaria.usuarios.excepcion;

/** Se transforma en una respuesta 401 (credenciales o token de renovación inválidos) */
public class NoAutorizadoException extends RuntimeException {

    public NoAutorizadoException(String mensaje) {
        super(mensaje);
    }
}
