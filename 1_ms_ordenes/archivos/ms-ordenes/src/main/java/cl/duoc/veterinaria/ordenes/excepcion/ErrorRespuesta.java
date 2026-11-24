package cl.duoc.veterinaria.ordenes.excepcion;

import java.util.Map;

/** Formato único de los errores que devuelve la API */
public record ErrorRespuesta(int estado, String mensaje, Map<String, String> errores) {

    public static ErrorRespuesta de(int estado, String mensaje) {
        return new ErrorRespuesta(estado, mensaje, Map.of());
    }
}
