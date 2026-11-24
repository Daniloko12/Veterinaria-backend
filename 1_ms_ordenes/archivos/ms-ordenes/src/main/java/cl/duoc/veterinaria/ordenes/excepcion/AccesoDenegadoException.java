package cl.duoc.veterinaria.ordenes.excepcion;

/** Se transforma en una respuesta 403 (por ejemplo: ver la orden de otro cliente) */
public class AccesoDenegadoException extends RuntimeException {

    public AccesoDenegadoException(String mensaje) {
        super(mensaje);
    }
}
