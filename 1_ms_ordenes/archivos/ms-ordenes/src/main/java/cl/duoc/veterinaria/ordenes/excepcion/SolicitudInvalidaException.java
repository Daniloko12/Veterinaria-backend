package cl.duoc.veterinaria.ordenes.excepcion;

import java.util.Map;

/** Se transforma en una respuesta 400 con el error de un campo */
public class SolicitudInvalidaException extends RuntimeException {

    private final Map<String, String> errores;

    public SolicitudInvalidaException(String campo, String mensaje) {
        super(mensaje);
        this.errores = Map.of(campo, mensaje);
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
