package cl.duoc.veterinaria.usuarios.excepcion;

import java.util.Map;

/** Se transforma en una respuesta 409 (por ejemplo: correo repetido). Puede indicar el campo afectado. */
public class ConflictoException extends RuntimeException {

    private final Map<String, String> errores;

    public ConflictoException(String mensaje) {
        super(mensaje);
        this.errores = Map.of();
    }

    public ConflictoException(String campo, String mensaje) {
        super(mensaje);
        this.errores = Map.of(campo, mensaje);
    }

    public Map<String, String> getErrores() {
        return errores;
    }
}
