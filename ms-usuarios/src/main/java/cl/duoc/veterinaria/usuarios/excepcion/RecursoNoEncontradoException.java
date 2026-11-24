package cl.duoc.veterinaria.usuarios.excepcion;

/** Se transforma en una respuesta 404 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
