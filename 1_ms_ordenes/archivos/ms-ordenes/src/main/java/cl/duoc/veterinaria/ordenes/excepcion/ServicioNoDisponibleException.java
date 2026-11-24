package cl.duoc.veterinaria.ordenes.excepcion;

/** Se transforma en una respuesta 503 cuando ms-productos no responde */
public class ServicioNoDisponibleException extends RuntimeException {

    public ServicioNoDisponibleException(String mensaje) {
        super(mensaje);
    }
}
